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
import static com.arsvechkarev.timerx.Constants.TimeValues.MILLIS_IN_SECOND;
import static com.arsvechkarev.timerx.Constants.TimeValues.MINUTES_IN_HOUR;
import static com.arsvechkarev.timerx.Constants.TimeValues.NONE;
import static com.arsvechkarev.timerx.Constants.TimeValues.SECONDS_IN_MINUTE;
import static com.arsvechkarev.timerx.Constants.ZERO;
import static com.arsvechkarev.timerx.TimeUnits.HOURS;
import static com.arsvechkarev.timerx.TimeUnits.MILLISECONDS;
import static com.arsvechkarev.timerx.TimeUnits.MINUTES;
import static com.arsvechkarev.timerx.TimeUnits.SECONDS;

import android.util.Log;
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

  public long getOptimizedInterval() {
    long delay = 100;
    if (semantic.has(MILLISECONDS)) {
      if (semantic.countOf(MILLISECONDS) == 2) {
        delay = 10;
      } else if (semantic.countOf(MILLISECONDS) > 2) {
        delay = 1;
      }
    }
    return delay;
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
    if (semantic.has(MILLISECONDS)) {
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
    Log.d(TAG, "format: millisToShow = " + millisToShow);
    Log.d(TAG, "format: secondsToShow = " + secondsToShow);
    Log.d(TAG, "format: minutesToShow = " + minutesToShow);
    Log.d(TAG, "format: hoursToShow = " + hoursToShow);
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
    String strMillis = getFormatOf(millisToShow, MILLISECONDS);

    Log.d(TAG, "applyFormatOf: strMillis = " + strMillis);
    Log.d(TAG, "applyFormatOf: strSeconds = " + strSeconds);
    Log.d(TAG, "applyFormatOf: strMinutes = " + strMinutes);
    Log.d(TAG, "applyFormatOf: strHours = " + strHours);

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

  private String getFormatOf(long number, TimeUnits numberType) {
    if (number != NONE) {
      if (number == 0) {
        return zerosBy(semantic.countOf(numberType));
      }
      int semanticCount = semantic.countOf(numberType);
      int numberLength = lengthOf(number);
      int diff = semanticCount - numberLength;
      if (numberType == MILLISECONDS) {
        Log.d(TAG, "getFormatOf (ms): number = " + number);
        Log.d(TAG, "getFormatOf (ms): semanticCount = " + semanticCount);
        Log.d(TAG, "getFormatOf (ms): numberLength = " + numberLength);
        Log.d(TAG, "getFormatOf (ms): diff = " + diff);
        return formatMillis(number, semanticCount);
      }
      if (diff > 0) {
        return addZerosTo(number, diff);
      }
      return number + EMPTY_STRING;
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

  private String formatMillis(long millis, int semanticCount) {
    String strMillis = formatToThreeOrMoreDigits(millis);
    if (semanticCount <= 2) {
      return strMillis.substring(0, semanticCount);
    }
    return strMillis;
  }

  private String formatToThreeOrMoreDigits(long remMillis) {
    int length = lengthOf(remMillis);
    if (length == 1) {
      return ZERO + ZERO + remMillis;
    }
    if (length == 2) {
      return ZERO + remMillis;
    }
    return remMillis + EMPTY_STRING;
  }

  private int lengthOf(long number) {
    return (number + EMPTY_STRING).length();
  }

  private String addZerosTo(long number, int zerosCount) {
    if (zerosCount == 1) {
      return ZERO + number;
    }
    if (zerosCount == 2) {
      return ZERO + ZERO + number;
    }
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < zerosCount; i++) {
      builder.append(ZERO);
    }
    return builder.append(number).toString();
  }
}
