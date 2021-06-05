package timerx

/**
 * Information about format, such as positions of different units, smallest available unit
 * and stripped version of format without escape symbols
 */
internal class Semantic(
  val hoursPosition: Position,
  val minutesPosition: Position,
  val secondsPosition: Position,
  val rMillisPosition: Position,
  val format: String,
  val strippedFormat: String,
  val smallestAvailableUnit: TimeUnitType,
  val largestAvailableUnit: TimeUnitType
) {
  
  val largestAvailableUnitLength: Int
    get() = getPositionOf(largestAvailableUnit).length
  
  fun has(unitType: TimeUnitType) = when (unitType) {
    TimeUnitType.HOURS -> hoursPosition.isNotEmpty
    TimeUnitType.MINUTES -> minutesPosition.isNotEmpty
    TimeUnitType.SECONDS -> secondsPosition.isNotEmpty
    TimeUnitType.R_MILLISECONDS -> rMillisPosition.isNotEmpty
  }
  
  fun hasOnlyRMillis(): Boolean {
    return (rMillisPosition.isNotEmpty && secondsPosition.isEmpty
        && minutesPosition.isEmpty && hoursPosition.isEmpty)
  }
  
  fun getPositionOf(unitType: TimeUnitType) = when (unitType) {
    TimeUnitType.HOURS -> hoursPosition
    TimeUnitType.MINUTES -> minutesPosition
    TimeUnitType.SECONDS -> secondsPosition
    TimeUnitType.R_MILLISECONDS -> rMillisPosition
  }
}