package timerx

import java.util.concurrent.TimeUnit

/**
 * Represents a timer with base functions like [start], [stop], etc. Also allows to format time with
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
   * "10:00.00".
   */
  public val formattedStartTime: CharSequence
  
  /**
   * Returns the remaining time of the timer in milliseconds.
   */
  public val remainingTimeInMillis: Long
  
  /**
   * Returns the current time formatted according to current format.
   */
  public val remainingFormattedTime: CharSequence
  
  /**
   * Depending on the state of the timer there are three possible types of behavior:
   *
   * - If the timer hasn't been started yet or [reset] was called, then
   * the timer starts from the initial time.
   *
   * - If the timer has been started, and method [stop] was called, then
   * the timer continues running.
   *
   * - If the timer is running, then the method does nothing.
   */
  public fun start()
  
  /**
   * Stops the timer if it is running. If it isn't running, then the method does nothing.
   */
  public fun stop()
  
  /**
   * Sets the [time] to the timer. Time will be set whether timer is running or not. Note that
   * calling this **will not** result on onTickListener() callback invocation
   */
  public fun setTime(time: Long, timeUnit: TimeUnit)
  
  /**
   * Stops the timer and resets its time to initial value. Subsequent calls have no effect.
   */
  public fun reset()
  
  /**
   * Stops the timer and removes all tick and actions listeners. There is no guarantee that timer
   * will be working properly again. You might want to call this method from onDestroy() in
   * Activity or Fragment. If you want to use timer after calling this method you will have to
   * discard this instance and create a new one.
   */
  public fun release()
}