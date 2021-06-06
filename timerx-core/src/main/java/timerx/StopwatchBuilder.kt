package timerx

import java.util.SortedSet
import java.util.TreeSet
import java.util.concurrent.TimeUnit

/**
 * Builder to create an instance of [Stopwatch]. Use function [buildStopwatch] to instantiate
 * the builder. Usage example:
 * <pre>
 *
 * val stopwatch = buildStopwatch {
 *    // Set the start format of timer
 *    startFormat("SS:LL")
 *    // Set the tick listener that gets notified when time changes
 *    onTick { time: CharSequence -> myTextView.text = time }
 *    // Run the action at a certain time
 *    actionWhen(10, TimeUnit.SECONDS) { showToast("10s passed") }
 *    // When time is equal to one minute, change format to "MM:SS:LL"
 *    changeFormatWhen(1, TimeUnit.MINUTES, "MM:SS:LL")
 * }
 *
 * // Start stopwatch
 * stopwatch.start();
 *
 * // Wait a couple of seconds...
 *
 * // Get current time in milliseconds
 * long currentTime = stopwatch.getTimeIn(TimeUnit.MILLISECONDS);
 *
 * // When stopwatch is not needed anymore
 * stopwatch.release()
 * </pre>
 *
 * @see Stopwatch
 * @see buildStopwatch
 */
public class StopwatchBuilder internal constructor() {
  
  private var startSemantic: Semantic? = null
  private var startTime: Long = 0
  private var tickListener: TimeTickListener? = null
  private val semanticsHolders: SortedSet<SemanticsHolder> = TreeSet()
  private val actionsHolders: SortedSet<ActionsHolder> = TreeSet()
  
  /**
   * Set start time format to stopwatch
   *
   * @param format Format for stopwatch. See [TimeFormatter] to find out about formats
   */
  public fun startFormat(format: String) {
    startSemantic = Analyzer.analyze(format)
  }
  
  /**
   * Set the start time to stopwatch
   *
   * @param time Time to set
   * @param timeUnit Unit of the time
   */
  public fun startTime(time: Long, timeUnit: TimeUnit) {
    require(time >= 0) { "Time shouldn't be negative" }
    startTime = timeUnit.toMillis(time)
  }
  
  /**
   * Set tick listener to receive formatted time
   */
  public fun onTick(tickListener: TimeTickListener) {
    this.tickListener = tickListener
  }
  
  /**
   * Schedules changing format at a certain time. Format is applied as soon as stopwatch
   * reaches given time. This method can be called many times, all received formats will
   * be scheduled. When called with the same time, only first invocation is scheduled.
   * Examples:
   * <pre>
   * val stopwatch = buildStopwatch {
   *    // When the time reaches 1 minute, then format will be changed to "M:SS:LL"
   *    changeFormatWhen(1, TimeUnits.MINUTES, "M:SS:LL")
   *    // When the time reaches 10 minutes, then format will be changed to "MM:SS:LL"
   *    changeFormatWhen(10, TimeUnits.MINUTES, "MM:SS:LL")
   * }
   * </pre>
   */
  public fun changeFormatWhen(time: Long, timeUnit: TimeUnit, newFormat: String) {
    require(time >= 0) { "Time cannot be negative" }
    val semantic = Analyzer.analyze(newFormat)
    val millis = timeUnit.toMillis(time)
    semanticsHolders.add(SemanticsHolder(millis, semantic))
  }
  
  /**
   * Like [changeFormatWhen], but schedules an action at a certain time.
   * Example:
   * <pre>
   *  val stopwatch = buildStopwatch {
   *     // When the time is equal to 1 minute, show toast
   *     actionWhen(1, TimeUnit.MINUTES) { showToast("1 minute passed") }
   *  }
   * </pre>
   */
  public fun actionWhen(time: Long, timeUnit: TimeUnit, action: Runnable) {
    require(time >= 0) { "Time cannot be negative" }
    val millis = timeUnit.toMillis(time)
    actionsHolders.add(ActionsHolder(millis, action))
  }
  
  /**
   * Creates and returns stopwatch instance
   */
  internal fun build(): Stopwatch {
    val startSemantic = startSemantic ?: error("Start format is not provided. Call" +
        " startFormat(String) to provide initial format")
    semanticsHolders.add(SemanticsHolder(startTime, startSemantic))
    return StopwatchImpl(tickListener, semanticsHolders.toMutableList(),
      actionsHolders.toMutableList())
  }
}

/**
 * Use this function to create and configure an instance of stopwatch
 *
 * @see StopwatchBuilder
 */
public fun buildStopwatch(action: StopwatchBuilder.() -> Unit): Stopwatch {
  return StopwatchBuilder().apply(action).build()
}