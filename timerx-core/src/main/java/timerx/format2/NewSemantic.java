package timerx.format2;

import androidx.annotation.NonNull;
import timerx.TimeUnit;

class NewSemantic {

  Position hoursPosition;
  Position minutesPosition;
  Position secondsPosition;
  Position rMillisPosition;
  TimeUnit smallestAvailableUnit;

  String format;
  String strippedFormat;

  NewSemantic(@NonNull Position hoursPosition, @NonNull Position minutesPosition,
      @NonNull Position secondsPosition, @NonNull Position rMillisPosition,
      String format, String strippedFormat,
      TimeUnit smallestAvailableUnit) {
    this.hoursPosition = hoursPosition;
    this.minutesPosition = minutesPosition;
    this.secondsPosition = secondsPosition;
    this.rMillisPosition = rMillisPosition;
    this.format = format;
    this.strippedFormat = strippedFormat;
    this.smallestAvailableUnit = smallestAvailableUnit;
  }

  boolean has(TimeUnit unitType) {
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