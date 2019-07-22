package com.arsvechkarev.timerx;

import com.arsvechkarev.timerx.Constants.TimeValues;
import com.arsvechkarev.timerx.format.Analyzer;
import com.arsvechkarev.timerx.format.Semantic;
import java.util.SortedSet;
import java.util.TreeSet;

public class TimerBuilder {

  private String format;
  private long startTime = TimeValues.NONE;
  private long interval = TimeValues.MILLIS_IN_SECOND;
  private TimeTickListener timeTickListener = null;

  /**
   * Set of formats to change
   */
  private SortedSet<MillisSemanticHolder> keyTimes = new TreeSet<>();

  private TimerBuilder(String format) {
    this.format = format;
  }

  public static TimerBuilder ofFormat(String format) {
    return new TimerBuilder(format);
  }

  public TimerBuilder setStartTime(long time, TimeUnits unitType) {
    startTime = Utils.millisOf(time, unitType);
    return this;
  }

  public TimerBuilder setInterval(long time, TimeUnits unitType) {
    interval = Utils.millisOf(time, unitType);
    return this;
  }

  public TimerBuilder setTickListener(TimeTickListener timeTickListener) {
    this.timeTickListener = timeTickListener;
    return this;
  }


  public TimerBuilder changeFormatWhen(long time, TimeUnits timeUnitType,
      String newFormat) {
    Semantic semantic = Analyzer.checkFormat(newFormat);
    long millis = Utils.millisOf(time, timeUnitType);
    keyTimes.add(new MillisSemanticHolder(millis, semantic));
    return this;
  }

  public Timer build() {
    Utils.checkNotNull(timeTickListener, "Time tick listener can't be null");
    if (timeTickListener == null) {
      throw new IllegalStateException("Time tick listener can't be null");
    }
    if (startTime == TimeValues.NONE) {
      throw new IllegalStateException("Start time is not initialized");
    }
    return new Timer(timeTickListener, format, keyTimes, startTime, interval);
  }
}
