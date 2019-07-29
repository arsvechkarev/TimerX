package com.arsvechkarev.timerx.exceptions;

/**
 * Exceptions throws when input format does not contains any parse symbols like "H", "M",
 * "S" or "L"
 */
public class NoNecessarySymbolsException extends RuntimeException {

  public NoNecessarySymbolsException(String message) {
    super(message);
  }
}
