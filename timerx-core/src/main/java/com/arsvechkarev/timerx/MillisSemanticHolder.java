package com.arsvechkarev.timerx;

import com.arsvechkarev.timerx.format.Semantic;

public class MillisSemanticHolder implements Comparable<MillisSemanticHolder> {

  private long millis;
  private Semantic semantic;

  MillisSemanticHolder(long millis, Semantic semantic) {
    this.millis = millis;
    this.semantic = semantic;
  }

  public long getMillis() {
    return millis;
  }

  public Semantic getSemantic() {
    return semantic;
  }

  @SuppressWarnings("UseCompareMethod") // Method requires api 19
  @Override
  public int compareTo(MillisSemanticHolder o) {
    return (millis > o.millis) ? 1 : (millis < o.millis) ? -1 : 0;
  }

  @Override
  public String toString() {
    return "MillisSemanticHolder{" +
        "millis=" + millis +
        ", format=" + semantic.getFormat() +
        '}';
  }
}
