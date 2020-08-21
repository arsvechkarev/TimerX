package timerx;

import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;

/**
 * Current state of either timer or stopwatch
 */
@RestrictTo(Scope.LIBRARY)
enum TimeCountingState {
  RESUMED,
  PAUSED,
  INACTIVE
}
