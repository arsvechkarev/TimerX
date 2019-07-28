package com.arsvechkarev.timerx;

import static com.arsvechkarev.timerx.util.Checker.expectNotNull;

import androidx.annotation.NonNull;
import com.arsvechkarev.timerx.format.Analyzer;
import com.arsvechkarev.timerx.format.Semantic;
import com.arsvechkarev.timerx.util.Constants.TimeValues;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class TimerBuilder {

  private Semantic startSemantic;
  private long startTime = TimeValues.NONE;
  private TimerTickListener tickListener = null;
  private SortedSet<NextFormatsHolder> nextFormatsHolder = new TreeSet<>(
      Collections.<NextFormatsHolder>reverseOrder());
  private SortedSet<ActionsHolder> nextActionsHolder = new TreeSet<>(
      Collections.<ActionsHolder>reverseOrder());

  public TimerBuilder startFormat(String format) {
    expectNotNull(format);
    this.startSemantic = Analyzer.check(format);
    return this;
  }

  public TimerBuilder startTime(long time, TimeUnit timeUnit) {
    startTime = timeUnit.toMillis(time);
    return this;
  }

  public TimerBuilder tickListener(@NonNull TimerTickListener tickListener) {
    expectNotNull(tickListener);
    this.tickListener = tickListener;
    return this;
  }

  public TimerBuilder changeFormatWhen(long time, TimeUnit timeUnit,
      @NonNull String newFormat) {
    expectNotNull(newFormat);
    Semantic semantic = Analyzer.check(newFormat);
    long millis = timeUnit.toMillis(time);
    nextFormatsHolder.add(new NextFormatsHolder(millis, semantic));
    return this;
  }

  public TimerBuilder actionWhen(long time, TimeUnit timeUnit,
      @NonNull Action action) {
    expectNotNull(action);
    long millis = timeUnit.toMillis(time);
    nextActionsHolder.add(new ActionsHolder(millis, action));
    return this;
  }

  public Timer build() {
    return new Timer(startTime, startSemantic, tickListener, nextFormatsHolder,
        nextActionsHolder);
  }
}
