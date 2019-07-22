package com.arsvechkarev.timerx;

import com.arsvechkarev.timerx.Constants.TimeValues;
import java.util.concurrent.TimeUnit;

public class Utils {

  public static long millisOf(long time, TimeUnits timeUnit) {
    switch (timeUnit) {
      case HOURS:
        return TimeUnit.HOURS.toMillis(time);
      case MINUTES:
        return TimeUnit.MINUTES.toMillis(time);
      case SECONDS:
        return TimeUnit.SECONDS.toMillis(time);
      case MILLIS:
        return time;
      default:
        throw new IllegalArgumentException("Incorrect enum constant");
    }
  }

  public static void checkNotNull(Object o, String msg) {
    if (o == null) {
      throw new IllegalArgumentException(msg);
    }
  }

  public static void checkInitialized(long time, String msg) {
    if (time == TimeValues.NONE) {
      throw new IllegalArgumentException(msg);
    }
  }

}
