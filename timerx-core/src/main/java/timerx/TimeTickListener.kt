package timerx

/**
 * Tick listener that receives formatted time
 */
public fun interface TimeTickListener {
  /**
   * @param time Formatted time
   */
  public fun onTick(time: CharSequence)
}