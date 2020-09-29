package timerx;

/**
 * Represents position indices of special symbols in a format. {@link #start} and {@link
 * #end} both could be equal to -1 if there is no symbols of necessary type
 */
class Position {

  final int start;
  final int end;

  Position(int start, int end) {
    if (start > end) {
      throw new IllegalArgumentException(
          "start value: " + start + " is more than end value: " + end);
    }
    this.start = start;
    this.end = end;
  }

  boolean isEmpty() {
    return start == -1 && end == -1;
  }

  boolean isNotEmpty() {
    return start != -1 && end != -1;
  }

  int length() {
    if (start == -1 && end == -1) {
      return 0;
    }
    return end - start + 1;
  }
}
