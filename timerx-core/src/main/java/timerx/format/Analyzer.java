package timerx.format;

import static timerx.TimeUnit.HOURS;
import static timerx.TimeUnit.MINUTES;
import static timerx.TimeUnit.R_MILLISECONDS;
import static timerx.TimeUnit.SECONDS;
import static timerx.util.Checker.expect;
import static timerx.util.Constants.Patterns.ESCAPED_HOURS;
import static timerx.util.Constants.Patterns.ESCAPED_MINUTES;
import static timerx.util.Constants.Patterns.ESCAPED_REM_MILLIS;
import static timerx.util.Constants.Patterns.ESCAPED_SECONDS;
import static timerx.util.Constants.Patterns.PATTERN_HAS_HOURS;
import static timerx.util.Constants.Patterns.PATTERN_HAS_MINUTES;
import static timerx.util.Constants.Patterns.PATTERN_HAS_REM_MILLIS;
import static timerx.util.Constants.Patterns.PATTERN_HAS_SECONDS;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import timerx.exceptions.IllegalSymbolsCombinationException;
import timerx.exceptions.NoNecessarySymbolsException;
import timerx.exceptions.NonContiguousFormatSymbolsException;

/**
 * Class for validating and analyzing input format and builds a {@link Semantic} of the
 * format
 *
 * @see Semantic
 * @see TimeFormatter
 */
public class Analyzer {

  private final Semantic semantic;

  private Analyzer(Semantic semantic) {
    this.semantic = semantic;
  }

  /**
   * Checks and validates input format
   *
   * @param format Format to check
   * @return Semantic with information about format
   */
  public static Semantic create(String format) {
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
      assignEscapedPatterns();
    }
  }

  private int positionalCheckOf(String format) {
    int formatOccurs = 0;
    for (int type = 0; type < 4; type++) {
      String strPattern = nextPatternOf(type);
      Pattern pattern = Pattern.compile(strPattern);
      assignPattern(type, pattern);
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

  private void assignPattern(int type, Pattern pattern) {
    if (type == 0) {
      semantic.patternHours = pattern;
    } else if (type == 1) {
      semantic.patternMinutes = pattern;
    } else if (type == 2) {
      semantic.patternSeconds = pattern;
    } else if (type == 3) {
      semantic.patternRMillis = pattern;
    } else {
      throw new IllegalArgumentException("What was that?");
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

  private void assignEscapedPatterns() {
    semantic.patternEscapedHours = Pattern.compile(ESCAPED_HOURS);
    semantic.patternEscapedMinutes = Pattern.compile(ESCAPED_MINUTES);
    semantic.patternEscapedSeconds = Pattern.compile(ESCAPED_SECONDS);
    semantic.patternEscapedRMillis = Pattern.compile(ESCAPED_REM_MILLIS);
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

  // TODO: 26.07.2019 Translate below

  private void throwIncorrectPositionEx() {
    throw new NonContiguousFormatSymbolsException(
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
