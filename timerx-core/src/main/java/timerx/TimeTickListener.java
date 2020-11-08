package timerx;

import androidx.annotation.NonNull;

/**
 * Tick listener that receives formatted time
 */
public interface TimeTickListener {

  /**
   * @param time Formatted time
   */
  void onTick(@NonNull CharSequence time);
}
