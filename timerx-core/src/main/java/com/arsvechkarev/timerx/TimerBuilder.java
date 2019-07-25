package com.arsvechkarev.timerx;

import com.arsvechkarev.timerx.Constants.TimeValues;
import com.arsvechkarev.timerx.format.Analyzer;
import com.arsvechkarev.timerx.format.Semantic;
import java.util.SortedSet;
import java.util.TreeSet;

public class TimerBuilder {

  private Semantic semantic;
  private long startTime = TimeValues.NONE;
  private TimerTickListener tickListener = null;

  private SortedSet<NextFormatsHolder> formatsHolder = new TreeSet<>();

  public TimerBuilder(String format) {
    this.semantic = Analyzer.check(format);
  }

  public TimerBuilder setStartTime(long time, TimeUnits unitType) {
    startTime = Utils.millisOf(time, unitType);
    return this;
  }

  public TimerBuilder setTickListener(TimerTickListener tickListener) {
    this.tickListener = tickListener;
    return this;
  }


  public TimerBuilder changeFormatWhen(long time, TimeUnits timeUnitType,
      String newFormat) {
    Semantic semantic = Analyzer.check(newFormat);
    long millis = Utils.millisOf(time, timeUnitType);
    formatsHolder.add(new NextFormatsHolder(millis, semantic));
    return this;
  }

  public Timer build() {
    return new Timer(tickListener, semantic, formatsHolder, startTime);
  }
}
