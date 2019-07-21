package com.arsvechkarev.timerx.format;

import static com.arsvechkarev.timerx.Constants.EMPTY_STRING;
import static com.arsvechkarev.timerx.Constants.Patterns.ESCAPED_HOURS;
import static com.arsvechkarev.timerx.Constants.Patterns.ESCAPED_MILLIS;
import static com.arsvechkarev.timerx.Constants.Patterns.ESCAPED_MINUTES;
import static com.arsvechkarev.timerx.Constants.Patterns.ESCAPED_SECONDS;
import static com.arsvechkarev.timerx.Constants.Patterns.PATTERN_HAS_HOURS;
import static com.arsvechkarev.timerx.Constants.Patterns.PATTERN_HAS_MILLIS;
import static com.arsvechkarev.timerx.Constants.Patterns.PATTERN_HAS_MINUTES;
import static com.arsvechkarev.timerx.Constants.Patterns.PATTERN_HAS_SECONDS;
import static com.arsvechkarev.timerx.Constants.Patterns.STANDARD_HOURS;
import static com.arsvechkarev.timerx.Constants.Patterns.STANDARD_MILLIS;
import static com.arsvechkarev.timerx.Constants.Patterns.STANDARD_MINUTES;
import static com.arsvechkarev.timerx.Constants.Patterns.STANDARD_SECONDS;
import static com.arsvechkarev.timerx.Constants.TimeUnits.HOURS;
import static com.arsvechkarev.timerx.Constants.TimeUnits.MILLIS;
import static com.arsvechkarev.timerx.Constants.TimeUnits.MILLIS_IN_SECOND;
import static com.arsvechkarev.timerx.Constants.TimeUnits.MINUTES;
import static com.arsvechkarev.timerx.Constants.TimeUnits.MINUTES_IN_HOUR;
import static com.arsvechkarev.timerx.Constants.TimeUnits.NONE;
import static com.arsvechkarev.timerx.Constants.TimeUnits.SECONDS;
import static com.arsvechkarev.timerx.Constants.TimeUnits.SECONDS_IN_MINUTE;
import static com.arsvechkarev.timerx.Constants.ZERO;

public class TimeFormatter {

  private final Semantic semantic;
  private final TimeContainer timeContainer;
  private final String format;

  public TimeFormatter(Semantic semantic) {
    this.semantic = semantic;
    this.format = semantic.getFormat();
    timeContainer = new TimeContainer();
  }

  public long getOptimizedDelay() {
    long delay;
    if (semantic.has(MILLIS)) {
      if (semantic.countOf(MILLIS) == 1) {
        delay = 100;
      } else if (semantic.countOf(MILLIS) == 2) {
        delay = 10;
      } else {
        delay = 1;
      }
    } else {
      delay = MILLIS_IN_SECOND;
    }
    return delay;
  }

  public String format(long delayTime) {
    TimeContainer units = timeUnitsOf(delayTime);
    long millisToShow = NONE;
    long secondsToShow = NONE;
    long minutesToShow = NONE;
    long hoursToShow = NONE;
    if (semantic.has(MILLIS)) {
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
    String strMillis = getFormatOf(millisToShow, MILLIS);

    return format
        .replaceAll(PATTERN_HAS_HOURS, strHours)
        .replaceAll(PATTERN_HAS_MINUTES, strMinutes)
        .replaceAll(PATTERN_HAS_SECONDS, strSeconds)
        .replaceAll(PATTERN_HAS_MILLIS, strMillis)
        .replaceAll(ESCAPED_HOURS, STANDARD_HOURS)
        .replaceAll(ESCAPED_MINUTES, STANDARD_MINUTES)
        .replaceAll(ESCAPED_SECONDS, STANDARD_SECONDS)
        .replaceAll(ESCAPED_MILLIS, STANDARD_MILLIS);
  }

  private String getFormatOf(long number, int numberType) {
    if (number != NONE) {
      if (number == 0) {
        return zerosBy(semantic.countOf(numberType));
      }
      int diff = semantic.countOf(numberType) - lengthOf(number);
      if (diff > 0) {
        return addZerosTo(number, diff);
      } else {
        if (numberType == MILLIS) {
          return decreaseMillisIfNeed(number, diff);
        }
        return String.valueOf(number);
      }
    }
    return EMPTY_STRING;
  }

  private String zerosBy(int num) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < num; i++) {
      builder.append(ZERO);
    }
    return builder.toString();
  }

  private String decreaseMillisIfNeed(long millis, int diff) {
    int reduceNum = (int) Math.pow(10, Math.abs(diff));
    long resMillis = millis / reduceNum;
    return String.valueOf(resMillis);
  }

  private int lengthOf(long number) {
    return String.valueOf(number).length();
  }

  private String addZerosTo(long number, int zerosCount) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < zerosCount; i++) {
      builder.append(ZERO);
    }
    return builder.append(number).toString();
  }
}
