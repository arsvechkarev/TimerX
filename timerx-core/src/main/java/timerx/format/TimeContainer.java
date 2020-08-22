package timerx.format;

import androidx.annotation.NonNull;

/**
 * Temporary holds time in different time units
 */
class TimeContainer {

  long millis;
  long seconds;
  long minutes;
  long hours;
  long remMillis;
  long remSeconds;
  long remMinutes;

  @NonNull
  TimeContainer setMillis(long millis) {
    this.millis = millis;
    return this;
  }

  @NonNull
  TimeContainer setSeconds(long seconds) {
    this.seconds = seconds;
    return this;
  }

  @NonNull
  TimeContainer setMinutes(long minutes) {
    this.minutes = minutes;
    return this;
  }

  @NonNull
  TimeContainer setHours(long hours) {
    this.hours = hours;
    return this;
  }

  @NonNull
  TimeContainer setRemMillis(long remMillis) {
    this.remMillis = remMillis;
    return this;
  }

  @NonNull
  TimeContainer setRemSeconds(long remSeconds) {
    this.remSeconds = remSeconds;
    return this;
  }

  @NonNull
  TimeContainer setRemMinutes(long remMinutes) {
    this.remMinutes = remMinutes;
    return this;
  }
}
