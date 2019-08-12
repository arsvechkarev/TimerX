package timerx.format;

/**
 * Class for temporary containing of different time units
 *
 * @see TimeFormatter
 */
class TimeContainer {

  long millis;
  long seconds;
  long minutes;
  long hours;
  long remMillis;
  long remSeconds;
  long remMinutes;

  TimeContainer setMillis(long millis) {
    this.millis = millis;
    return this;
  }

  TimeContainer setSeconds(long seconds) {
    this.seconds = seconds;
    return this;
  }

  TimeContainer setMinutes(long minutes) {
    this.minutes = minutes;
    return this;
  }

  TimeContainer setHours(long hours) {
    this.hours = hours;
    return this;
  }

  TimeContainer setRemMillis(long remMillis) {
    this.remMillis = remMillis;
    return this;
  }

  TimeContainer setRemSeconds(long remSeconds) {
    this.remSeconds = remSeconds;
    return this;
  }

  TimeContainer setRemMinutes(long remMinutes) {
    this.remMinutes = remMinutes;
    return this;
  }
}
