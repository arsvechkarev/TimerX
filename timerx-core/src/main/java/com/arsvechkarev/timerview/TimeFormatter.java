package com.arsvechkarev.timerview;

class TimeFormatter {

  private static final long MILLIS_IN_SECOND = 1000;
  private static final long MILLIS_IN_MINUTE = MILLIS_IN_SECOND * 60;
  private static final long MILLIS_IN_HOUR = MILLIS_IN_MINUTE * 60;
  private static final long SECONDS_IN_MINUTE = 60;
  private static final long MINUTES_IN_HOUR = 60;

  private final SemanticHolder semanticHolder;

  TimeFormatter(String inputFormat) {
    Validator validator = Validator.check(inputFormat);
    semanticHolder = SemanticHolder.of(validator);
  }

  String format(long millis) {
    return millis + "";
  }

  private TimeUnits timeUnitsFrom(long millis) {
//    if (semanticHolder.hasOnlyHours()) {
//      long hours = millis / MILLIS_IN_HOUR;
//    }
    return null;
  }


    /*long hours = millis / MILLIS_IN_HOUR;
      long minutes = millis / MILLIS_IN_MINUTE;
      long remMinutes = minutes - hours * MINUTES_IN_HOUR;
      long seconds = millis / MILLIS_IN_SECOND;
      long remSeconds = seconds - minutes * SECONDS_IN_MINUTE;
      long remMillis = millis / MILLIS_IN_SECOND;*/

  enum TimeUnits {
    ;
    static long hours;
    static long minutes;
    static long seconds;
    static long millis;
  }
}
