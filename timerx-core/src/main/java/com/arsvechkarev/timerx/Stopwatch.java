package com.arsvechkarev.timerx;

import static com.arsvechkarev.timerx.Checker.assertThat;
import static com.arsvechkarev.timerx.TimeCountingStateState.INACTIVE;
import static com.arsvechkarev.timerx.TimeCountingStateState.PAUSED;
import static com.arsvechkarev.timerx.TimeCountingStateState.RESUMED;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import androidx.annotation.NonNull;
import com.arsvechkarev.timerx.format.Analyzer;
import com.arsvechkarev.timerx.format.Semantic;
import com.arsvechkarev.timerx.format.TimeFormatter;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Class for standard time counting
 *
 * @author Arseny Svechkarev
 */
public class Stopwatch {

  private static final String TAG = Stopwatch.class.getSimpleName();

  /**
   * Message id for {@link #handler}
   */
  private static final int MSG = 2;
  /**
   * Listener for sending formatted time
   *
   * @see StopwatchListener
   */
  private final StopwatchListener tickListener;
  /**
   * Semantic of base format
   */
  private final Semantic startSemantic;
  /**
   * Current time of stopwatch (in millis)
   */
  private long currentTime;
  /**
   * Time when stopwatch started (in millis)
   */
  private long baseTime;
  /**
   * Delay for handler in millis
   */
  private long delay;

  private boolean notStartedYet = true;

  /**
   * Current state of stopwatch
   *
   * @see TimeCountingStateState
   */
  private TimeCountingStateState state = INACTIVE;
  /**
   * Formatter for formatting {@link #currentTime} according to particular format
   *
   * @see TimeFormatter
   */
  private TimeFormatter timeFormatter;
  /**
   * Set of formats which will be applied when {@link #currentTime} will be equal to time
   * that holder contains
   */
  private SortedSet<NextFormatsHolder> formatsHolder = new TreeSet<>();

  // Copy of formats holder to delete it elements after format will pass
  private SortedSet<NextFormatsHolder> copyOfFormatsHolder;

  /**
   * Main constructor
   *
   * @param tickListener Listener for sending formatted time
   * @param format Started format
   */
  public Stopwatch(StopwatchListener tickListener, String format) {
    this.tickListener = tickListener;
    this.startSemantic = Analyzer.check(format);
    applyFormat(startSemantic);
  }

  /**
   * Schedules changing format at certain time. Format will be applied as soon as time
   * comes. This method can be invokes many times, all received formats will be scheduled.
   * Invoking with same time schedules only <b>first</b> invocation. Examples:
   * <pre>
   *  // Creating stopwatch with start format "SS:LL"
   *  Stopwatch stopwatch = new Stopwatch(new StopwatchListener() {...}, "SS:LL")
   *
   *  // When time will be equals to 1 minute, then format will change to "M:SS:LL"
   *  stopwatch.changeFormatWhen(1, TimeUnits.MINUTES, "M:SS:LL")
   *
   *  // When time will be equals to 10 minutes, then format will change to "MM:SS:LL"
   *  stopwatch.changeFormatWhen(10, TimeUnits.MINUTES, "MM:SS:LL")
   *
   *  // Code below will be ignored, because time we already have format will be applied
   *  // when time will be equals to 10 minutes
   *  stopwatch.changeFormatWhen(10, TimeUnits.MINUTES, "HH:MM:SS:LL")
   * </pre>
   */
  public Stopwatch changeFormatWhen(long time, @NonNull TimeUnits timeUnitType,
      @NonNull String newFormat) {
    if (notStartedYet) {
      Semantic semantic = Analyzer.check(newFormat);
      long millis = Utils.millisOf(time, timeUnitType);
      formatsHolder.add(new NextFormatsHolder(millis, semantic));
      return this;
    }
    throw new IllegalStateException(
        "Stopwatch already started, but formats still adding");
  }

  /**
   * Starts timer if timer
   */
  public void start() {
    notStartedYet = false;
    if (state != RESUMED) {
      if (state == INACTIVE) {
        copyOfFormatsHolder = new TreeSet<>(formatsHolder);
        applyFormat(startSemantic);
        baseTime = SystemClock.elapsedRealtime();
      } else {
        assertThat(state == PAUSED);
        baseTime = SystemClock.elapsedRealtime() - currentTime;
      }
      handler.sendMessage(handler.obtainMessage(MSG));
      state = RESUMED;
    }
  }

  public void stop() {
    state = PAUSED;
    handler.removeMessages(MSG);
  }

  public void reset() {
    currentTime = 0;
    baseTime = 0;
    state = INACTIVE;
    notStartedYet = true;
    handler.removeMessages(MSG);
  }

  public long getTime() {
    return currentTime;
  }

  private void applyFormat(Semantic semantic) {
    timeFormatter = new TimeFormatter(semantic);
    delay = timeFormatter.getOptimizedInterval();
  }

  @SuppressLint("HandlerLeak")
  private final Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      synchronized (Stopwatch.this) {
        long executionStartedTime = SystemClock.elapsedRealtime();
        currentTime = SystemClock.elapsedRealtime() - baseTime;
        Log.d(TAG, "handleMessage: currentTime = " + currentTime);
        changeFormatIfNeed();
        String format = timeFormatter.format(currentTime);
        Log.d(TAG, "handleMessage: format = " + format);
        tickListener.onTimeTick(format);
        long executionDelay = SystemClock.elapsedRealtime() - executionStartedTime;
        sendMessageDelayed(obtainMessage(MSG), delay - executionDelay);
      }
    }
  };

  private void changeFormatIfNeed() {
    if (copyOfFormatsHolder.size() > 0
        && !timeFormatter.currentFormat()
        .equals(copyOfFormatsHolder.first().getSemantic().getFormat())
        && currentTime >= copyOfFormatsHolder.first().getMillis()) {
      applyFormat(copyOfFormatsHolder.first().getSemantic());
      copyOfFormatsHolder.remove(copyOfFormatsHolder.first());
    }
  }
}
