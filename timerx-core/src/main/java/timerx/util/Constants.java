package timerx.util;

import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;

@RestrictTo(Scope.LIBRARY)
public class Constants {

  @RestrictTo(Scope.LIBRARY)
  public static class Symbols {

    public static final char SYMBOL_HOURS = 'H';
    public static final char SYMBOL_MINUTES = 'M';
    public static final char SYMBOL_SECONDS = 'S';
    public static final char SYMBOL_REM_MILLIS = 'L';
    public static final char SYMBOL_ESCAPE = '#';
  }

  @RestrictTo(Scope.LIBRARY)
  public static class TimeValues {

    public static final long SECONDS_IN_MINUTE = 60;
    public static final long MINUTES_IN_HOUR = 60;
    public static final long MILLIS_IN_SECOND = 1000;
    public static final long MILLIS_IN_MINUTE = MILLIS_IN_SECOND * SECONDS_IN_MINUTE;
    public static final long MILLIS_IN_HOUR = MILLIS_IN_MINUTE * MINUTES_IN_HOUR;
    public static final long NONE = -1;
  }
}
