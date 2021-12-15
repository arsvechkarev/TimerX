package timerx;

import static timerx.Checker.assertTimeNotNegative;

import androidx.annotation.NonNull;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**
 * Builder to configure and instantiate {@link Stopwatch}.<br/> Usage example:
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
    private long startTime;


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
     * Set the start time to stopwatch
     *
     * @param time     Time to set
     * @param timeUnit Unit of the time
     */
    @NonNull
    public StopwatchBuilder startTime(long time, @NonNull TimeUnit timeUnit) {
        Checker.assertTimeNotNegative(time);
        this.startTime = timeUnit.toMillis(time);
        return this;
    }

    /**
     * Schedules changing format at a certain time. Format is applied as soon as stopwatch
     * reaches given time. This method can be called many times, all received formats will
     * be scheduled. When called with the same time, only first invocation is scheduled
     * Examples:
     * <pre>
     * StopwatchBuilder builder = new StopwatchBuilder();
     *
     * // When the time is equal to 1 minute, then format will be changed to "M:SS:LL"
     * builder.changeFormatWhen(1, TimeUnits.MINUTES, "M:SS:LL");
     *
     * // When the time is equal to 10 minutes, then format will be changed to "MM:SS:LL"
     * builder.changeFormatWhen(10, TimeUnits.MINUTES, "MM:SS:LL");
     *
     *  ...
     * </pre>
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
     * Like {@link #changeFormatWhen(long, TimeUnit, String)}, but schedules an action at a
     * certain time.<br/> Example:
     * <pre>
     * StopwatchBuilder builder = new StopwatchBuilder();
     *
     * // When the time is equal to 1 minute, show toast
     * builder.actionWhen(1, TimeUnits.MINUTES, () -> {
     *     Toast.makeText(getContext(), "1 minute past", Toast.LENGTH_SHORT).show();
     * })
     *
     * ...
     * </pre>
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
                actionsHolder, startTime);
    }
}
