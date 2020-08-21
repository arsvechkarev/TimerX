package timerx;

import timerx.util.Constants.Patterns;

public final class TestHelper {

  public static String updateFormatIfNecessary(String inputFormat) {
    return inputFormat
        .replaceAll("H", Patterns.STR_HOURS)
        .replaceAll("M", Patterns.STR_MINUTES)
        .replaceAll("S", Patterns.STR_SECONDS)
        .replaceAll("L", Patterns.STR_REM_MILLIS)
        .replaceAll("#", Patterns.SYMBOL_ESCAPE);
  }
}
