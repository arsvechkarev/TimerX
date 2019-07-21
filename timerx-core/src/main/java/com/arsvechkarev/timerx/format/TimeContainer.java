package com.arsvechkarev.timerx.format;

public class TimeContainer {

  long millis;
  long seconds;
  long minutes;
  long hours;
  long remMillis;
  long remSeconds;
  long remMinutes;

  public TimeContainer setMillis(long millis) {
    this.millis = millis;
    return this;
  }

  public TimeContainer setSeconds(long seconds) {
    this.seconds = seconds;
    return this;
  }

  public TimeContainer setMinutes(long minutes) {
    this.minutes = minutes;
    return this;
  }

  public TimeContainer setHours(long hours) {
    this.hours = hours;
    return this;
  }

  public TimeContainer setRemMillis(long remMillis) {
    this.remMillis = remMillis;
    return this;
  }

  public TimeContainer setRemSeconds(long remSeconds) {
    this.remSeconds = remSeconds;
    return this;
  }

  public TimeContainer setRemMinutes(long remMinutes) {
    this.remMinutes = remMinutes;
    return this;
  }
}
