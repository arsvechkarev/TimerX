package timerx;

import static timerx.util.Checker.expect;
import static timerx.util.Checker.expectNotNull;
import static timerx.util.Checker.expectTimeInitialized;

import androidx.annotation.NonNull;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import timerx.format.Analyzer;
import timerx.format.Semantic;
import timerx.util.Constants.TimeValues;

public class TimerBuilder {

  private Semantic startSemantic;
  private long startTime = TimeValues.NONE;
  private TimeTickListener tickListener;
  private TimeFinishListener finishListener;
  private SortedSet<NextFormatsHolder> nextFormatsHolder = new TreeSet<>(
      Collections.reverseOrder());
  private SortedSet<ActionsHolder> nextActionsHolder = new TreeSet<>(
      Collections.reverseOrder());

  public TimerBuilder startFormat(String format) {
    expectNotNull(format);
    this.startSemantic = Analyzer.check(format);
    return this;
  }

  public TimerBuilder startTime(long time, TimeUnit timeUnit) {
    startTime = timeUnit.toMillis(time);
    return this;
  }

  public TimerBuilder onTick(@NonNull TimeTickListener tickListener) {
    expectNotNull(tickListener);
    this.tickListener = tickListener;
    return this;
  }

  public TimerBuilder onFinish(@NonNull TimeFinishListener finishListener) {
    expectNotNull(finishListener);
    this.finishListener = finishListener;
    return this;
  }

  public TimerBuilder changeFormatWhen(long time, TimeUnit timeUnit,
      @NonNull String newFormat) {
    expect(time >= 0, "Time should be positive");
    expectNotNull(newFormat);
    Semantic semantic = Analyzer.check(newFormat);
    long millis = timeUnit.toMillis(time);
    nextFormatsHolder.add(new NextFormatsHolder(millis, semantic));
    return this;
  }

  public TimerBuilder actionWhen(long time, TimeUnit timeUnit,
      @NonNull Action action) {
    expect(time >= 0, "Time should be positive");
    expectNotNull(action);
    long millis = timeUnit.toMillis(time);
    nextActionsHolder.add(new ActionsHolder(millis, action));
    return this;
  }

  public Timer build() {
    expectNotNull(startSemantic, "Start format should be initialized");
    expectTimeInitialized(startTime, "Time should be initialized");
    return new Timer(startTime, startSemantic, tickListener, finishListener,
        nextFormatsHolder,
        nextActionsHolder);
  }
}
