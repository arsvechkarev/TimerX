package timerx.format;

import androidx.annotation.NonNull;

public class Semantic {

  Position hoursPosition;
  Position minutesPosition;
  Position secondsPosition;
  Position rMillisPosition;
  TimeUnitType smallestAvailableUnit;

  String format;
  String strippedFormat;

  Semantic(@NonNull Position hoursPosition, @NonNull Position minutesPosition,
      @NonNull Position secondsPosition, @NonNull Position rMillisPosition,
      String format, String strippedFormat,
      TimeUnitType smallestAvailableUnit) {
    this.hoursPosition = hoursPosition;
    this.minutesPosition = minutesPosition;
    this.secondsPosition = secondsPosition;
    this.rMillisPosition = rMillisPosition;
    this.format = format;
    this.strippedFormat = strippedFormat;
    this.smallestAvailableUnit = smallestAvailableUnit;
  }

  @NonNull
  public String getFormat() {
    return strippedFormat;
  }

  boolean has(@NonNull TimeUnitType unitType) {
    switch (unitType) {
      case HOURS:
        return hoursPosition.isNotEmpty();
      case MINUTES:
        return minutesPosition.isNotEmpty();
      case SECONDS:
        return secondsPosition.isNotEmpty();
      case R_MILLISECONDS:
        return rMillisPosition.isNotEmpty();
    }
    throw new IllegalArgumentException("Incorrect type of unit");
  }

  boolean hasOnlyRMillis() {
    return (rMillisPosition.isNotEmpty() && secondsPosition.isEmpty()
        && minutesPosition.isEmpty() && hoursPosition.isEmpty());
  }
}