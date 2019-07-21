package com.arsvechkarev.timerx;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import com.arsvechkarev.timerx.format.Semantic;
import com.arsvechkarev.timerx.format.TimeFormatter;
import com.arsvechkarev.timerx.format.Validator;

/**
 * Class for counting time in standard order
 *
 * @author Arseny Svechkarev
 */
public class Stopwatch {

  /**
   * Message id for {@link #handler}
   */
  private static final int MSG = 1;

  /**
   * Current time check stopwatch in millis
   */
  private long currentTime;

  /**
   * Started time check stopwatch in millis
   */
  private long baseTime;

  /**
   * Current state check timer
   */
  private TimerState state = TimerState.INACTIVE;

  private final TimeTickListener tickListener;
  private final TimeFormatter formatter;

  /**
   * Delay time for handler in millis
   */
  private long delay;

  public Stopwatch(TimeTickListener tickListener, String parseFormat) {
    this.tickListener = tickListener;
    Semantic semantic = new Semantic(parseFormat);
    Validator.check(semantic);
    formatter = new TimeFormatter(semantic);
    delay = formatter.getOptimizedDelay();
  }

  public void start() {
    if (state != TimerState.ACTIVE) {
      baseTime = (state == TimerState.INACTIVE)
          // start timer again
          ? SystemClock.elapsedRealtime()
          // resume timer
          : SystemClock.elapsedRealtime() - currentTime;
      handler.sendMessage(handler.obtainMessage(MSG));
      state = TimerState.ACTIVE;
    }
  }

  public void stop() {
    state = TimerState.PAUSED;
    handler.removeMessages(MSG);
  }

  public void reset() {
    currentTime = 0;
    baseTime = 0;
    state = TimerState.INACTIVE;
    handler.removeMessages(MSG);
  }

  public long getTime() {
    return currentTime;
  }

  @SuppressLint("HandlerLeak")
  private final Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      synchronized (Stopwatch.this) {
        long executionStartedTime = SystemClock.elapsedRealtime();
        currentTime = SystemClock.elapsedRealtime() - baseTime;
        tickListener.onTimeTick(formatter.format(currentTime));
        long executionDelay = SystemClock.elapsedRealtime() - executionStartedTime;
        sendMessageDelayed(obtainMessage(MSG), delay - executionDelay);
      }
    }
  };
}
