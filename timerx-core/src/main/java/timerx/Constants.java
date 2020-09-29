package timerx;

class Constants {

  static class Symbols {

    static final char SYMBOL_HOURS = 'H';
    static final char SYMBOL_MINUTES = 'M';
    static final char SYMBOL_SECONDS = 'S';
    static final char SYMBOL_REM_MILLIS = 'L';
    static final char SYMBOL_ESCAPE = '#';

    static boolean isOneOfSpecialSymbols(char c) {
      return c == SYMBOL_HOURS || c == SYMBOL_MINUTES || c == SYMBOL_SECONDS
          || c == SYMBOL_REM_MILLIS;
    }
  }

  static class TimeValues {

    static final long SECONDS_IN_MINUTE = 60;
    static final long MINUTES_IN_HOUR = 60;
    static final long MILLIS_IN_SECOND = 1000;
    static final long MILLIS_IN_MINUTE = MILLIS_IN_SECOND * SECONDS_IN_MINUTE;
    static final long MILLIS_IN_HOUR = MILLIS_IN_MINUTE * MINUTES_IN_HOUR;
    static final long NONE = -1;
  }
}
