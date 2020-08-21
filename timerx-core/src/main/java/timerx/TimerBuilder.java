package timerx;

import static timerx.util.Checker.assertNotNull;
import static timerx.util.Checker.assertTimeInitialized;
import static timerx.util.Checker.assertTimeNotNegative;

import androidx.annotation.NonNull;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import timerx.format.Analyzer;
import timerx.format.Semantic;
import timerx.util.Checker;
import timerx.util.Constants.TimeValues;

/**
 * Builder to configure and instantiate {@link TimerImpl}.<br/> Usage example:
 * <pre>{@code
 *   Timer timer = new TimerBuilder()
 *         // Set the start format of the timer
 *         .startFormat("SS:LL")
 *         // Set the start time
 *         .startTime(45, TimeUnit.SECONDS)
 *         // Set the tick listener that gets notified when time changes
 *         .tickListener(time -> textViewTime.setText(time))
 *         // Run the action after a certain time
 *         .actionWhen(30, TimeUnit.SECONDS, () -> {
 *            Toast.makeText(context, "30 seconds past!", Toast.LENGTH_SHORT).show();
 *         })
 *         // When time will be equal to one minute, change format to "MM:SS:LL"
 *         .changeFormatWhen(1, TimeUnit.MINUTES, "MM:SS:LL")
 *         .build();
 *
 *   timer.start();
 *
 *   // Wait a couple of seconds...
 *
 *   // Get remaining time in seconds
 *   long remaining = timer.getRemainingTimeIn(TimeUnit.SECONDS);
 * }</pre>
 *
 * @see Timer
 */
public class TimerBuilder {

  private Semantic startSemantic;

  private long startTime = TimeValues.NONE;

  private TimeTickListener tickListener;
  private Action finishAction;

  private final SortedSet<NextFormatsHolder> nextFormatsHolder = new TreeSet<>(
      Collections.reverseOrder());
  private final SortedSet<ActionsHolder> nextActionsHolder = new TreeSet<>(
      Collections.reverseOrder());

  /**
   * Set the start format to timer
   *
   * @param format Format for timer. See {@link timerx.format.TimeFormatter} to find out
   * what are valid formats
   */
  @NonNull
  public TimerBuilder startFormat(@NonNull String format) {
    this.startSemantic = Analyzer.analyze(format);
    return this;
  }

  /**
   * Set start time to timer
   *
   * @param time Time to set
   * @param timeUnit Unit of the time
   */
  @NonNull
  public TimerBuilder startTime(long time, @NonNull TimeUnit timeUnit) {
    assertTimeNotNegative(time);
    startTime = timeUnit.toMillis(time);
    return this;
  }

  /**
   * Set tick listener to receive formatted time
   *
   * @param tickListener Event listener for format changing
   */
  @NonNull
  public TimerBuilder onTick(@NonNull TimeTickListener tickListener) {
    this.tickListener = tickListener;
    return this;
  }

  /**
   * Set tick listener to be notified when timer ends
   *
   * @param finishAction Action that fires up when timer reaches 0
   */
  @NonNull
  public TimerBuilder onFinish(@NonNull Action finishAction) {
    this.finishAction = finishAction;
    return this;
  }

  /**
   * Schedules changing format at a certain time. Format will be applied as soon as timer
   * reaches given time. This method can be invokes many times, all received formats will
   * be scheduled. Invoking with the same time schedules only <b>first</b>
   * invocation.<br/> Examples:
   * <pre>
   * TimerBuilder builder = new TimerBuilder();
   *
   * // When the remaining time is equal to 10 minutes, then format will be
   * // changed to "MM:SS:LL"
   * builder.changeFormatWhen(10, TimeUnits.MINUTES, "MM:SS:LL");
   *
   * // When the remaining time is equal to 1 minute, then format will be
   * // changed to "M:SS:LL"
   * builder.changeFormatWhen(1, TimeUnits.MINUTES, "M:SS:LL");
   *
   * ...
   * </pre>
   */
  @NonNull
  public TimerBuilder changeFormatWhen(long time, @NonNull TimeUnit timeUnit,
      @NonNull String newFormat) {
    assertTimeNotNegative(time);
    Semantic semantic = Analyzer.analyze(newFormat);
    long millis = timeUnit.toMillis(time);
    nextFormatsHolder.add(new NextFormatsHolder(millis, semantic));
    return this;
  }

  /**
   * Like {@link #changeFormatWhen(long, TimeUnit, String)}, but schedules action at a
   * certain time.<br/> Example:
   * <pre>
   * TimerBuilder builder = new TimerBuilder();
   *
   * // When the remaining time is equal to 1 minute, show toast
   * builder.actionWhen(1, TimeUnits.MINUTES, () -> {
   *     Toast.makeText(getContext(), "1 minute past", Toast.LENGTH_SHORT).show();
   * })
   *
   * ...
   * </pre>
   */
  @NonNull
  public TimerBuilder actionWhen(long time, @NonNull TimeUnit timeUnit,
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
  @NonNull
  public Timer build() {
    Checker.assertNotNull(startSemantic, "Start format should be initialized");
    assertTimeInitialized(startTime, "Time should be initialized");
    return new TimerImpl(startTime, startSemantic, tickListener, finishAction,
        nextFormatsHolder,
        nextActionsHolder);
  }
}
