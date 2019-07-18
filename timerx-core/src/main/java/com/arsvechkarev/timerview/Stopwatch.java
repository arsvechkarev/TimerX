package com.arsvechkarev.timerview;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/**
 * Class for counting time
 */
public class Stopwatch {

  /**
   * Message id for {@link #handler}
   */
  private static final int MSG = 29;

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

  public Stopwatch(TimeTickListener tickListener, String parseFormat) {
    this.tickListener = tickListener;
    formatter = new TimeFormatter(parseFormat);
  }

  public void start() {
    if (state != TimerState.ACTIVE) {
      // if timer paused or active
      baseTime = (state == TimerState.INACTIVE)
          // first timer start
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
        currentTime = SystemClock.elapsedRealtime() - baseTime;
        tickListener.onTimeTick(formatter.format(currentTime));
        sendMessageDelayed(obtainMessage(MSG), 1);
      }
    }
  };
}
