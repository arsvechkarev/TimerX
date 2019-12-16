package timerx.util;

import timerx.util.Constants.TimeValues;

public class Checker {

  public static void assertNotNull(Object o) {
    if (o == null) {
      throw new IllegalArgumentException("Value is null");
    }
  }

  public static void assertNotNull(Object o, String failMsg) {
    if (o == null) {
      throw new IllegalArgumentException(failMsg);
    }
  }

  public static void assertTimeNotNegative(long value) {
    if (value < 0) {
      throw new IllegalArgumentException("Time shouldn't be negative");
    }
  }

  public static void assertTimeInitialized(long time, String failMsg) {
    if (time == TimeValues.NONE) {
      throw new IllegalArgumentException(failMsg);
    }
  }

  public static void expect(boolean condition) {
    if (!condition) {
      throw new AssertionError();
    }
  }

}
