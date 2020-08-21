package timerx.util;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import timerx.util.Constants.TimeValues;

@RestrictTo(Scope.LIBRARY)
public class Checker {

  public static void assertNotNull(@Nullable Object o) {
    if (o == null) {
      throw new IllegalArgumentException("Value is null");
    }
  }

  public static void assertNotNull(@Nullable Object o, String failMsg) {
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

  public static void assertThat(boolean condition) {
    if (!condition) {
      throw new AssertionError();
    }
  }
}
