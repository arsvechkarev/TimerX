package timerx;

import androidx.annotation.NonNull;
import java.util.concurrent.TimeUnit;
import timerx.format.TimeFormatter;

/**
 * Represents timer with base functions like {@link #start() start}, {@link #stop() stop},
 * etc. Also allows to format time with specific pattern and run actions at a certain
 * time. See {@link TimeFormatter} to find out how to use format syntax. Use {@link
 * TimerBuilder} to configure and instantiate the timer.
 *
 * @author Arseniy Svechkarev
 * @see TimerBuilder
 * @see Stopwatch
 */
public interface Timer {

  /**
   * Returns the start time formatted according to the start format<br/> For example, if
   * the start format is "MM:SS.LL", and start time is 10 minutes, then result will be
   * "10:00.00"
   */
  @NonNull
  CharSequence getFormattedStartTime();

  /**
   * Depending on the state of the timer there are three possible variants of behavior:
   * <p> - If timer hasn't been started yet or {@link #reset()} was called, then
   * the timer starts from the start time</p>
   * <p> - If timer has been started, and method {@link #stop()} was called, then
   * the timer continues running</p>
   * <p> - If timer is running, then the method does nothing</p>
   */
  void start();

  /**
   * Stops timer if it is active. If it isn't active, then the method does nothing
   */
  void stop();

  /**
   * Stops timer and resets time to zero. Subsequent calls have no effect
   */
  void reset();

  /**
   * Returns remaining time in specified unit
   *
   * @param timeUnit time unit to witch time will be converted
   */
  long getRemainingTimeIn(@NonNull TimeUnit timeUnit);
}
