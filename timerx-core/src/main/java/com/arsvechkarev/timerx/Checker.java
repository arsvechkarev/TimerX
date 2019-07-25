package com.arsvechkarev.timerx;

import com.arsvechkarev.timerx.Constants.TimeValues;

public class Checker {

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

  public static void assertThat(boolean condition) {
    if (!condition) {
      throw new AssertionError();
    }
  }
}
