package com.arsvechkarev.timerx;

public class ActionsHolder implements Comparable<ActionsHolder> {

  private long millis;
  private Runnable action;

  public ActionsHolder(long millis, Runnable action) {
    this.millis = millis;
    this.action = action;
  }

  public long getMillis() {
    return millis;
  }

  public Runnable getAction() {
    return action;
  }

  @SuppressWarnings("UseCompareMethod")
  @Override
  public int compareTo(ActionsHolder o) {
    return (millis > o.millis) ? 1 : (millis < o.millis) ? -1 : 0;
  }
}
