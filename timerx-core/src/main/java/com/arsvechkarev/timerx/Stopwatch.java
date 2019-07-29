package com.arsvechkarev.timerx;

import static com.arsvechkarev.timerx.TimeCountingState.INACTIVE;
import static com.arsvechkarev.timerx.TimeCountingState.PAUSED;
import static com.arsvechkarev.timerx.TimeCountingState.RESUMED;
import static com.arsvechkarev.timerx.util.Checker.expect;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import com.arsvechkarev.timerx.format.Semantic;
import com.arsvechkarev.timerx.format.TimeFormatter;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**
 * Represents a standard stopwatch
 *
 * @author Arseny Svechkarev
 * @see StopwatchBuilder
 * @see Timer
 */
public class Stopwatch {

  /**
   * Message id for {@link #handler}
   */
  private static final int MSG = 2;
  /**
   * Listener for sending formatted time
   *
   * @see TimeTickListener
   */
  private final TimeTickListener tickListener;
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

  /**
   * Current state of stopwatch
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
   * Set of formats which will be applied when {@link #currentTime} will be equal to time
   * that holder contains
   */
  private final SortedSet<NextFormatsHolder> nextFormatsHolder;
  private final SortedSet<ActionsHolder> actionsHolder;

  // Copy of formats holder to delete it elements after format will pass
  private SortedSet<NextFormatsHolder> copyOfFormatsHolder;
  private SortedSet<ActionsHolder> copyOfActionsHolder;

  @RestrictTo(Scope.LIBRARY)
  Stopwatch(Semantic startSemantic, TimeTickListener tickListener,
      SortedSet<NextFormatsHolder> nextFormatsHolder,
      SortedSet<ActionsHolder> actionsHolder) {
    this.startSemantic = startSemantic;
    this.tickListener = tickListener;
    this.nextFormatsHolder = nextFormatsHolder;
    this.actionsHolder = actionsHolder;
  }

  public String getFormattedStartTime() {
    return new TimeFormatter(startSemantic).format(0L);
  }

  /**
   * Starts timer if timer
   */
  public void start() {
    if (state != RESUMED) {
      if (state == INACTIVE) {
        copyOfFormatsHolder = new TreeSet<>(nextFormatsHolder);
        copyOfActionsHolder = new TreeSet<>(actionsHolder);
        applyFormat(startSemantic);
        baseTime = SystemClock.elapsedRealtime();
      } else {
        expect(state == PAUSED);
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
    handler.removeMessages(MSG);
  }

  public long getTimeIn(TimeUnit timeUnit) {
    return timeUnit.convert(currentTime, TimeUnit.MILLISECONDS);
  }

  private void applyFormat(Semantic semantic) {
    timeFormatter = new TimeFormatter(semantic);
    delay = timeFormatter.getOptimizedDelay();
  }

  @SuppressLint("HandlerLeak")
  private final Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      synchronized (Stopwatch.this) {
        long executionStartedTime = SystemClock.elapsedRealtime();
        currentTime = SystemClock.elapsedRealtime() - baseTime;
        changeFormatIfNeed();
        makeActionIfNeed();
        String format = timeFormatter.format(currentTime);
        tickListener.onTick(format);
        long executionDelay = SystemClock.elapsedRealtime() - executionStartedTime;
        sendMessageDelayed(obtainMessage(MSG), delay - executionDelay);
      }
    }
  };

  private void makeActionIfNeed() {
    if (copyOfActionsHolder.size() > 0
        && currentTime >= copyOfActionsHolder.first().getMillis()) {
      copyOfActionsHolder.first().getAction().execute();
      copyOfActionsHolder.remove(copyOfActionsHolder.first());
    }
  }

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
