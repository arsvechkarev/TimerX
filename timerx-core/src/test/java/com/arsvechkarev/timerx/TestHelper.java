package com.arsvechkarev.timerx;

import com.arsvechkarev.timerx.util.Constants.Patterns;
import org.junit.Assert;
import org.junit.Test;

public final class TestHelper {

  public static String updateFormatIfNecessary(String inputFormat) {
    return inputFormat
        .replaceAll("H", Patterns.STANDARD_HOURS)
        .replaceAll("M", Patterns.STANDARD_MINUTES)
        .replaceAll("S", Patterns.STANDARD_SECONDS)
        .replaceAll("L", Patterns.STANDARD_R_MILLIS)
        .replaceAll("#", Patterns.SYMBOL_ESCAPE);
  }

  public static String mockedFormatOf(String inputFormat) {
    return inputFormat
        .replaceAll("H", "C")
        .replaceAll("M", "B")
        .replaceAll("S", "K")
        .replaceAll("L", "R")
        .replaceAll("#", "@");
  }

  @Test
  public void test() {
    String format = "HH #H @S  MMM@M#M #### LLLL#L#L#L@@@@L";
    Assert.assertEquals("CC @C @K  BBB@B@B @@@@ RRRR@R@R@R@@@@R", mockedFormatOf(format));
  }

}
