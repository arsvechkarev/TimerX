package timerx.format;

import static timerx.common.TimeUnits.HOURS;
import static timerx.common.TimeUnits.MINUTES;
import static timerx.common.TimeUnits.R_MILLISECONDS;
import static timerx.common.TimeUnits.SECONDS;
import static timerx.util.Checker.expect;
import static timerx.util.Constants.Patterns.PATTERN_HAS_HOURS;
import static timerx.util.Constants.Patterns.PATTERN_HAS_MINUTES;
import static timerx.util.Constants.Patterns.PATTERN_HAS_REM_MILLIS;
import static timerx.util.Constants.Patterns.PATTERN_HAS_SECONDS;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import timerx.exceptions.IllegalSymbolsCombinationException;
import timerx.exceptions.IllegalSymbolsPositionException;
import timerx.exceptions.NoNecessarySymbolsException;

/**
 * Class that validate and analyzes input format and builds a {@link Semantic} of the
 * format
 *
 * @see Semantic
 * @see TimeFormatter
 */
public class Analyzer {

  private Semantic semantic;

  private Analyzer(Semantic semantic) {
    this.semantic = semantic;
  }

  /**
   * Checks and validates input format
   *
   * @param format Format to check
   * @return Semantic with information about format
   */
  public static Semantic check(String format) {
    Semantic semantic = new Semantic(format);
    Analyzer analyzer = new Analyzer(semantic);
    analyzer.processFormat(semantic.getFormat());
    return semantic;
  }

  private void processFormat(String format) {
    int formatOccurs = positionalCheckOf(format);
    if (formatOccurs == 0) {
      throwNoSymbolsEx();
    } else {
      executeCombinationCheck();
    }
  }

  private int positionalCheckOf(String format) {
    int formatOccurs = 0;
    for (int type = 0; type < 4; type++) {
      String strPattern = nextPatternOf(type);
      Pattern pattern = Pattern.compile(strPattern);
      Matcher matcher = pattern.matcher(format);
      int counter = 0;
      while (matcher.find()) {
        updateAppropriateField(type, matcher.group().length());
        formatOccurs++;
        counter++;
        if (counter > 1) {
          throwIncorrectPositionEx();
        }
      }
    }
    return formatOccurs;
  }

  private String nextPatternOf(int pos) {
    switch (pos) {
      case 0:
        return PATTERN_HAS_HOURS;
      case 1:
        return PATTERN_HAS_MINUTES;
      case 2:
        return PATTERN_HAS_SECONDS;
      case 3:
        return PATTERN_HAS_REM_MILLIS;
      default:
        // TODO: 26.07.2019 Translate
        throw new IllegalArgumentException("No pattern to match");
    }
  }

  private void updateAppropriateField(int type, int count) {
    expect(count != 0);
    if (type == 0) {
      semantic.setHoursCount(count);
      semantic.setMinimumUnit(HOURS);
    } else if (type == 1) {
      semantic.setMinutesCount(count);
      semantic.setMinimumUnit(MINUTES);
    } else if (type == 2) {
      semantic.setSecondsCount(count);
      semantic.setMinimumUnit(SECONDS);
    } else if (type == 3) {
      semantic.setRMillisCount(count);
      semantic.setMinimumUnit(R_MILLISECONDS);
    } else {
      throw new IllegalArgumentException("No number to match");
    }
  }

  private void executeCombinationCheck() {
    boolean hasHours = semantic.has(HOURS);
    boolean hasMinutes = semantic.has(MINUTES);
    boolean hasSeconds = semantic.has(SECONDS);
    boolean hasRMillis = semantic.has(R_MILLISECONDS);
    if (hasHours) {
      if ((hasSeconds || hasRMillis) && !hasMinutes) {
        throwCombinationEx();
      } else if (hasMinutes && hasRMillis && !hasSeconds) {
        throwCombinationEx();
      }
    } else {
      if (hasMinutes && hasRMillis && !hasSeconds) {
        throwCombinationEx();
      }
    }
  }

  // TODO: 26.07.2019 Translate below

  private void throwIncorrectPositionEx() {
    throw new IllegalSymbolsPositionException(
        "Some check symbol contains many times in format");
  }

  private void throwNoSymbolsEx() {
    throw new NoNecessarySymbolsException("Format hasn't any necessary symbols");
  }

  private void throwCombinationEx() {
    throw new IllegalSymbolsCombinationException(
        "Different symbols in format has incompatible order");
  }
}
