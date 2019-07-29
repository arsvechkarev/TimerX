package com.arsvechkarev.timerx.format;

import static com.arsvechkarev.timerx.TimeUnits.HOURS;
import static com.arsvechkarev.timerx.TimeUnits.MINUTES;
import static com.arsvechkarev.timerx.TimeUnits.R_MILLISECONDS;
import static com.arsvechkarev.timerx.TimeUnits.SECONDS;
import static com.arsvechkarev.timerx.util.Constants.EMPTY_STRING;
import static com.arsvechkarev.timerx.util.Constants.Patterns.ESCAPED_HOURS;
import static com.arsvechkarev.timerx.util.Constants.Patterns.ESCAPED_MINUTES;
import static com.arsvechkarev.timerx.util.Constants.Patterns.ESCAPED_R_MILLIS;
import static com.arsvechkarev.timerx.util.Constants.Patterns.ESCAPED_SECONDS;
import static com.arsvechkarev.timerx.util.Constants.Patterns.PATTERN_HAS_HOURS;
import static com.arsvechkarev.timerx.util.Constants.Patterns.PATTERN_HAS_MINUTES;
import static com.arsvechkarev.timerx.util.Constants.Patterns.PATTERN_HAS_R_MILLIS;
import static com.arsvechkarev.timerx.util.Constants.Patterns.PATTERN_HAS_SECONDS;
import static com.arsvechkarev.timerx.util.Constants.Patterns.STANDARD_HOURS;
import static com.arsvechkarev.timerx.util.Constants.Patterns.STANDARD_MINUTES;
import static com.arsvechkarev.timerx.util.Constants.Patterns.STANDARD_R_MILLIS;
import static com.arsvechkarev.timerx.util.Constants.Patterns.STANDARD_SECONDS;
import static com.arsvechkarev.timerx.util.Constants.STR_ZERO;
import static com.arsvechkarev.timerx.util.Constants.TimeValues.MILLIS_IN_HOUR;
import static com.arsvechkarev.timerx.util.Constants.TimeValues.MILLIS_IN_MINUTE;
import static com.arsvechkarev.timerx.util.Constants.TimeValues.MILLIS_IN_SECOND;
import static com.arsvechkarev.timerx.util.Constants.TimeValues.MINUTES_IN_HOUR;
import static com.arsvechkarev.timerx.util.Constants.TimeValues.NONE;
import static com.arsvechkarev.timerx.util.Constants.TimeValues.SECONDS_IN_MINUTE;

import androidx.annotation.NonNull;
import com.arsvechkarev.timerx.TimeUnits;

public class TimeFormatter {

  private static final String TAG = TimeFormatter.class.getSimpleName();

  private final Semantic semantic;
  private final TimeContainer timeContainer;
  private final String format;

  public TimeFormatter(@NonNull Semantic semantic) {
    this.semantic = semantic;
    this.format = semantic.getFormat();
    timeContainer = new TimeContainer();
  }

  public long getOptimizedDelay() {
    long delay = 100;
    if (semantic.has(R_MILLISECONDS)) {
      if (semantic.countOf(R_MILLISECONDS) == 2) {
        delay = 10;
      } else if (semantic.countOf(R_MILLISECONDS) > 2) {
        delay = 1;
      }
    }
    return delay;
  }

  public long minimumUnitInMillis() {
    if (semantic.minimumUnit() == R_MILLISECONDS) {
      return 1;
    } else if (semantic.minimumUnit() == SECONDS) {
      return MILLIS_IN_SECOND;
    } else if (semantic.minimumUnit() == MINUTES) {
      return MILLIS_IN_MINUTE;
    }
    return MILLIS_IN_HOUR;
  }

  public String currentFormat() {
    return semantic.getFormat();
  }

