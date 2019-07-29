package com.arsvechkarev.timerx;

public class ActionsHolder implements Comparable<ActionsHolder> {

  private long millis;
  private Action action;

  ActionsHolder(long millis, Action action) {
    this.millis = millis;
    this.action = action;
  }

  long getMillis() {
    return millis;
  }

  Action getAction() {
    return action;
  }

  @SuppressWarnings("UseCompareMethod")
  @Override
  public int compareTo(ActionsHolder o) {
    return (millis > o.millis) ? 1 : (millis < o.millis) ? -1 : 0;
  }
}
