package com.arsvechkarev.timerx;

import java.util.concurrent.TimeUnit;

/**
 * Holder of actions and times when first should be executed
 *
 * @see TimerBuilder#actionWhen(long, TimeUnit, Action)
 * @see StopwatchBuilder#actionWhen(long, TimeUnit, Action)
 */
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

  @SuppressWarnings("UseCompareMethod") // Method requires api 19, but current min is 14
  @Override
  public int compareTo(ActionsHolder o) {
    return (millis > o.millis) ? 1 : (millis < o.millis) ? -1 : 0;
  }
}
