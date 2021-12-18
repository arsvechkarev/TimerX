package timerx.formatting

internal class SemanticImpl(
  override val hoursPosition: Position,
  override val minutesPosition: Position,
  override val secondsPosition: Position,
  override val rMillisPosition: Position,
  override val format: String,
  override val strippedFormat: String,
  override val smallestAvailableUnit: TimeUnitType,
  override val largestAvailableUnit: TimeUnitType
) : Semantic {
  
  override val largestAvailableUnitLength: Int
    get() = getPositionOf(largestAvailableUnit).length
  
  override fun has(unitType: TimeUnitType) = when (unitType) {
    TimeUnitType.HOURS -> hoursPosition.isNotEmpty
    TimeUnitType.MINUTES -> minutesPosition.isNotEmpty
    TimeUnitType.SECONDS -> secondsPosition.isNotEmpty
    TimeUnitType.R_MILLISECONDS -> rMillisPosition.isNotEmpty
  }
  
  override fun hasOnlyRMillis(): Boolean {
    return (rMillisPosition.isNotEmpty && secondsPosition.isEmpty
        && minutesPosition.isEmpty && hoursPosition.isEmpty)
  }
  
  override fun getPositionOf(unitType: TimeUnitType) = when (unitType) {
    TimeUnitType.HOURS -> hoursPosition
    TimeUnitType.MINUTES -> minutesPosition
    TimeUnitType.SECONDS -> secondsPosition
    TimeUnitType.R_MILLISECONDS -> rMillisPosition
  }
}
