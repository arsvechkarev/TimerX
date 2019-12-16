package timerx;

import static timerx.util.Checker.assertTimeNotNegative;

import androidx.annotation.NonNull;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import timerx.format.Analyzer;
import timerx.format.Semantic;
import timerx.format.TimeFormatter;
import timerx.util.Checker;

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
   * Schedules changing format at certain time. This method can be invokes many times, all
   * received formats will be scheduled.
   */
  public StopwatchBuilder changeFormatWhen(long time, TimeUnit timeUnit,
      @NonNull String newFormat) {
    assertTimeNotNegative(time);
    Semantic semantic = Analyzer.check(newFormat);
    long millis = timeUnit.toMillis(time);
    nextFormatsHolder.add(new NextFormatsHolder(millis, semantic));
    return this;
  }

  /**
   * Like {@link #changeFormatWhen(long, TimeUnit, String)}, but schedules action at a
   * certain time.<br/>
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
    Checker.assertNotNull(startSemantic, "Start format should be initialized");
    return new Stopwatch(startSemantic, tickListener, nextFormatsHolder,
        actionsHolder);
  }
}
