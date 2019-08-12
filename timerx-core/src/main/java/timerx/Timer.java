package timerx;

import static timerx.TimeCountingState.INACTIVE;
import static timerx.TimeCountingState.PAUSED;
import static timerx.TimeCountingState.RESUMED;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import timerx.format.Semantic;
import timerx.format.TimeFormatter;

/**
 * Represents standard timer with base functions like {@link #start() start}, {@link
 * #stop() stop}, etc. Also allows changing time format and executing actions by a
 * specific time. Use {@link TimerBuilder} to configure and instantiate the stopwatch.
 * <p>Example of usage:</p>
 * <pre>
 *   Timer timer = new timerBuilder()
 *         // Set start format of time
 *         .startFormat("SS:LL")
 *         // Set tick listener that receives formatted time
 *         .tickListener(time -> textViewTime.setText(time))
 *         // Executing the action at a certain time
 *         .actionWhen(30, TimeUnit.SECONDS, () -> {
 *            Toast.makeText(context, "30 seconds past!", Toast.LENGTH_SHORT).show();
 *         })
 *         // When time will be equal to one minute, change format to "MM:SS:LL"
 *         .changeFormatWhen(1, TimeUnit.MINUTES, "MM:SS:LL")
 *         .build();
 *
 *   timer.start();
 *   timer.stop();
 *   timer.start();
 *   timer.stop();
 *   long resultMillis = timer.getRemainingTimeIn(TimeUnit.MILLISECONDS);
 *   timer.reset();
 * </pre>
 *
 * @author Arseny Svechkarev
 * @see TimerBuilder
 */
public class Timer {

  /**
   * Message id for {@link #handler}
   */
  private final int MSG = 3;

  // Start time in milliseconds
  private long startTime;

  // Current time (left time) in millis
  private long currentTime;

  // Millis when timer should stop
  private long millisInFuture;

  // Interval for timer
  private long interval;

  /**
   * Listener to notify about changing time
   *
   * @see TimeTickListener
   */
  private TimeTickListener tickListener;

  /**
   * Listener to notify user when timer finishes counting
   */
  private TimeFinishListener finishListener;

  // Semantic of start format
  private Semantic startSemantic;

  /**
   * Current state of timer
   *
   * @see TimeCountingState
   */
  private TimeCountingState state = INACTIVE;

  /**
   * Formatter for formatting {@link #currentTime} according to particular format
   *
   * @see TimeFormatter
   */
  private TimeFormatter timeFormatter;

  /**
   * Time formats to be applied in future
   */
  private final SortedSet<NextFormatsHolder> nextFormatsHolders;

  /**
   * Actions to execute in future
   */
  private final SortedSet<ActionsHolder> nextActionsHolders;

  private SortedSet<NextFormatsHolder> copyOfFormatsHolders;
  private SortedSet<ActionsHolder> copyOfActionsHolders;

  @RestrictTo(Scope.LIBRARY)
  Timer(long startTime, Semantic startSemantic, TimeTickListener tickListener,
      TimeFinishListener finishListener, SortedSet<NextFormatsHolder> nextFormatsHolders,
      SortedSet<ActionsHolder> nextActionsHolders) {
    this.startTime = startTime;
    this.startSemantic = startSemantic;
    this.tickListener = tickListener;
    this.finishListener = finishListener;
    this.nextFormatsHolders = nextFormatsHolders;
    this.nextActionsHolders = nextActionsHolders;
    currentTime = startTime;
  }

  /**
   * Returns start time formatted according to start format<br/> For example, if start
   * format is "MM:SS.LL", and start time is 10 minutes then result is "10:00.00"
   */
  public String getFormattedStartTime() {
    return new TimeFormatter(startSemantic).format(startTime);
  }

  /**
   * Depending on state of timer there are three possible variants of behaviour:
   * <p> - If timer hasn't started yet or {@link #reset()} was called, then
   * timer normally starts</p>
   * <p> - If timer has been started, and method {@link #stop()} was called, then
   * timer continues time counting</p>
   * <p> - If timer is running now, then the method does nothing</p>
   */
  public void start() {
    if (state != RESUMED) {
      if (state == INACTIVE) {
        currentTime = startTime;
        copyOfFormatsHolders = new TreeSet<>(nextFormatsHolders);
        copyOfActionsHolders = new TreeSet<>(nextActionsHolders);
        applyFormat(startSemantic);
        millisInFuture = SystemClock.elapsedRealtime() + startTime;
      } else {
        millisInFuture = SystemClock.elapsedRealtime() + currentTime;
      }
      handler.sendMessage(handler.obtainMessage(MSG));
      state = RESUMED;
    }
  }

  /**
   * Stops timer if it is active
   */
  public void stop() {
    state = PAUSED;
    handler.removeMessages(MSG);
  }

  /**
   * Stops timer and resets time to zero
   */
  public void reset() {
    state = INACTIVE;
    handler.removeMessages(MSG);
    applyFormat(startSemantic);
  }

  /**
   * Returns remaining time in particular unit
   */
  public long getRemainingTimeIn(TimeUnit timeUnit) {
    long timeToFormat = currentTime + timeFormatter.minimumUnitInMillis();
    return (currentTime > 0)
        ? timeUnit.convert(timeToFormat, TimeUnit.MILLISECONDS)
        : 0;
  }

  private void applyFormat(Semantic semantic) {
    timeFormatter = new TimeFormatter(semantic);
    interval = timeFormatter.getOptimizedDelay();
  }

  @SuppressLint("HandlerLeak")
  private final Handler handler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      synchronized (Timer.this) {
        long startExecution = SystemClock.elapsedRealtime();
        currentTime = millisInFuture - SystemClock.elapsedRealtime();
        changeFormatIfNeed();
        makeActionIfNeed();
        if (currentTime <= 0) {
          currentTime = 0;
          tickListener.onTick(timeFormatter.format(currentTime));
          finishListener.onFinish();
          reset();
          return;
        }
        long timeToFormat;
        if (currentTime == startTime) {
          timeToFormat = currentTime;
        } else {
          timeToFormat = currentTime + timeFormatter.minimumUnitInMillis();
        }
        String formattedTime = timeFormatter.format(timeToFormat);
        tickListener.onTick(formattedTime);
        long executionTime = SystemClock.elapsedRealtime() - startExecution;
        sendMessageDelayed(obtainMessage(MSG), interval - executionTime);
      }
    }
  };

  private void changeFormatIfNeed() {
    if (copyOfFormatsHolders.size() > 0
        && !timeFormatter.currentFormat()
        .equals(copyOfFormatsHolders.first().getSemantic().getFormat())
        && currentTime <= copyOfFormatsHolders.first().getMillis()) {
      applyFormat(copyOfFormatsHolders.first().getSemantic());
      copyOfFormatsHolders.remove(copyOfFormatsHolders.first());
    }
  }

  private void makeActionIfNeed() {
    if (copyOfActionsHolders.size() > 0
        && currentTime <= copyOfActionsHolders.first().getMillis()) {
      copyOfActionsHolders.first().getAction().run();
      copyOfActionsHolders.remove(copyOfActionsHolders.first());
    }
  }
}




