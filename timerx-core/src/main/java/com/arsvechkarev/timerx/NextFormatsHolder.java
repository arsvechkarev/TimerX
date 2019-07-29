package com.arsvechkarev.timerx;

import com.arsvechkarev.timerx.format.Semantic;
import java.util.concurrent.TimeUnit;

/**
 * Holder of formats and times when first should be applied in {@link Timer} and {@link
 * Stopwatch}
 *
 * @see TimerBuilder#changeFormatWhen(long, TimeUnit, String)
 * @see StopwatchBuilder#changeFormatWhen(long, TimeUnit, String)
 */
public class NextFormatsHolder implements Comparable<NextFormatsHolder> {

  private long millis;
  private Semantic semantic;

  NextFormatsHolder(long millis, Semantic semantic) {
    this.millis = millis;
    this.semantic = semantic;
  }

  long getMillis() {
    return millis;
  }

  Semantic getSemantic() {
    return semantic;
  }

  @SuppressWarnings("UseCompareMethod") // Method requires api 19, but current min is 14
  @Override
  public int compareTo(NextFormatsHolder o) {
    return (millis > o.millis) ? 1 : (millis < o.millis) ? -1 : 0;
  }
}
