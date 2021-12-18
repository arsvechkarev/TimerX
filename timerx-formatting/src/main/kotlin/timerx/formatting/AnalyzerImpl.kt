package timerx.formatting

import timerx.formatting.Constants.Symbols
import timerx.formatting.Constants.Symbols.isOneOfSpecialSymbols

internal object AnalyzerImpl : Analyzer {
  
  override fun analyze(format: String): SemanticImpl {
    val hours = getPositionOf(TimeUnitType.HOURS, format)
    val minutes = getPositionOf(TimeUnitType.MINUTES, format)
    val seconds = getPositionOf(TimeUnitType.SECONDS, format)
    val rMillis = getPositionOf(TimeUnitType.R_MILLISECONDS, format)
    validatePositions(hours, minutes, seconds, rMillis)
    validateCombinations(hours, minutes, seconds, rMillis)
    val smallestUnit = getSmallestAvailableUnit(minutes, seconds, rMillis)
    val largestUnit = getLargestAvailableUnit(hours, minutes, seconds)
    val strippedFormat = stripFormat(format)
    return SemanticImpl(hours, minutes, seconds, rMillis, format,
      strippedFormat, smallestUnit, largestUnit)
  }
  
  private fun getPositionOf(timeUnitType: TimeUnitType, input: String): Position {
    val timeUnitChar = timeUnitType.value
    var start = -1
    var end = -1
    for (i in input.indices) {
      val symbol = input[i]
      if (isSymbolNotEscapedAndEqualTo(timeUnitType, input, i)
          && start != -1 && i - 2 > 0 && !isSymbolNotEscapedAndEqualTo(timeUnitType, input,
            i - 1)) {
        throw NonContiguousFormatSymbolsException(
          "Time unit " + timeUnitType.value
              + " was found several times in the format")
      }
      if (symbol == timeUnitChar) {
        if (i == 0) {
          end = i
          start = end
        } else if (input[i - 1] != Symbols.SYMBOL_ESCAPE) {
          if (start == -1) {
            start = i
          }
          end = i
        }
      }
    }
    start -= numberOfEscapeSymbolsBefore(input, start)
    end -= numberOfEscapeSymbolsBefore(input, end)
    return Position(start, end)
  }
  
  private fun isSymbolNotEscapedAndEqualTo(
    timeUnitType: TimeUnitType,
    input: String,
    position: Int
  ): Boolean {
    if (position - 1 < 0) {
      return false
    }
    val symbol = input[position]
    val prev = input[position - 1]
    return prev != Symbols.SYMBOL_ESCAPE && symbol == timeUnitType.value
  }
  
  private fun validatePositions(
    hours: Position,
    minutes: Position,
    seconds: Position,
    rMillis: Position
  ) {
    if (hours.isEmpty && minutes.isEmpty && seconds.isEmpty && rMillis.isEmpty) {
      throw NoNecessarySymbolsException(
        "No special symbols like " + Symbols.SYMBOL_HOURS + ", "
            + Symbols.SYMBOL_MINUTES + ",  " + Symbols.SYMBOL_SECONDS + " or "
            + Symbols.SYMBOL_REM_MILLIS + "was found in the format")
    }
  }
  
  private fun validateCombinations(
    hours: Position,
    minutes: Position,
    seconds: Position,
    rMillis: Position
  ) {
    val hasHours = hours.isNotEmpty
    val hasMinutes = minutes.isNotEmpty
    val hasSeconds = seconds.isNotEmpty
    val hasRMillis = rMillis.isNotEmpty
    if (hasHours) {
      if ((hasSeconds || hasRMillis) && !hasMinutes) {
        throw IllegalSymbolsCombinationException(
          "Input format has hours with seconds or milliseconds, but does not have minutes")
      } else if (hasMinutes && hasRMillis && !hasSeconds) {
        throw IllegalSymbolsCombinationException(
          "Input format has hours, minutes, and milliseconds, but does not have seconds")
      }
    } else {
      if (hasMinutes && hasRMillis && !hasSeconds) {
        throw IllegalSymbolsCombinationException(
          "Input format has minutes and milliseconds, but does not have seconds")
      }
    }
  }
  
  private fun getSmallestAvailableUnit(
    minutes: Position,
    seconds: Position,
    rMillis: Position
  ): TimeUnitType {
    var smallestAvailableUnit = TimeUnitType.HOURS
    if (minutes.isNotEmpty) smallestAvailableUnit = TimeUnitType.MINUTES
    if (seconds.isNotEmpty) smallestAvailableUnit = TimeUnitType.SECONDS
    if (rMillis.isNotEmpty) smallestAvailableUnit = TimeUnitType.R_MILLISECONDS
    return smallestAvailableUnit
  }
  
  private fun getLargestAvailableUnit(
    hours: Position,
    minutes: Position,
    seconds: Position,
  ): TimeUnitType {
    var largestAvailableUnit = TimeUnitType.R_MILLISECONDS
    if (seconds.isNotEmpty) largestAvailableUnit = TimeUnitType.SECONDS
    if (minutes.isNotEmpty) largestAvailableUnit = TimeUnitType.MINUTES
    if (hours.isNotEmpty) largestAvailableUnit = TimeUnitType.HOURS
    return largestAvailableUnit
  }
  
  private fun stripFormat(format: String): String {
    val builder = StringBuilder()
    for (i in format.indices) {
      val symbol = format[i]
      if (symbol == Symbols.SYMBOL_ESCAPE && i < format.length - 1) {
        val next = format[i + 1]
        if (next.isOneOfSpecialSymbols()) {
          // Do not add this symbol
          continue
        }
      }
      builder.append(symbol)
    }
    return builder.toString()
  }
  
  private fun numberOfEscapeSymbolsBefore(input: String, position: Int): Int {
    var count = 0
    for (i in 0 until position - 1) {
      val symbol = input[i]
      val next = input[i + 1]
      if (symbol == Symbols.SYMBOL_ESCAPE && next.isOneOfSpecialSymbols()) {
        count++
      }
    }
    return count
  }
}
