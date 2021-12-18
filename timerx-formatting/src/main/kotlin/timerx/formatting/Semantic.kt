package timerx.formatting

/**
 * Information about format, such as positions of different units, smallest available unit
 * and stripped version of format without escape symbols
 */
interface Semantic {
  
  val hoursPosition: Position
  val minutesPosition: Position
  val secondsPosition: Position
  val rMillisPosition: Position
  val format: String
  val strippedFormat: String
  val smallestAvailableUnit: TimeUnitType
  val largestAvailableUnit: TimeUnitType
  val largestAvailableUnitLength: Int
  
  fun has(unitType: TimeUnitType): Boolean
  fun hasOnlyRMillis(): Boolean
  fun getPositionOf(unitType: TimeUnitType): Position
}
