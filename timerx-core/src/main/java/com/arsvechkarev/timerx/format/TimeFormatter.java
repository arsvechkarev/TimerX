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

  public long getOptimizedDelay() {
    long delay;
    if (semantic.has(MILLISECONDS)) {
      if (semantic.countOf(MILLISECONDS) == 1) {
        delay = 100;
      } else if (semantic.countOf(MILLISECONDS) == 2) {
        delay = 10;
      } else {
        delay = 1;
      }
    } else {
      delay = MILLIS_IN_SECOND;
    }
    return delay;
  }

  public long getOptimizedInterval() {
    long delay;
    if (semantic.has(MILLISECONDS)) {
      if (semantic.countOf(MILLISECONDS) == 1) {
        delay = 100;
      } else if (semantic.countOf(MILLISECONDS) == 2) {
        delay = 10;
      } else {
        delay = 1;
      }
    } else {
      delay = 100;
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


  /*


  D/Timer: handleMessage: currentTime = 2114
D/TimeFormatter: format: millisToShow = 114
D/TimeFormatter: format: secondsToShow = 2
D/TimeFormatter: format: minutesToShow = -1
D/TimeFormatter: format: hoursToShow = -1
D/TimeFormatter: getFormatOf (ms): number = 114
D/TimeFormatter: getFormatOf (ms): numLength = 3
D/TimeFormatter: getFormatOf (ms): countInSemantic
D/TimeFormatter: getFormatOf (ms): diff = -1
D/TimeFormatter: decreaseMillisIfNeed: millis = 114
D/TimeFormatter: decreaseMillisIfNeed: reduceNum =
D/TimeFormatter: decreaseMillisIfNeed: resMillis =
D/TimeFormatter: applyFormatOf: strMillis = 11
D/TimeFormatter: applyFormatOf: strSeconds = 02
D/TimeFormatter: applyFormatOf: strMinutes =
D/TimeFormatter: applyFormatOf: strHours =
D/Timer: handleMessage: format = 02:11
D/Timer: =================
D/Timer: handleMessage: currentTime = 2097
D/TimeFormatter: format: millisToShow = 97
D/TimeFormatter: format: secondsToShow = 2
D/TimeFormatter: format: minutesToShow = -1
D/TimeFormatter: format: hoursToShow = -1
D/TimeFormatter: getFormatOf (ms): number = 97
D/TimeFormatter: getFormatOf (ms): numLength = 2
D/TimeFormatter: getFormatOf (ms): countInSemantic
D/TimeFormatter: getFormatOf (ms): diff = 0
D/TimeFormatter: decreaseMillisIfNeed: millis = 97
D/TimeFormatter: decreaseMillisIfNeed: reduceNum =
D/TimeFormatter: decreaseMillisIfNeed: resMillis =
D/TimeFormatter: applyFormatOf: strMillis = 97
D/TimeFormatter: applyFormatOf: strSeconds = 02
D/TimeFormatter: applyFormatOf: strMinutes =
D/TimeFormatter: applyFormatOf: strHours =
D/Timer: handleMessage: format = 02:97
D/Timer: =================


  */


  /*
  *
  *
  * 2 digits:
  *
  *                             currentTime = 9110
                                semantic.countOf = 2
                                lengthOf(number) = 3
                                diff = -1
                                number = 110
                                kkkkk: reduce num = 10
                                resMillis = 11
                                format = 09:11
                                ------
                                currentTime = 9100
                                semantic.countOf = 2
                                lengthOf(number) = 3
                                diff = -1
                                number = 100
                                kkkkk: reduce num = 10
                                resMillis = 10
                                format = 09:10
                                ------
                                currentTime = 9090
                                semantic.countOf = 2
                                lengthOf(number) = 2
                                diff = 0
                                number = 90
                                kkkkk: reduce num = 1
                                resMillis = 90
                                format = 09:90
                                ------
                                currentTime = 9080
                                semantic.countOf = 2
                                lengthOf(number) = 2
                                diff = 0
                                number = 80
                                kkkkk: reduce num = 1
                                resMillis = 80
                                format = 09:80
                                ------
  *
  *
  *
  *
  *
  *
  *
  *
  *
  *
  * */

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
      }
      if (diff > 0) {
        return addZerosTo(number, diff);
      } else {
        if (numberType == MILLISECONDS) {
          return formatMillis(number, semanticCount);
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

  private String formatMillis(long millis, int semanticCount) {
    String strMillis = formatToThreeOrMoreDigits(millis);
    Log.d(TAG, "formatMillis: millis = " + millis);
    Log.d(TAG, "formatMillis: semanticCount = " + semanticCount);
    Log.d(TAG, "formatMillis: strMillis = " + strMillis);
    if (semanticCount == 1) {
      return strMillis.substring(0, 1);
    }
    if (semanticCount == 2) {
      return strMillis.substring(0, 2);
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
