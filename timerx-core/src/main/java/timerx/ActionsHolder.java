package timerx;

import java.util.concurrent.TimeUnit;

/**
 * Holder of actions and times when first should be executed
 *
 * @see TimerBuilder#actionWhen(long, TimeUnit, Action)
 * @see StopwatchBuilder#actionWhen(long, TimeUnit, Action)
 */
class ActionsHolder implements Comparable<ActionsHolder> {

  private final long millis;
  private final Action action;

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

  @SuppressWarnings("UseCompareMethod") // Method requires api 19, but actual min is 14
  @Override
  public int compareTo(ActionsHolder o) {
    return (millis > o.millis) ? 1 : (millis < o.millis) ? -1 : 0;
  }
}
