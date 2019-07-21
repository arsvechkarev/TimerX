package com.arsvechkarev.timerx.format;

import static com.arsvechkarev.timerx.Constants.TimeUnits.HOURS;
import static com.arsvechkarev.timerx.Constants.TimeUnits.MILLIS;
import static com.arsvechkarev.timerx.Constants.TimeUnits.MINUTES;
import static com.arsvechkarev.timerx.Constants.TimeUnits.SECONDS;

import com.arsvechkarev.timerx.Constants.TimeUnits;

// todo: handle with weaker access
@SuppressWarnings("WeakerAccess")
public class Semantic {

  private String format;

  private int hoursCount = 0;
  private int minutesCount = 0;
  private int secondsCount = 0;
  private int millisCount = 0;

  public void setHoursCount(int hoursCount) {
    this.hoursCount = hoursCount;
  }

  public void setMinutesCount(int minutesCount) {
    this.minutesCount = minutesCount;
  }

  public void setSecondsCount(int secondsCount) {
    this.secondsCount = secondsCount;
  }

  public void setMillisCount(int millisCount) {
    this.millisCount = millisCount;
  }

  public Semantic(String parseFormat) {
    this.format = parseFormat;
  }

  public String getFormat() {
    return format;
  }

  public boolean has(int unitType) {
    switch (unitType) {
      case TimeUnits.HOURS:
        return hoursCount > 0;
      case TimeUnits.MINUTES:
        return minutesCount > 0;
      case TimeUnits.SECONDS:
        return secondsCount > 0;
      case TimeUnits.MILLIS:
        return millisCount > 0;
      default:
        throw new IllegalArgumentException("Incorrect type of unit");
    }
  }

  public int countOf(int unitType) {
    switch (unitType) {
      case HOURS:
        return hoursCount;
      case MINUTES:
        return minutesCount;
      case SECONDS:
        return secondsCount;
      case MILLIS:
        return millisCount;
      default:
        throw new IllegalArgumentException("Incorrect type of unit");
    }
  }

}
