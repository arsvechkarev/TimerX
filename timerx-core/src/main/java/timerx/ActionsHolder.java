package timerx;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;

@RestrictTo(Scope.LIBRARY)
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

  @NonNull
  Action getAction() {
    return action;
  }

  @SuppressWarnings("UseCompareMethod") // Method requires api 19, but actual min is 14
  @Override
  public int compareTo(ActionsHolder o) {
    return (millis > o.millis) ? 1 : (millis < o.millis) ? -1 : 0;
  }
}
