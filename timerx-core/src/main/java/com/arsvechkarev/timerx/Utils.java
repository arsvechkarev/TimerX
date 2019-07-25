package com.arsvechkarev.timerx;

import java.util.concurrent.TimeUnit;

public class Utils {

  public static long millisOf(long time, TimeUnits unitType) {
    switch (unitType) {
      case HOURS:
        return TimeUnit.HOURS.toMillis(time);
      case MINUTES:
        return TimeUnit.MINUTES.toMillis(time);
      case SECONDS:
        return TimeUnit.SECONDS.toMillis(time);
      case MILLISECONDS:
        return time;
      default:
        throw new IllegalArgumentException("Incorrect enum constant");
    }
  }

  public static long timeIn(long millis, TimeUnits unitType) {
    switch (unitType) {
      case HOURS:
        return TimeUnit.MILLISECONDS.toHours(millis);
      case MINUTES:
        return TimeUnit.MILLISECONDS.toMinutes(millis);
      case SECONDS:
        return TimeUnit.MILLISECONDS.toSeconds(millis);
      case MILLISECONDS:
        return millis;
      default:
        throw new IllegalArgumentException("Incorrect enum constant");
    }
  }

}
