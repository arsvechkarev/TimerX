package timerx;

import static timerx.Constants.Symbols.SYMBOL_HOURS;
import static timerx.Constants.Symbols.SYMBOL_MINUTES;
import static timerx.Constants.Symbols.SYMBOL_REM_MILLIS;
import static timerx.Constants.Symbols.SYMBOL_SECONDS;

enum TimeUnitType {
  HOURS(SYMBOL_HOURS),
  MINUTES(SYMBOL_MINUTES),
  SECONDS(SYMBOL_SECONDS),
  R_MILLISECONDS(SYMBOL_REM_MILLIS);

  private final char value;

  TimeUnitType(char value) {
    this.value = value;
  }

  char getValue() {
    return value;
  }
}
