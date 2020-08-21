package timerx.format;

import static timerx.util.Constants.Symbols.SYMBOL_HOURS;
import static timerx.util.Constants.Symbols.SYMBOL_MINUTES;
import static timerx.util.Constants.Symbols.SYMBOL_REM_MILLIS;
import static timerx.util.Constants.Symbols.SYMBOL_SECONDS;

enum TimeUnitType {
  HOURS(SYMBOL_HOURS),
  MINUTES(SYMBOL_MINUTES),
  SECONDS(SYMBOL_SECONDS),
  R_MILLISECONDS(SYMBOL_REM_MILLIS);

  private final char value;

  TimeUnitType(char value) {
    this.value = value;
  }

  public char getValue() {
    return value;
  }
}
