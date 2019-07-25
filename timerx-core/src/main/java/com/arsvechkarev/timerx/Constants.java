package com.arsvechkarev.timerx;

public class Constants {

  public static final String EMPTY_STRING = "";
  public static final String ZERO = "0";

  public static class Patterns {

    public static final String STANDARD_HOURS = "H";
    public static final String STANDARD_MINUTES = "M";
    public static final String STANDARD_SECONDS = "S";
    public static final String STANDARD_MILLIS = "L";
    public static final String PATTERN_HAS_HOURS = "(?<!#)" + STANDARD_HOURS + "+";
    public static final String PATTERN_HAS_MINUTES = "(?<!#)" + STANDARD_MINUTES + "+";
    public static final String PATTERN_HAS_SECONDS = "(?<!#)" + STANDARD_SECONDS + "+";
    public static final String PATTERN_HAS_MILLIS = "(?<!#)" + STANDARD_MILLIS + "+";
    static final String SYMBOL_ESCAPE = "#";
    public static final String ESCAPED_HOURS = SYMBOL_ESCAPE + STANDARD_HOURS;
    public static final String ESCAPED_MINUTES = SYMBOL_ESCAPE + STANDARD_MINUTES;
    public static final String ESCAPED_SECONDS = SYMBOL_ESCAPE + STANDARD_SECONDS;
    public static final String ESCAPED_MILLIS = SYMBOL_ESCAPE + STANDARD_MILLIS;


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
