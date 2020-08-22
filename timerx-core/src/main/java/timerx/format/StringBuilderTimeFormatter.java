package timerx.format;

import static timerx.format.TimeUnitType.HOURS;
import static timerx.format.TimeUnitType.MINUTES;
import static timerx.format.TimeUnitType.R_MILLISECONDS;
import static timerx.format.TimeUnitType.SECONDS;
import static timerx.util.Constants.TimeValues.MILLIS_IN_SECOND;
import static timerx.util.Constants.TimeValues.MINUTES_IN_HOUR;
import static timerx.util.Constants.TimeValues.NONE;
import static timerx.util.Constants.TimeValues.SECONDS_IN_MINUTE;

import androidx.annotation.NonNull;

/**
 * Optimized time formatter based on string builder
 */
public class StringBuilderTimeFormatter extends TimeFormatter {

  private final Semantic semantic;
  private final StringBuilder mutableString;
  private final TimeContainer timeContainer = new TimeContainer();

  public StringBuilderTimeFormatter(@NonNull Semantic semantic) {
    this.semantic = semantic;
    mutableString = new StringBuilder(semantic.strippedFormat.length());
    mutableString.append(semantic.strippedFormat);
  }

  public long getOptimalDelay() {
    long delay = 100;
    if (semantic.has(R_MILLISECONDS)) {
      if (semantic.rMillisPosition.length() == 2) {
        delay = 10;
      } else if (semantic.rMillisPosition.length() > 2) {
        delay = 1;
      }
    }
    return delay;
  }

  @NonNull
  public String getFormat() {
    return semantic.getFormat();
  }

  @NonNull
  public CharSequence format(long millis) {
    TimeContainer units = timeUnitsOf(millis);
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
    applyFormat(millisToShow, secondsToShow, minutesToShow, hoursToShow);
    return mutableString;
  }

  @NonNull
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

  private void applyFormat(long millisToShow, long secondsToShow,
      long minutesToShow, long hoursToShow) {
    if (millisToShow != NONE) updateString(millisToShow, R_MILLISECONDS);
    if (secondsToShow != NONE) updateString(secondsToShow, SECONDS);
    if (minutesToShow != NONE) updateString(minutesToShow, MINUTES);
    if (hoursToShow != NONE) updateString(hoursToShow, HOURS);
  }

  private void updateString(long time, TimeUnitType timeUnitType) {
    Position position = positionOfUnit(timeUnitType);
    int timeLength = lengthOf(time);
    if (!semantic.hasOnlyRMillis() && timeUnitType == R_MILLISECONDS) {
      if (semantic.rMillisPosition.length() < 3 && timeLength < 3) {
        int difference = 3 - semantic.rMillisPosition.length();
        time /= Math.pow(10, difference);
      }
      if (semantic.rMillisPosition.length() < timeLength) {
        int difference = timeLength - semantic.rMillisPosition.length();
        time /= Math.pow(10, difference);
      }
    }
    int updatedTimeLength = lengthOf(time);
    int range = Math.max(position.end - position.start, updatedTimeLength - 1);
    for (int i = position.end; i >= position.end - range; i--) {
      char ch = (char) ('0' + (time % 10));
      if (i >= position.start) {
        mutableString.setCharAt(i, ch);
      } else {
        mutableString.insert(Math.max(i + 1, 0), ch);
      }
      time /= 10;
    }
  }

  @NonNull
  private Position positionOfUnit(TimeUnitType timeUnitType) {
    switch (timeUnitType) {
      case HOURS:
        return semantic.hoursPosition;
      case MINUTES:
        return semantic.minutesPosition;
      case SECONDS:
        return semantic.secondsPosition;
      case R_MILLISECONDS:
        return semantic.rMillisPosition;
    }
    throw new IllegalStateException();
  }

  private int lengthOf(long number) {
    int length = 0;
    long temp = 1;
    while (temp <= number) {
      length++;
      temp *= 10;
    }
    return length;
  }
}
