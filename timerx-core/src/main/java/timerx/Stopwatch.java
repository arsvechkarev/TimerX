package timerx;

import androidx.annotation.NonNull;
import java.util.concurrent.TimeUnit;
import timerx.format.TimeFormatter;

/**
 * Represents Stopwatch with base functions like {@link #start() start}, {@link #stop()
 * stop}, etc. Also allows to format time with specific pattern and run actions at a
 * certain time. See {@link TimeFormatter} to find out how to use format syntax. Use
 * {@link StopwatchBuilder} to configure and instantiate the stopwatch.
 *
 * @author Arseny Svechkarev
 * @see StopwatchBuilder
 * @see Timer
 */
public interface Stopwatch {

  /**
   * Returns start time (in this case, 0) formatted according to start format<br/> For
   * example, if start format is "MM:SS.LL", then result is "00:00.00"
   */
  @NonNull
  CharSequence getFormattedStartTime();

  /**
   * Depending on the state of the stopwatch there are three possible variants of
   * behavior:
   * <p> - If stopwatch hasn't been started yet or {@link #reset()} was called, then
   * stopwatch starts from 0</p>
   * <p> - If stopwatch has been started, and method {@link #stop()} was called, then
   * stopwatch continues with previously saved time</p>
   * <p> - If stopwatch is running now, then calling the method does nothing</p>
   */
  void start();

  /**
   * Stops stopwatch if it is running now. If it isn't running now, the calling the method
   * does nothing
   */
  void stop();

  /**
   * Stops timer and resets time to zero. Subsequent calls have no effect
   */
  void reset();

  /**
   * Returns current time of the stopwatch converted to specified time unit
   *
   * @param timeUnit time unit to witch time will be converted
   */
  long getTimeIn(@NonNull TimeUnit timeUnit);
}
