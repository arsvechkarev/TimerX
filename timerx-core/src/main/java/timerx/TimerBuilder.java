package timerx;

import static timerx.util.Checker.assertNotNull;
import static timerx.util.Checker.assertTimeInitialized;
import static timerx.util.Checker.assertTimeNotNegative;

import androidx.annotation.NonNull;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import timerx.format.TimeFormatter;
import timerx.format2.NewAnalyzer;
import timerx.format2.NewSemantic;
import timerx.util.Checker;
import timerx.util.Constants.TimeValues;

/**
 * Builder to configure and instantiate {@link TimerImpl}.<br/> Usage example:
 * <pre>
 *  Timer timer = new TimerBuilder()
 *        // Set start format to timer. Format syntax explaining {@link TimeFormatter here}
 *        .startFormat("MMm SSs")
 *        // Set start time of timer
 *        .startTime(10, TimeUnit.MINUTES)
 *        // When certain time remains (in this case one minute), format changes to "SS:LL"
 *        .changeFormatWhen(1, TimeUnit.MINUTES, "SS:LL")
 *        // Set time tick listener
 *        .tickListener(time -> textView.setText(time))
 *        // Executing an action when certain time remains
 *        .actionWhen(30, TimeUnit.SECONDS, () -> {
 *            Toast.makeText(context, "30 seconds left!", Toast.LENGTH_SHORT).show();
 *        })
 *        .build();
 * </pre>
 *
 * @see TimerImpl
 */
public class TimerBuilder {

  private NewSemantic startSemantic;

  private long startTime = TimeValues.NONE;

  private TimeTickListener tickListener;
  private Action finishAction;

  private final SortedSet<NextFormatsHolder> nextFormatsHolder = new TreeSet<>(
      Collections.reverseOrder());
  private final SortedSet<ActionsHolder> nextActionsHolder = new TreeSet<>(
      Collections.reverseOrder());

  /**
   * Set start format to timer
   */
  public TimerBuilder startFormat(@NonNull String format) {
    this.startSemantic = NewAnalyzer.analyze(format);
    return this;
  }

  /**
   * Set start time to timer
   */
  public TimerBuilder startTime(long time, TimeUnit timeUnit) {
    assertTimeNotNegative(time);
    startTime = timeUnit.toMillis(time);
    return this;
  }

  /**
   * Set tick listener to receive formatted time
   */
  public TimerBuilder onTick(@NonNull TimeTickListener tickListener) {
    this.tickListener = tickListener;
    return this;
  }

  /**
   * Set tick listener to be notified when timer ends
   */
  public TimerBuilder onFinish(@NonNull Action finishAction) {
    this.finishAction = finishAction;
    return this;
  }

  /**
   * Schedules changing format at certain time. Format will be applied as soon as time
   * comes. This method can be invokes many times, all received formats will be scheduled.
   * Invoking with same time schedules only <b>first</b> invocation.<br/> Examples:
   * <pre>
   * TimerBuilder builder = new TimerBuilder();
   *
   * // When time will equal to 1 minute, then format changes to "M:SS:LL"
   * builder.changeFormatWhen(1, TimeUnits.MINUTES, "M:SS:LL");
   *
   * // When time will equal to 10 minutes, then format changes to "MM:SS:LL"
   * builder.changeFormatWhen(10, TimeUnits.MINUTES, "MM:SS:LL");
   *
   * // Invocation below will be ignored, because time we already have format will be applied
   * // when time will be equals to 10 minutes
   * builder.changeFormatWhen(10, TimeUnits.MINUTES, "HH:MM:SS:LL");
   * </pre>
   */
  public TimerBuilder changeFormatWhen(long time, TimeUnit timeUnit,
      @NonNull String newFormat) {
    assertTimeNotNegative(time);
    NewSemantic semantic = NewAnalyzer.analyze(newFormat);
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
   * // When time will equal to 1 minute (remains one minute), show toast
   * builder.actionWhen(1, TimeUnits.MINUTES, () -> {
   *     Toast.makeText(getContext(), "1 minute past", Toast.LENGTH_SHORT).show();
   * })
   *
   * // Invocation below will be ignored, because time we already action will be run
   * // when remains 1 minute
   * builder.actionWhen(1, TimeUnits.MINUTES, () -> {
   *     Toast.makeText(getContext(), "1 minute past, lol", Toast.LENGTH_SHORT).show();
   * })
   * </pre>
   */
  public TimerBuilder actionWhen(long time, TimeUnit timeUnit,
      @NonNull Action action) {
    assertTimeNotNegative(time);
    assertNotNull(action);
    long millis = timeUnit.toMillis(time);
    nextActionsHolder.add(new ActionsHolder(millis, action));
    return this;
  }

  /**
   * Creates and returns timer instance
   */
  public TimerImpl build() {
    Checker.assertNotNull(startSemantic, "Start format should be initialized");
    assertTimeInitialized(startTime, "Time should be initialized");
    return new TimerImpl(startTime, startSemantic, tickListener, finishAction,
        nextFormatsHolder,
        nextActionsHolder);
  }
}
