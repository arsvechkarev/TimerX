package timerx;

import androidx.annotation.Nullable;

class Checker {

  static void assertNotNull(@Nullable Object o) {
    if (o == null) {
      throw new NullPointerException();
    }
  }

  static void assertNotNull(@Nullable Object o, String failMsg) {
    if (o == null) {
      throw new NullPointerException(failMsg);
    }
  }

  static void assertTimeNotNegative(long value) {
    if (value < 0) {
      throw new IllegalArgumentException("Time shouldn't be negative");
    }
  }

  static void assertThat(boolean condition) {
    if (!condition) {
      throw new IllegalStateException();
    }
  }

  static void assertThat(boolean condition, String failMsg) {
    if (!condition) {
      throw new IllegalArgumentException(failMsg);
    }
  }
}
