package timerx.format;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import timerx.exceptions.IllegalSymbolsCombinationException;
import timerx.exceptions.NoNecessarySymbolsException;
import timerx.exceptions.NonContiguousFormatSymbolsException;
import timerx.util.Constants.Symbols;

public class Analyzer {

  @NonNull
  public static Semantic analyze(String input) {
    Position hours = checkPositionOf(TimeUnitType.HOURS, input);
    Position minutes = checkPositionOf(TimeUnitType.MINUTES, input);
    Position seconds = checkPositionOf(TimeUnitType.SECONDS, input);
    Position rMillis = checkPositionOf(TimeUnitType.R_MILLISECONDS, input);
    validatePositions(hours, minutes, seconds, rMillis);
    validateCombinations(hours, minutes, seconds, rMillis);
    TimeUnitType smallestUnit = getSmallestAvailableUnit(minutes, seconds,
        rMillis);
    String strippedFormat = stripFormat(input);
    return new Semantic(hours, minutes, seconds, rMillis, input, strippedFormat,
        smallestUnit);
  }

  @VisibleForTesting
  @NonNull
  static Position checkPositionOf(TimeUnitType timeUnitType, String input) {
    char timeUnitChar = timeUnitType.getValue();
    int start = -1;
    int end = -1;
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      if (isSymbolNotEscapedAndEqualTo(timeUnitType, input, i)
          && start != -1
          && i - 2 > 0
          && !isSymbolNotEscapedAndEqualTo(timeUnitType, input, i - 1)) {
        throw new NonContiguousFormatSymbolsException(
            "Time unit " + timeUnitType.getValue()
                + " was found several times in the format");
      }
      if (c == timeUnitChar) {
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
    return format.replace(String.valueOf(Symbols.SYMBOL_ESCAPE), "");
  }

  private static int numberOfEscapeSymbolsBefore(String input, int position) {
    int count = 0;
    for (int i = 0; i < position; i++) {
      char symbol = input.charAt(i);
      if (symbol == Symbols.SYMBOL_ESCAPE) {
        count++;
      }
    }
    return count;
  }
}
