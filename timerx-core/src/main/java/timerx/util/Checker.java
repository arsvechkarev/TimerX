package timerx.util;

import timerx.util.Constants.TimeValues;

public class Checker {

  public static void expectNotNull(Object o) {
    if (o == null) {
      throw new IllegalArgumentException("Value is null");
    }
  }

  public static void expectNotNull(Object o, String failMsg) {
    if (o == null) {
      throw new IllegalArgumentException(failMsg);
    }
  }

  public static void expectTimeNotNegative(long value) {
    if (value < 0) {
      throw new IllegalArgumentException("Time shouldn't be negative");
    }
  }

  public static void expectTimeInitialized(long time, String failMsg) {
    if (time == TimeValues.NONE) {
      throw new IllegalArgumentException(failMsg);
    }
  }

  public static void expect(boolean condition) {
    if (!condition) {
      throw new AssertionError();
    }
  }

  public static void expect(boolean condition, String failMsg) {
    if (!condition) {
      throw new AssertionError(failMsg);
    }
  }
}
