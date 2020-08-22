package timerx.exceptions;

// @formatter_off
/**
 * This exception is thrown when symbols in input format are in unacceptable combination,
 * such as:<br/>
 * 1) Input format has hours with seconds or milliseconds, but does not have minutes.<br/>
 * 2) Input format has hours, minutes, and milliseconds, but does not have seconds<br/>
 * 3) Input format has minutes and milliseconds, but does not have seconds
 */
public class IllegalSymbolsCombinationException extends RuntimeException {

  public IllegalSymbolsCombinationException(String message) {
    super(message);
  }
}
