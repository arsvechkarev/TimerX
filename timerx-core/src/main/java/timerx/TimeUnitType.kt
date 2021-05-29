package timerx

import timerx.Constants.Symbols.SYMBOL_HOURS
import timerx.Constants.Symbols.SYMBOL_MINUTES
import timerx.Constants.Symbols.SYMBOL_REM_MILLIS
import timerx.Constants.Symbols.SYMBOL_SECONDS

internal enum class TimeUnitType(val value: Char) {
  HOURS(SYMBOL_HOURS),
  MINUTES(SYMBOL_MINUTES),
  SECONDS(SYMBOL_SECONDS),
  R_MILLISECONDS(SYMBOL_REM_MILLIS);
}