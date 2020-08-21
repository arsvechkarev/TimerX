package timerx.format2;

import timerx.format.TimeFormatter;

/**
 * Class for temporary holding different time units
 *
 * @see TimeFormatter
 */
class NewTimeContainer {

  long millis;
  long seconds;
  long minutes;
  long hours;
  long remMillis;
  long remSeconds;
  long remMinutes;

  NewTimeContainer setMillis(long millis) {
    this.millis = millis;
    return this;
  }

  NewTimeContainer setSeconds(long seconds) {
    this.seconds = seconds;
    return this;
  }

  NewTimeContainer setMinutes(long minutes) {
    this.minutes = minutes;
    return this;
  }

  NewTimeContainer setHours(long hours) {
    this.hours = hours;
    return this;
  }

  NewTimeContainer setRemMillis(long remMillis) {
    this.remMillis = remMillis;
    return this;
  }

  NewTimeContainer setRemSeconds(long remSeconds) {
    this.remSeconds = remSeconds;
    return this;
  }

  NewTimeContainer setRemMinutes(long remMinutes) {
    this.remMinutes = remMinutes;
    return this;
  }
}
