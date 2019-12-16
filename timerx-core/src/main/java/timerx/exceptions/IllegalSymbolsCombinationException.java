package timerx.exceptions;

/**
 * Exception thrown when symbols in input format are in unacceptable combination, e.g.
 * when format contains only hours and seconds, and not contains minutes, like "HH:SS", or
 * format contains only minutes and milliseconds and does not contain seconds, like "MM:LLL".
 * Such formats are considered wrong, because there is no definite way to format remaining
 * time, and in most cases these formats are not needed.<br/><br/>
 *
 * Full list of unsupported symbols combination presented below, but there are two notes
 * to this:
 * <p>1. Positions of particular symbols or number of special symbols doesn't matter,
 * e.g. "HH:LL" format is incorrect, and "L:HHH" format is incorrect either)</p>
 * <p>2. Escaped symbols like "#H" or "#S" are not considered as special symbols.
 * For example, format "HH:MM.L" is incorrect, but escaping last symbol "L", like
 * "HH:MM#L" makes this format correct</p>.
 *
 * Unacceptable formats:
 * <pre>
 *   "HS", "ML", "HL", "HSL", "HML"
 * </pre>
 */
public class IllegalSymbolsCombinationException extends RuntimeException {

  public IllegalSymbolsCombinationException(String message) {
    super(message);
  }
}
