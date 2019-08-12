package timerx.common;

import java.util.concurrent.TimeUnit;
import timerx.stopwatch.StopwatchBuilder;
import timerx.timer.TimerBuilder;

/**
 * Holder of actions and times when first should be executed
 *
 * @see TimerBuilder#actionWhen(long, TimeUnit, Action)
 * @see StopwatchBuilder#actionWhen(long, TimeUnit, Action)
 */
public class ActionsHolder implements Comparable<ActionsHolder> {

  private long millis;
  private Action action;

  public ActionsHolder(long millis, Action action) {
    this.millis = millis;
    this.action = action;
  }

  public long getMillis() {
    return millis;
  }

  public Action getAction() {
    return action;
  }

  @SuppressWarnings("UseCompareMethod") // Method requires api 19, but actual min is 14
  @Override
  public int compareTo(ActionsHolder o) {
    return (millis > o.millis) ? 1 : (millis < o.millis) ? -1 : 0;
  }
}
