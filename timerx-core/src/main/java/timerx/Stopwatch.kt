package timerx

import java.util.concurrent.TimeUnit

/**
 * Represents stopwatch with base functions like [start], [stop], etc. Also allows to format time
 * with the specific pattern and run actions at a certain time. See [TimeFormatter] to find out
 * how to use format syntax. Use [StopwatchBuilder] to configure and instantiate the stopwatch.
 *
 * @see StopwatchBuilder
 * @see Timer
 *
 * @author Arseniy Svechkarev
 */
public interface Stopwatch {
  
  /**
   * Returns start time (in this case, 0) formatted according to start format. For
   * example, if the start format is "MM:SS.LL", then result is "00:00.00"
   */
  public val formattedStartTime: CharSequence
  
  /**
   * Depending on the state of the stopwatch there are three possible variants of
   * behavior:
   *
   * - If stopwatch hasn't been started yet or [.reset] was called, then
   * stopwatch starts from 0
   *
   * - If stopwatch has been started, and method [.stop] was called, then
   * stopwatch continues from previously stopped time
   *
   * - If stopwatch is running, then this method does nothing
   */
  public fun start()
  
  /**
   * Stops stopwatch if it is running. If it isn't running, then the method does nothing
   */
  public fun stop()
  
  /**
   * Sets time of the stopwatch
   */
  public fun setTimeTo(time: Long, timeUnit: TimeUnit)
  
  /**
   * Stops stopwatch and resets time to initial time. Subsequent calls have no effect
   */
  public fun reset()
  
  /**
   * Returns current time of the stopwatch converted to a specified time unit
   *
   * @param timeUnit time unit to witch current time time will be converted
   */
  public fun getTimeIn(timeUnit: TimeUnit): Long
  
  /**
   * Stops stopwatch and removes all tick and actions listeners and there is no guarantee that
   * stopwatch will be working properly again. You might want to call this method from
   * onDestroy() in Activity or Fragment. If you want to use stopwatch after calling this
   * method you will have to discard this instance and create a new one
   */
  public fun release()
}