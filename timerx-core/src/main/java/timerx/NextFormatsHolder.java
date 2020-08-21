package timerx;

import java.util.concurrent.TimeUnit;
import timerx.format2.NewSemantic;

/**
 * Holder of formats and times when former should be applied in {@link TimerImpl} and
 * {@link StopwatchImpl}
 *
 * @see TimerBuilder#changeFormatWhen(long, TimeUnit, String)
 * @see StopwatchBuilder#changeFormatWhen(long, TimeUnit, String)
 */
class NextFormatsHolder implements Comparable<NextFormatsHolder> {

  private final long millis;
  private final NewSemantic semantic;

  NextFormatsHolder(long millis, NewSemantic semantic) {
    this.millis = millis;
    this.semantic = semantic;
  }

  long getMillis() {
    return millis;
  }

  public NewSemantic getSemantic() {
    return semantic;
  }

  @SuppressWarnings("UseCompareMethod") // Method requires api 19, but current min is 14
  @Override
  public int compareTo(NextFormatsHolder o) {
    return (millis > o.millis) ? 1 : (millis < o.millis) ? -1 : 0;
  }
}
