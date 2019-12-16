package timerx.exceptions;

/**
 * Exception thrown when input format does not contain any special symbols like "H", "M",
 * "S" or "L"
 */
public class NoNecessarySymbolsException extends RuntimeException {

  public NoNecessarySymbolsException(String message) {
    super(message);
  }
}
