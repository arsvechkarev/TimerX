package com.arsvechkarev.timerx;

import static com.arsvechkarev.timerx.util.Checker.expectNotNull;

import androidx.annotation.NonNull;
import com.arsvechkarev.timerx.format.Analyzer;
import com.arsvechkarev.timerx.format.Semantic;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class StopwatchBuilder {

  private Semantic startSemantic;
  private StopwatchTickListener tickListener;
  private SortedSet<NextFormatsHolder> nextFormatsHolder = new TreeSet<>();
  private SortedSet<ActionsHolder> actionsHolder = new TreeSet<>();

  public StopwatchBuilder startFormat(String format) {
    startSemantic = Analyzer.check(format);
    return this;
  }

  public StopwatchBuilder tickListener(StopwatchTickListener tickListener) {
    expectNotNull(tickListener);
    this.tickListener = tickListener;
    return this;
  }

  /**
   * Schedules changing format at certain time. Format will be applied as soon as time
   * comes. This method can be invokes many times, all received formats will be scheduled.
   * Invoking with same time schedules only <b>first</b> invocation. Examples:
   * <pre>
   *  // Creating stopwatch with start format "SS:LL"
   *  Stopwatch stopwatch = new Stopwatch(new StopwatchTickListener() {...}, "SS:LL")
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
  public StopwatchBuilder changeFormatWhen(long time, TimeUnit timeUnit,
      @NonNull String newFormat) {
    expectNotNull(newFormat);
    Semantic semantic = Analyzer.check(newFormat);
    long millis = timeUnit.toMillis(time);
    nextFormatsHolder.add(new NextFormatsHolder(millis, semantic));
    return this;
  }

  public StopwatchBuilder actionWhen(long time, TimeUnit timeUnit,
      @NonNull Runnable action) {
    expectNotNull(action);
    long millis = timeUnit.toMillis(time);
    actionsHolder.add(new ActionsHolder(millis, action));
    return this;
  }

  public Stopwatch build() {
    return new Stopwatch(startSemantic, tickListener, nextFormatsHolder, actionsHolder);
  }
}
