package timerx;

import java.util.concurrent.TimeUnit;
import timerx.format.Semantic;

/**
 * Holder of formats and times when former should be applied in {@link Timer} and {@link
 * Stopwatch}
 *
 * @see TimerBuilder#changeFormatWhen(long, TimeUnit, String)
 * @see StopwatchBuilder#changeFormatWhen(long, TimeUnit, String)
 */
class NextFormatsHolder implements Comparable<NextFormatsHolder> {

  private final long millis;
  private final Semantic semantic;

  NextFormatsHolder(long millis, Semantic semantic) {
    this.millis = millis;
    this.semantic = semantic;
  }

  long getMillis() {
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
