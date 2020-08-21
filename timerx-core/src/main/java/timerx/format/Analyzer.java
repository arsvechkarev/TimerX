package timerx.format;

import androidx.annotation.VisibleForTesting;
import timerx.TimeUnit;
import timerx.exceptions.IllegalSymbolsCombinationException;
import timerx.exceptions.NoNecessarySymbolsException;
import timerx.exceptions.NonContiguousFormatSymbolsException;

public class Analyzer {

  public static Semantic analyze(String input) {
    Position hours = checkPositionOf(TimeUnit.HOURS, input);
    Position minutes = checkPositionOf(TimeUnit.MINUTES, input);
    Position seconds = checkPositionOf(TimeUnit.SECONDS, input);
    Position rMillis = checkPositionOf(TimeUnit.R_MILLISECONDS, input);
    validatePositions(hours, minutes, seconds, rMillis);
    validateCombinations(hours, minutes, seconds, rMillis);
    TimeUnit smallestUnit = getSmallestAvailableUnit(hours, minutes, seconds, rMillis);
    String strippedFormat = stripFormat(input);
    return new Semantic(hours, minutes, seconds, rMillis, input, strippedFormat,
        smallestUnit);
  }

  @VisibleForTesting
  static Position checkPositionOf(TimeUnit timeUnit, String input) {
    char timeUnitChar = timeUnit.getValue();
    int start = -1;
    int end = -1;
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      if (isSymbolNotEscapedAndEqualTo(timeUnit, input, i)
          && start != -1
          && i - 2 > 0
          && !isSymbolNotEscapedAndEqualTo(timeUnit, input, i - 1)) {
        throw new NonContiguousFormatSymbolsException("Wrong pattern");
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

  private static boolean isSymbolNotEscapedAndEqualTo(TimeUnit timeUnit, String input,
      int position) {
    if (position - 1 < 0) {
      return false;
    }
    char symbol = input.charAt(position);
    char prev = input.charAt(position - 1);
    return prev != '#' && symbol == timeUnit.getValue();
  }

  private static void validatePositions(Position hours, Position minutes,
      Position seconds, Position rMillis) {
    if (hours.isEmpty() && minutes.isEmpty() && seconds.isEmpty() && rMillis.isEmpty()) {
      throw new NoNecessarySymbolsException("No necessary symbols");
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

  private static TimeUnit getSmallestAvailableUnit(Position hours, Position minutes,
      Position seconds, Position rMillis) {
    TimeUnit smallestAvailableUnit = TimeUnit.HOURS;
    if (minutes.isNotEmpty()) smallestAvailableUnit = TimeUnit.MINUTES;
    if (seconds.isNotEmpty()) smallestAvailableUnit = TimeUnit.SECONDS;
    if (rMillis.isNotEmpty()) smallestAvailableUnit = TimeUnit.R_MILLISECONDS;
    return smallestAvailableUnit;
  }

  private static String stripFormat(String format) {
    return format.replace("#", "");
  }

  private static int numberOfEscapeSymbolsBefore(String input, int position) {
    int count = 0;
    for (int i = 0; i < position; i++) {
      char symbol = input.charAt(i);
      if (symbol == '#') {
        count++;
      }
    }
    return count;
  }
}
