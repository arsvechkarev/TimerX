package timerx

import java.util.concurrent.TimeUnit

/**
 * Represents a stopwatch with base functions like [start], [stop], etc. Also allows to format time
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
   * Returns the start time formatted according to the start format. For example, if the start format is
   * "MM:SS.LL", and start time is 0, then result will be "00:00.00".
   */
  public val formattedStartTime: CharSequence
  
  /**
   * Returns the current time of the stopwatch in milliseconds.
   */
  public val currentTimeInMillis: Long
  
  /**
   * Returns the current time formatted according to the current format.
   */
  public val currentFormattedTime: CharSequence
  
  /**
   * Depending on the state of the stopwatch there are three possible variants of
   * behavior:
   *
   * - If the stopwatch hasn't been started yet or [reset] was called, then
   * the stopwatch starts from 0.
   *
   * - If the stopwatch has been started, and method [stop] was called, then
   * the stopwatch continues from previously stopped time.
   *
   * - If the stopwatch is running, then this method does nothing.
   */
  public fun start()
  
  /**
   * Stops stopwatch if it is running. If it isn't running, then the method does nothing.
   */
  public fun stop()
  
  /**
   * Sets time to the stopwatch. Time will be set whether stopwatch is running or not. Note that
   * calling this **will not** result on onTickListener() callback invocation.
   */
  public fun setTime(time: Long, timeUnit: TimeUnit)
  
  /**
   * Stops the stopwatch and resets time to initial value. Subsequent calls have no effect.
   */
  public fun reset()
  
  /**
   * Stops the stopwatch and removes all tick and actions listeners. There is no guarantee that
   * stopwatch will be working properly again. You might want to call this method from
   * onDestroy() in Activity or Fragment. If you want to use stopwatch after calling this
   * method you will have to discard this instance and create a new one.
   */
  public fun release()
}