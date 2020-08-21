package timerx;

import static timerx.util.Checker.assertTimeNotNegative;

import androidx.annotation.NonNull;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import timerx.format.Analyzer;
import timerx.format.Semantic;
import timerx.util.Checker;

/**
 * Builder to configure and instantiate {@link StopwatchImpl}.<br/> Usage example:
 * <pre>
 *   Stopwatch stopwatch = new StopwatchBuilder()
 *         // Set the start format of timer
 *         .startFormat("SS:LL")
 *         // Set the tick listener that gets notified when time changes
 *         .tickListener(time -> textViewTime.setText(time))
 *         // Run the action at a certain time
 *         .actionWhen(30, TimeUnit.SECONDS, () -> {
 *            Toast.makeText(context, "30 seconds past!", Toast.LENGTH_SHORT).show();
 *         })
 *         // When time is equal to one minute, change format to "MM:SS:LL"
 *         .changeFormatWhen(1, TimeUnit.MINUTES, "MM:SS:LL")
 *         .build();
 *
 *   stopwatch.start();
 *
 *   // Wait a couple of seconds...
 *
 *   // Get current time in milliseconds
 *   long currentTime = stopwatch.getTimeIn(TimeUnit.MILLISECONDS);
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
  @NonNull
  public StopwatchBuilder startFormat(@NonNull String format) {
    startSemantic = Analyzer.analyze(format);
    return this;
  }

  /**
   * Set tick listener to receive formatted time
   */
  @NonNull
  public StopwatchBuilder onTick(@NonNull TimeTickListener tickListener) {
    this.tickListener = tickListener;
    return this;
  }

  /**
   * Schedules changing format at certain time. This method can be invokes many times, all
   * received formats will be scheduled.
   */
  @NonNull
  public StopwatchBuilder changeFormatWhen(long time, @NonNull TimeUnit timeUnit,
      @NonNull String newFormat) {
    assertTimeNotNegative(time);
    Semantic semantic = Analyzer.analyze(newFormat);
    long millis = timeUnit.toMillis(time);
    nextFormatsHolder.add(new NextFormatsHolder(millis, semantic));
    return this;
  }

  /**
   * Like {@link #changeFormatWhen(long, TimeUnit, String)}, but schedules action at a
   * certain time.<br/>
   */
  @NonNull
  public StopwatchBuilder actionWhen(long time, @NonNull TimeUnit timeUnit,
      @NonNull Action action) {
    long millis = timeUnit.toMillis(time);
    actionsHolder.add(new ActionsHolder(millis, action));
    return this;
  }

  /**
   * Creates and returns stopwatch instance
   */
  @NonNull
  public Stopwatch build() {
    Checker.assertNotNull(startSemantic, "Start format should be initialized");
    return new StopwatchImpl(startSemantic, tickListener, nextFormatsHolder,
        actionsHolder);
  }
}
