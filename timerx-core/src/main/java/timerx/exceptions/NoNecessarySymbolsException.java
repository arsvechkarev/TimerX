package timerx.exceptions;

/**
 * Exceptions throws when input format does not contains any special symbols like "H", "M",
 * "S" or "L"
 */
public class NoNecessarySymbolsException extends RuntimeException {

  public NoNecessarySymbolsException(String message) {
    super(message);
  }
}
