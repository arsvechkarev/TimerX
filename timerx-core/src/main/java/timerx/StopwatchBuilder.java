package timerx;

import static timerx.util.Checker.expectNotNull;
import static timerx.util.Checker.expectTimeNotNegative;

import androidx.annotation.NonNull;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import timerx.format.Analyzer;
import timerx.format.Semantic;
import timerx.format.TimeFormatter;

/**
 * Builder to configure and instantiate {@link Stopwatch}.<br/> Usage example:
 * <pre>
 *  Stopwatch stopwatch = new StopwatchBuilder()
 *          // Set start format to stopwatch. Format syntax explaining {@link TimeFormatter here}
 *          .startFormat("SS:LL")
 *          // When time will be one minute, format changes to "MM:SS"
 *          .changeFormatWhen(1, TimeUnit.MINUTES, "MM:SS")
 *          // Set time tick listener
 *          .tickListener(new TimeTickListener() {
 *           {@literal @Override}
 *            public void onTick(String time) {
 *              textView.setText(time);
 *            }
 *          })
 *          // Executing an action at a certain time
 *          .actionWhen(30, TimeUnit.SECONDS, new Action() {
 *          {@literal @Override}
 *            public void run() {
 *              Toast.makeText(context, "30 seconds past!", Toast.LENGTH_SHORT).show();
 *            }
 *          })
 *          .build();
 * </pre>
 *
 * @see Stopwatch
 */
public class StopwatchBuilder {

  private Semantic startSemantic;
  private TimeTickListener tickListener;
  private final SortedSet<NextFormatsHolder> nextFormatsHolder = new TreeSet<>();
  private final SortedSet<ActionsHolder> actionsHolder = new TreeSet<>();

  /**
   * Set start time format to stopwatch
   */
  public StopwatchBuilder startFormat(@NonNull String format) {
    startSemantic = Analyzer.check(format);
    return this;
  }

  /**
   * Set tick listener to receive formatted time
   */
  public StopwatchBuilder tickListener(@NonNull TimeTickListener tickListener) {
    this.tickListener = tickListener;
    return this;
  }

  /**
   * Schedules changing format at certain time. Format will be applied as soon as time
   * comes. This method can be invokes many times, all received formats will be scheduled.
   * Invoking with same time schedules only <b>first</b> invocation.<br/> Example:
   * <pre>{@code
   *  StopwatchBuilder stopwatchBuilder = new StopwatchBuilder()
   *
   * // When time will equal to 1 minute, then format changes to "M:SS:LL"
   * stopwatchBuilder.changeFormatWhen(1, TimeUnits.MINUTES, "M:SS:LL")
   *
   * // When time will equal to 10 minutes, then format changes to "MM:SS:LL"
   * stopwatchBuilder.changeFormatWhen(10, TimeUnits.MINUTES, "MM:SS:LL")
   *
   * // Invocation below will be ignored, because time we already have
   * // format will be applied when time will be equals to 10 minutes
   * stopwatchBuilder.changeFormatWhen(10, TimeUnits.MINUTES, "HH:MM:SS:LL")
   * }</pre>
   */
  public StopwatchBuilder changeFormatWhen(long time, TimeUnit timeUnit,
      @NonNull String newFormat) {
    expectTimeNotNegative(time);
    Semantic semantic = Analyzer.check(newFormat);
    long millis = timeUnit.toMillis(time);
    nextFormatsHolder.add(new NextFormatsHolder(millis, semantic));
    return this;
  }

  /**
   * Like {@link #changeFormatWhen(long, TimeUnit, String)}, but schedules action at a
   * certain time.<br/> Example:
   * <pre>
   * StopwatchBuilder builder = new StopwatchBuilder();
   *
   * // When time will equal to 1 minute, show toast
   * builder.actionWhen(1, TimeUnits.MINUTES, () -> {
   *     Toast.makeText(getContext(), "1 minute past", Toast.LENGTH_SHORT).show();
   * })
   *
   * // When time will equal to 10 minutes do something
   * builder.actionWhen(10, TimeUnits.MINUTES, this::doSomething())
   *
   * // Invocation below will be ignored, because time we already action will be run
   * // when time will be 10 minutes
   * builder.actionWhen(10, TimeUnits.MINUTES, () -> {
   *     Toast.makeText(getContext(), "1 minute past, lol", Toast.LENGTH_SHORT).show();
   * })
   * </pre>
   */
  public StopwatchBuilder actionWhen(long time, TimeUnit timeUnit,
      @NonNull Action action) {
    long millis = timeUnit.toMillis(time);
    actionsHolder.add(new ActionsHolder(millis, action));
    return this;
  }

  /**
   * Creates and returns stopwatch instance
   */
  public Stopwatch build() {
    expectNotNull(startSemantic, "Start format should be initialized");
    return new Stopwatch(startSemantic, tickListener, nextFormatsHolder,
        actionsHolder);
  }
}