  @NonNull
  public String format(long time) {
    TimeContainer units = timeUnitsOf(time);
    long millisToShow = NONE;
    long secondsToShow = NONE;
    long minutesToShow = NONE;
    long hoursToShow = NONE;
    if (semantic.has(R_MILLISECONDS)) {
      millisToShow = (semantic.has(SECONDS)) ? units.remMillis : units.millis;
    }
    if (semantic.has(SECONDS)) {
      secondsToShow = (semantic.has(MINUTES)) ? units.remSeconds : units.seconds;
    }
    if (semantic.has(MINUTES)) {
      minutesToShow = (semantic.has(HOURS)) ? units.remMinutes : units.minutes;
    }
    if (semantic.has(HOURS)) {
      hoursToShow = units.hours;
    }
    return applyFormatOf(millisToShow, secondsToShow, minutesToShow, hoursToShow);
  }

  private TimeContainer timeUnitsOf(long millis) {
    long seconds = millis / MILLIS_IN_SECOND;
    long minutes = seconds / SECONDS_IN_MINUTE;
    long hours = minutes / MINUTES_IN_HOUR;
    long remMillis = millis % MILLIS_IN_SECOND;
    long remSeconds = seconds - minutes * SECONDS_IN_MINUTE;
    long remMinutes = minutes - hours * MINUTES_IN_HOUR;
    return timeContainer
        .setMillis(millis)
        .setSeconds(seconds)
        .setMinutes(minutes)
        .setHours(hours)
        .setRemMillis(remMillis)
        .setRemSeconds(remSeconds)
        .setRemMinutes(remMinutes);
  }

  private String applyFormatOf(long millisToShow, long secondsToShow,
      long minutesToShow, long hoursToShow) {
    String strHours = getFormatOf(hoursToShow, HOURS);
    String strMinutes = getFormatOf(minutesToShow, MINUTES);
    String strSeconds = getFormatOf(secondsToShow, SECONDS);
    String strMillis = getFormatOf(millisToShow, R_MILLISECONDS);

    return format
        .replaceAll(PATTERN_HAS_HOURS, strHours)
        .replaceAll(PATTERN_HAS_MINUTES, strMinutes)
        .replaceAll(PATTERN_HAS_SECONDS, strSeconds)
        .replaceAll(PATTERN_HAS_R_MILLIS, strMillis)
        .replaceAll(ESCAPED_HOURS, STANDARD_HOURS)
        .replaceAll(ESCAPED_MINUTES, STANDARD_MINUTES)
        .replaceAll(ESCAPED_SECONDS, STANDARD_SECONDS)
        .replaceAll(ESCAPED_R_MILLIS, STANDARD_R_MILLIS);
  }

  private String getFormatOf(long number, TimeUnits numberType) {
    if (number != NONE) {
      if (number == 0) {
        return zerosBy(semantic.countOf(numberType));
      }
      int semanticCount = semantic.countOf(numberType);
      int numberLength = lengthOf(number);
      int diff = semanticCount - numberLength;
      if (numberType == R_MILLISECONDS && !semantic.hasOnlyRMillis()) {
        return formatRMillis(number, semanticCount);
      }
      if (diff > 0) {
        return zerosBy(diff) + number;
      }
      return number + EMPTY_STRING;
    }
    return EMPTY_STRING;
  }

  private String zerosBy(int num) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < num; i++) {
      builder.append(STR_ZERO);
    }
    return builder.toString();
  }

  private String formatRMillis(long millis, int semanticCount) {
    String strMillis = formatToThreeOrMoreDigits(millis, semanticCount);
    if (semanticCount <= 2) {
      return strMillis.substring(0, semanticCount);
    }
    return strMillis;
  }

  private String formatToThreeOrMoreDigits(long millis, int semanticCount) {
    int numLength = lengthOf(millis);
    System.out.println("numLength = " + numLength);
    System.out.println("semanticCount = " + semanticCount);
    StringBuilder result = new StringBuilder();
    if (numLength == 1) {
      result.append(STR_ZERO).append(STR_ZERO);
    }
    if (numLength == 2) {
      result.append(STR_ZERO);
    }
    int zerosToAdd = semanticCount - (result.length() + numLength);
    System.out.println("zerosToAdd = " + zerosToAdd);
    if (zerosToAdd > 0) {
      result.insert(0, zerosBy(zerosToAdd)).append(millis);
    } else {
      result.append(millis);
    }
    System.out.println("result.toString() = " + result.toString());
    return result.toString();
  }

  private int lengthOf(long number) {
    return (number + EMPTY_STRING).length();
  }
}
