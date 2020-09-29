package timerx;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import timerx.Constants.Symbols;

class Analyzer {

  @NonNull
  static Semantic analyze(String format) {
    Position hours = checkPositionOf(TimeUnitType.HOURS, format);
    Position minutes = checkPositionOf(TimeUnitType.MINUTES, format);
    Position seconds = checkPositionOf(TimeUnitType.SECONDS, format);
    Position rMillis = checkPositionOf(TimeUnitType.R_MILLISECONDS, format);
    validatePositions(hours, minutes, seconds, rMillis);
    validateCombinations(hours, minutes, seconds, rMillis);
    TimeUnitType smallestUnit = getSmallestAvailableUnit(minutes, seconds,
        rMillis);
    String strippedFormat = stripFormat(format);
    return new Semantic(hours, minutes, seconds, rMillis, format, strippedFormat,
        smallestUnit);
  }

  @VisibleForTesting
  @NonNull
  static Position checkPositionOf(TimeUnitType timeUnitType, String input) {
    char timeUnitChar = timeUnitType.getValue();
    int start = -1;
    int end = -1;
    for (int i = 0; i < input.length(); i++) {
      char symbol = input.charAt(i);
      if (isSymbolNotEscapedAndEqualTo(timeUnitType, input, i)
          && start != -1
          && i - 2 > 0
          && !isSymbolNotEscapedAndEqualTo(timeUnitType, input, i - 1)) {
        throw new NonContiguousFormatSymbolsException(
            "Time unit " + timeUnitType.getValue()
                + " was found several times in the format");
      }
      if (symbol == timeUnitChar) {
        if (i == 0) {
          start = end = i;
        } else if (input.charAt(i - 1) != '#') {
          if (start == -1) {
            start = i;
          }
          end = i;
        }
      }
    }
    start -= numberOfEscapeSymbolsBefore(input, start);
    end -= numberOfEscapeSymbolsBefore(input, end);
    return new Position(start, end);
  }

  private static boolean isSymbolNotEscapedAndEqualTo(TimeUnitType timeUnitType,
      String input,
      int position) {
    if (position - 1 < 0) {
      return false;
    }
    char symbol = input.charAt(position);
    char prev = input.charAt(position - 1);
    return prev != Symbols.SYMBOL_ESCAPE && symbol == timeUnitType.getValue();
  }

  private static void validatePositions(Position hours, Position minutes,
      Position seconds, Position rMillis) {
    if (hours.isEmpty() && minutes.isEmpty() && seconds.isEmpty() && rMillis.isEmpty()) {
      throw new NoNecessarySymbolsException(
          "No special symbols like " + Symbols.SYMBOL_HOURS + ", "
              + Symbols.SYMBOL_MINUTES + ",  " + Symbols.SYMBOL_SECONDS + " or "
              + Symbols.SYMBOL_REM_MILLIS + "was found in the format");
    }
  }

  private static void validateCombinations(Position hours, Position minutes,
      Position seconds, Position rMillis) {
    boolean hasHours = hours.isNotEmpty();
    boolean hasMinutes = minutes.isNotEmpty();
    boolean hasSeconds = seconds.isNotEmpty();
    boolean hasRMillis = rMillis.isNotEmpty();
    if (hasHours) {
      if ((hasSeconds || hasRMillis) && !hasMinutes) {
        throw new IllegalSymbolsCombinationException(
            "Input format has hours with seconds or milliseconds, but does not have minutes");
      } else if (hasMinutes && hasRMillis && !hasSeconds) {
        throw new IllegalSymbolsCombinationException(
            "Input format has hours, minutes, and milliseconds, but does not have seconds");
      }
    } else {
      if (hasMinutes && hasRMillis && !hasSeconds) {
        throw new IllegalSymbolsCombinationException(
            "Input format has minutes and milliseconds, but does not have seconds");
      }
    }
  }

  @NonNull
  private static TimeUnitType getSmallestAvailableUnit(Position minutes,
      Position seconds, Position rMillis) {
    TimeUnitType smallestAvailableUnit = TimeUnitType.HOURS;
    if (minutes.isNotEmpty()) smallestAvailableUnit = TimeUnitType.MINUTES;
    if (seconds.isNotEmpty()) smallestAvailableUnit = TimeUnitType.SECONDS;
    if (rMillis.isNotEmpty()) smallestAvailableUnit = TimeUnitType.R_MILLISECONDS;
    return smallestAvailableUnit;
  }

  @NonNull
  private static String stripFormat(String format) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < format.length(); i++) {
      char symbol = format.charAt(i);
      if (symbol == Symbols.SYMBOL_ESCAPE && i < format.length() - 1) {
        char next = format.charAt(i + 1);
        if (Symbols.isOneOfSpecialSymbols(next)) {
          // Do not add this symbol
          continue;
        }
      }
      builder.append(symbol);
    }
    return builder.toString();
  }

  private static int numberOfEscapeSymbolsBefore(String input, int position) {
    int count = 0;
    for (int i = 0; i < position - 1; i++) {
      char symbol = input.charAt(i);
      char next = input.charAt(i + 1);
      if (symbol == Symbols.SYMBOL_ESCAPE && Symbols.isOneOfSpecialSymbols(next)) {
        count++;
      }
    }
    return count;
  }
}
