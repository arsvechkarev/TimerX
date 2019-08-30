package timerx.exceptions;

/**
 * Exceptions throws when special symbols contain multiple times in input format, e.g. "MM
 * - MM" or "HH:MM:SS - HH". This exceptions throws because there is no unambiguous way to
 * replace special symbols with corresponding time. Note that format can contains
 * same special symbols together, but not separately ("MMM:SSS" - is correct format,
 * "MM:SS:MM" - not)
 *
 * If you want to use special symbols as a regular letters, you can escape them with
 * symbol "#". Examples: "#Hello HH:MM:SS", "HH#H MM#M SS#S"
 */
public class IllegalSymbolsPositionException extends RuntimeException {

  public IllegalSymbolsPositionException(String message) {
    super(message);
  }
}
