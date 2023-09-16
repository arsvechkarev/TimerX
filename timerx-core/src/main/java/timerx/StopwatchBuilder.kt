package timerx

import androidx.annotation.UiThread
import java.util.SortedSet
import java.util.TreeSet
import java.util.concurrent.TimeUnit

/**
 * Builder for creation instances of [Stopwatch]. Use [buildStopwatch] to instantiate
 * the builder. Usage example:
 * <pre>
 *
 * val stopwatch = buildStopwatch {
 *    // Setting the start format of the stopwatch
 *    startFormat("SS:LL")
 *    // Setting a tick listener that gets notified when time changes
 *    onTick { millis: Long, time: CharSequence -> myTextView.text = time }
 *    // Running an action at a certain time
 *    actionWhen(10, TimeUnit.SECONDS) { showToast("10s passed") }
 *    // When the time is equal to one minute, change format to "MM:SS:LL"
 *    changeFormatWhen(1, TimeUnit.MINUTES, "MM:SS:LL")
 * }
 *
 * // Starting the stopwatch
 * stopwatch.start();
 *
 * // Getting the current time in milliseconds
 * long currentTime = stopwatch.currentTimeInMillis
 *
 * // You can change time of the stopwatch using [Stopwatch.setTime] method. Time will be changed
 * // whether stopwatch is running or not. All formats will be applied accordingly.
 * stopwatch.setTime(20, TimeUnit.SECONDS)
 *
 * // When the stopwatch is not needed anymore
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
  private var useExactDelay = false
  
  /**
   * Set start time format to the stopwatch.
   *
   * @param format Format for the stopwatch. See [TimeFormatter] to find out about formats
   */
  public fun startFormat(format: String) {
    startSemantic = Analyzer.analyze(format)
  }
  
  /**
   * Set the start time to the stopwatch.
   *
   * @param time Time to set
   * @param timeUnit Unit of the time
   */
  public fun startTime(time: Long, timeUnit: TimeUnit) {
    require(time >= 0) { "Time shouldn't be negative" }
    startTime = timeUnit.toMillis(time)
  }
  
  /**
   * Set a tick listener to receive time.
   */
  public fun onTick(tickListener: TimeTickListener) {
    this.tickListener = tickListener
  }
  
  /**
   * Schedules changing format at a certain time. Format is applied as soon as the stopwatch
   * reaches the given time. This method can be called many times, all received formats will
   * be scheduled. When called with the same time, only first invocation is applied.
   * Examples:
   * <pre>
   * val stopwatch = buildStopwatch {
   *    // When the time reaches 1 minute, then format is changed to "M:SS:LL"
   *    changeFormatWhen(1, TimeUnits.MINUTES, "M:SS:LL")
   *    // When the time reaches 10 minutes, then format is changed to "MM:SS:LL"
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
   * Determines whether stopwatch should have precise delays between ticks or not.
   *
   * If flag is set to _true_, then delay between ticks will happen according to format.
   * For example, if the current format of the stopwatch is "MM:SS", delay between onTick() \
   * invocations will be exactly 1 second, if the format is "HH:MM" - exactly one minute,
   * "SS:LL" - exactly 10 milliseconds and so on
   *
   * If, however, this flag is set to _false_, then delay between ticks might be less than exact
   * time. For example, if format is "MM:SS", delay between ticks might be less than a second, like
   * 100 milliseconds. In this case [TimeTickListener.onTick] will be called multiple times per
   * second with **same** formatted time, but **different** milliseconds
   *
   * By default this flag is set to false, and generally you don't have to worry about it. If you
   * are just displaying formatted time in a TextView and you want to have good precision, then leave
   * this flag as false. You need to set it to true if you want [TimeTickListener.onTick] method to
   * be called called exactly between delays specified by your format.
   *
   * @param [useExactDelay] Whether the stopwatch should use exact delays or not. Default is false.
   */
  public fun useExactDelay(useExactDelay: Boolean) {
    this.useExactDelay = useExactDelay
  }
  
  /**
   * Creates and returns a stopwatch instance.
   */
  internal fun build(): Stopwatch {
    val startSemantic = startSemantic ?: error("Start format is not provided. Call" +
        " startFormat(String) to provide initial format")
    semanticsHolders.add(SemanticsHolder(startTime, startSemantic))
    return StopwatchImpl(useExactDelay, tickListener, semanticsHolders.toMutableList(),
      actionsHolders.toMutableList())
  }
}

/**
 * Use this method to create and configure an instance of stopwatch. Note that you should call
 * this method either on the UI thread or on a thread that has its own Looper.
 *
 * @see StopwatchBuilder
 */
@UiThread
public fun buildStopwatch(action: StopwatchBuilder.() -> Unit): Stopwatch {
  return StopwatchBuilder().apply(action).build()
}