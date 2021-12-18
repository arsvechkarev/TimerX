package timerx.formatting

/**
 * Represents position indices of special symbols in a format. [start] and [end] both could be
 * equal to -1 if there is no symbols of necessary type
 */
data class Position(val start: Int, val end: Int) {
  
  init {
    require(start <= end) { "start value: $start is more than end value: $end" }
  }
  
  val isEmpty: Boolean
    get() = start == -1 && end == -1
  
  val isNotEmpty: Boolean
    get() = start != -1 && end != -1
  
  val length get() = if (start == -1 && end == -1) 0 else end - start + 1
  
  fun offsetBy(offset: Int): Position {
    return Position(start + offset, end + offset)
  }
}
