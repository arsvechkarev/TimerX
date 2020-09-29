package timerx;

import androidx.annotation.NonNull;

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

  @NonNull
  Semantic getSemantic() {
    return semantic;
  }

  @SuppressWarnings("UseCompareMethod") // Method requires api 19, but current min is 14
  @Override
  public int compareTo(NextFormatsHolder o) {
    return (millis > o.millis) ? 1 : (millis < o.millis) ? -1 : 0;
  }
}
