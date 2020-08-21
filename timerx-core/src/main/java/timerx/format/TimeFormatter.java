package timerx.format;

import static timerx.TimeUnit.HOURS;
import static timerx.TimeUnit.MINUTES;
import static timerx.TimeUnit.R_MILLISECONDS;
import static timerx.TimeUnit.SECONDS;
import static timerx.util.Constants.TimeValues.MILLIS_IN_HOUR;
import static timerx.util.Constants.TimeValues.MILLIS_IN_MINUTE;
import static timerx.util.Constants.TimeValues.MILLIS_IN_SECOND;
import static timerx.util.Constants.TimeValues.MINUTES_IN_HOUR;
import static timerx.util.Constants.TimeValues.NONE;
import static timerx.util.Constants.TimeValues.SECONDS_IN_MINUTE;

import androidx.annotation.NonNull;
import timerx.TimeUnit;

public class TimeFormatter {

  public final Semantic semantic;
  private final TimeContainer timeContainer = new TimeContainer();
  private final StringBuilder mutableString;

  public TimeFormatter(Semantic semantic) {
    this.semantic = semantic;
    mutableString = new StringBuilder(semantic.strippedFormat.length());
    mutableString.append(semantic.strippedFormat);
  }

  public long getOptimizedDelay() {
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
  public String currentFormat() {
    return semantic.getFormat();
  }

  public long minimumUnitInMillis() {
    if (semantic.smallestAvailableUnit == R_MILLISECONDS) {
      return 1;
    } else if (semantic.smallestAvailableUnit == SECONDS) {
      return MILLIS_IN_SECOND;
    } else if (semantic.smallestAvailableUnit == MINUTES) {
      return MILLIS_IN_MINUTE;
    }
    return MILLIS_IN_HOUR;
  }

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
    return applyFormat(millisToShow, secondsToShow, minutesToShow, hoursToShow);
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

  private CharSequence applyFormat(long millisToShow, long secondsToShow,
      long minutesToShow, long hoursToShow) {
    if (millisToShow != NONE) updateString(millisToShow, R_MILLISECONDS);
    if (secondsToShow != NONE) updateString(secondsToShow, SECONDS);
    if (minutesToShow != NONE) updateString(minutesToShow, MINUTES);
    if (hoursToShow != NONE) updateString(hoursToShow, HOURS);
    return mutableString;
  }

  private void updateString(long time, TimeUnit timeUnit) {
    Position position = positionOfUnit(timeUnit);
    int timeLength = lengthOf(time);
    if (!semantic.hasOnlyRMillis() && timeUnit == R_MILLISECONDS) {
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

  private Position positionOfUnit(TimeUnit timeUnit) {
    switch (timeUnit) {
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
