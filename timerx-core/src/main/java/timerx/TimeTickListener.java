package timerx;

import androidx.annotation.NonNull;

/**
 * Tick listener for receiving formatted time
 */
public interface TimeTickListener {

  void onTick(@NonNull CharSequence time);
}
