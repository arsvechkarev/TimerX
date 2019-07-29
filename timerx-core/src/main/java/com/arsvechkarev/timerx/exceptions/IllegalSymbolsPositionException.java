package com.arsvechkarev.timerx.exceptions;

/**
 * Exceptions throws when parse symbol contains multiple times in input format, e.g. "MM -
 * MM" or "HH:MM:SS - HH". This exceptions throws because there is no unambiguous symbols
 * to replace with time. Note that same format can contains same parse symbols together,
 * but not separately ("MMM:SSS" - is correct format, "MM:SS:MM" - not)
 *
 * If you want to use parse symbols as a regular letters, you can escape them with symbol
 * "#". Examples: "#Hello HH:MM:SS", "HH#H MM#M SS#S"
 */
public class IllegalSymbolsPositionException extends RuntimeException {

  public IllegalSymbolsPositionException(String message) {
    super(message);
  }
}
