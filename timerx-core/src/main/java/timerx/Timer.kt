package timerx

import java.util.concurrent.TimeUnit

/**
 * Represents timer with base functions like [start], [stop], etc. Also allows to format time with
 * specific pattern and run actions at a certain time. See [TimeFormatter] to find out how to use
 * format syntax. Use [TimerBuilder] to configure and instantiate the timer.
 *
 * @see TimerBuilder
 * @see Stopwatch
 *
 * @author Arseniy Svechkarev
 */
public interface Timer {
  
  /**
   * Returns the start time formatted according to the start format. For example, if
   * the start format is "MM:SS.LL", and start time is 10 minutes, then result will be
   * "10:00.00"
   */
  public val formattedStartTime: CharSequence
  
  /**
   * Returns remaining time of the timer in milliseconds
   */
  public val remainingTimeInMillis: Long
  
  /**
   * Returns current time formatted according to current format
   */
  public val remainingFormattedTime: CharSequence
  
  /**
   * Depending on the state of the timer there are three possible variants of behavior:
   *
   * - If timer hasn't been started yet or [reset] was called, then
   * the timer starts from the initial time
   *
   * - If timer has been started, and method [stop] was called, then
   * the timer continues running
   *
   * - If timer is running, then the method does nothing
   */
  public fun start()
  
  /**
   * Stops timer if it is active. If it isn't active, then the method does nothing
   */
  public fun stop()
  
  /**
   * Sets time to the timer. Time will be set whether timer is running or not. Note that
   * calling this **will not** result on onTickListener() callback invocation
   */
  public fun setTime(time: Long, timeUnit: TimeUnit)
  
  /**
   * Stops timer and resets time to initial value. Subsequent calls have no effect
   */
  public fun reset()
  
  /**
   * Stops timer and removes all tick and actions listeners and there is no guarantee that timer
   * will be working properly again. You might want to call this method from onDestroy() in
   * Activity or Fragment. If you want to use timer after calling this method you will have to
   * discard this instance and create a new one
   */
  public fun release()
}