package timerx

/**
 * Tick listener that receives time in milliseconds and formatted time
 */
public fun interface TimeTickListener {
  
  /**
   * Called on timer/stopwatch tick
   *
   * @param millis Time on timer/stopwatch
   * @param formattedTime Time formatted according to current format
   */
  public fun onTick(millis: Long, formattedTime: CharSequence)
}