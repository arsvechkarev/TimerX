package com.arsvechkarev.timerview;

import com.arsvechkarev.timerview.exceptions.IllegalSymbolsCombinationException;
import com.arsvechkarev.timerview.exceptions.IllegalSymbolsPositionException;
import com.arsvechkarev.timerview.exceptions.NoNecessarySymbolsException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Validator {

  private static final String PATTERN_HAS_HOURS = "(?<!#)H+";
  private static final String PATTERN_HAS_MINUTES = "(?<!#)M+";
  private static final String PATTERN_HAS_SECONDS = "(?<!#)S+";
  private static final String PATTERN_HAS_MILLIS = "(?<!#)L+";

  private boolean hasHours;
  private boolean hasMinutes;
  private boolean hasSeconds;
  private boolean hasMillis;

  private Validator() {
  }

  static Validator check(String inputFormat) {
    Validator validator = new Validator();
    validator.checkFormat(inputFormat);
    return validator;
  }

  private void checkFormat(String parseFormat) {
    int formatOccurs = superficialCheckOf(parseFormat);
    if (formatOccurs == 0) {
      throwNoSymbolsEx();
    } else {
      executeCombinationCheck();
    }
  }

  private int superficialCheckOf(String parseFormat) {
    int formatOccurs = 0;
    for (int pos = 0; pos < 4; pos++) {
      String strPattern = nextPattern(pos);
      Pattern pattern = Pattern.compile(strPattern);
      Matcher matcher = pattern.matcher(parseFormat);
      int counter = 0;
      while (matcher.find()) {
        updateAppropriateField(pos);
        formatOccurs++;
        counter++;
        if (counter > 1) {
          throwIncorrectPositionEx();
        }
      }
    }
    return formatOccurs;
  }

  // @formatter:off
  private String nextPattern(int pos) {
    switch (pos) {
      case 0: return PATTERN_HAS_HOURS;
      case 1: return PATTERN_HAS_MINUTES;
      case 2: return PATTERN_HAS_SECONDS;
      case 3: return PATTERN_HAS_MILLIS;
      default: throw new IllegalStateException("No pattern to match");
    }
  }
  // @formatter:on


  private void updateAppropriateField(int pos) {
    if (pos == 0) {
      hasHours = true;
    } else if (pos == 1) {
      hasMinutes = true;
    } else if (pos == 2) {
      hasSeconds = true;
    } else if (pos == 3) {
      hasMillis = true;
    } else {
      throw new IllegalStateException("No number to match");
    }
  }

  private void executeCombinationCheck() {
    if (hasHours) {
      if ((hasSeconds || hasMillis) && !hasMinutes) {
        throwCombinationEx();
      } else if (hasMinutes && hasMillis && !hasSeconds) {
        throwCombinationEx();
      }
    } else {
      if (hasMinutes) {
        if (hasMillis && !hasSeconds) {
          throwCombinationEx();
        }
      }
    }
  }

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

  boolean hasHours() {
    return hasHours;
  }

  boolean hasMinutes() {
    return hasMinutes;
  }

  boolean hasSeconds() {
    return hasSeconds;
  }

  boolean hasMillis() {
    return hasMillis;
  }
}
