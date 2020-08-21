package timerx;

public enum TimeUnit {
  HOURS('H'),
  MINUTES('M'),
  SECONDS('S'),
  R_MILLISECONDS('L');

  private final char value;

  TimeUnit(char value) {
    this.value = value;
  }

  public char getValue() {
    return value;
  }
}
