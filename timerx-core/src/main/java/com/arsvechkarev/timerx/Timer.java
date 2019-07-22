package com.arsvechkarev.timerx;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import java.util.SortedSet;

public class Timer {

  private final int MSG = 888;

  private long startTime;
  private long interval;
  private long currentTime;
  private TimeTickListener timeTickListener;
  private String format;
  private SortedSet<MillisSemanticHolder> keyTimes;

  Timer(TimeTickListener timeTickListener, String format,
      SortedSet<MillisSemanticHolder> keyTimes, long startTime, long interval) {
    Utils.checkNotNull(timeTickListener, "");
    Utils.checkNotNull(format, "");
    Utils.checkNotNull(keyTimes, "");
    Utils.checkInitialized(startTime, "");
    Utils.checkInitialized(interval, "");
    this.timeTickListener = timeTickListener;
    this.format = format;
    this.keyTimes = keyTimes;
    this.startTime = startTime;
    this.interval = interval;
  }

  public void start() {

  }

  public void stop() {

  }

  @SuppressLint("HandlerLeak")
  private final Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {

    }
  };

}
