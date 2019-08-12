package timerx.util;

public class Constants {

  public static final String EMPTY_STRING = "";
  public static final String STR_ZERO = "0";

  public static class Patterns {

    public static final String STR_HOURS = "H";
    public static final String STR_MINUTES = "M";
    public static final String STR_SECONDS = "S";
    public static final String STR_REM_MILLIS = "L";
    public static final String PATTERN_HAS_HOURS = "(?<!#)" + STR_HOURS + "+";
    public static final String PATTERN_HAS_MINUTES = "(?<!#)" + STR_MINUTES + "+";
    public static final String PATTERN_HAS_SECONDS = "(?<!#)" + STR_SECONDS + "+";
    public static final String PATTERN_HAS_REM_MILLIS = "(?<!#)" + STR_REM_MILLIS + "+";
    public static final String SYMBOL_ESCAPE = "#";
    public static final String ESCAPED_HOURS = SYMBOL_ESCAPE + STR_HOURS;
    public static final String ESCAPED_MINUTES = SYMBOL_ESCAPE + STR_MINUTES;
    public static final String ESCAPED_SECONDS = SYMBOL_ESCAPE + STR_SECONDS;
    public static final String ESCAPED_REM_MILLIS = SYMBOL_ESCAPE + STR_REM_MILLIS;
  }

  public static class TimeValues {

    public static final long SECONDS_IN_MINUTE = 60;
    public static final long MINUTES_IN_HOUR = 60;
    public static final long MILLIS_IN_SECOND = 1000;
    public static final long MILLIS_IN_MINUTE = MILLIS_IN_SECOND * SECONDS_IN_MINUTE;
    public static final long MILLIS_IN_HOUR = MILLIS_IN_MINUTE * MINUTES_IN_HOUR;
    public static final long NONE = -1;
  }
}
