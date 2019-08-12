package timerx.common;

import java.util.concurrent.TimeUnit;
import timerx.format.Semantic;
import timerx.stopwatch.StopwatchBuilder;
import timerx.stopwatch.StopwatchImpl;
import timerx.timer.TimerBuilder;
import timerx.timer.TimerImpl;

/**
 * Holder of formats and times when first should be applied in {@link TimerImpl} and
 * {@link StopwatchImpl}
 *
 * @see TimerBuilder#changeFormatWhen(long, TimeUnit, String)
 * @see StopwatchBuilder#changeFormatWhen(long, TimeUnit, String)
 */
public class NextFormatsHolder implements Comparable<NextFormatsHolder> {

  private long millis;
  private Semantic semantic;

  public NextFormatsHolder(long millis, Semantic semantic) {
    this.millis = millis;
    this.semantic = semantic;
  }

  public long getMillis() {
    return millis;
  }

  public Semantic getSemantic() {
    return semantic;
  }

  @SuppressWarnings("UseCompareMethod") // Method requires api 19, but current min is 14
  @Override
  public int compareTo(NextFormatsHolder o) {
    return (millis > o.millis) ? 1 : (millis < o.millis) ? -1 : 0;
  }
}
