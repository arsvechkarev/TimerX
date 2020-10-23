package timerx;

import androidx.annotation.NonNull;

/**
 * Tick listener that receives formatted time
 */
public interface TimeTickListener {

  /**
   * @param time Formatted time
   * @param millis Time in milliseconds
   */
  void onTick(@NonNull CharSequence time, long millis);
}
