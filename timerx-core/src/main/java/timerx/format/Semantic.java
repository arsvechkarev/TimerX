package timerx.format;

import timerx.TimeUnits;

/**
 * Provides various information about input format (including number of different parse
 * symbols etc)
 *
 * @see Analyzer
 * @see TimeFormatter
 */
public class Semantic {

  private final String format;

  private int hoursCount = 0;
  private int minutesCount = 0;
  private int secondsCount = 0;
  private int rMillisCount = 0;

  private TimeUnits minimumUnit;

  Semantic(String format) {
    this.format = format;
  }

  void setHoursCount(int hoursCount) {
    this.hoursCount = hoursCount;
  }

  void setMinutesCount(int minutesCount) {
    this.minutesCount = minutesCount;
  }

  void setSecondsCount(int secondsCount) {
    this.secondsCount = secondsCount;
  }

  void setRMillisCount(int rMillisCount) {
    this.rMillisCount = rMillisCount;
  }

  void setMinimumUnit(TimeUnits unit) {
    this.minimumUnit = unit;
  }

  public TimeUnits minimumUnit() {
    return minimumUnit;
  }

  public String getFormat() {
    return format;
  }

  boolean has(TimeUnits unitType) {
    switch (unitType) {
      case HOURS:
        return hoursCount > 0;
      case MINUTES:
        return minutesCount > 0;
      case SECONDS:
        return secondsCount > 0;
      case R_MILLISECONDS:
        return rMillisCount > 0;
      default:
        throw new IllegalArgumentException("Incorrect type of unit");
    }
  }

  boolean hasOnlyRMillis() {
    return (rMillisCount > 0 && secondsCount == 0
        && minutesCount == 0 && hoursCount == 0);
  }

  int countOf(TimeUnits unitType) {
    switch (unitType) {
      case HOURS:
        return hoursCount;
      case MINUTES:
        return minutesCount;
      case SECONDS:
        return secondsCount;
      case R_MILLISECONDS:
        return rMillisCount;
      default:
        throw new IllegalArgumentException("Incorrect type of unit");
    }
  }

}
