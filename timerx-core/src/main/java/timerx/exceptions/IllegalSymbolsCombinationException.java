package timerx.exceptions;

/**
 * Exception throws when symbols in input format are in unacceptable combination,
 * Unacceptable combination is when format contains only hours and seconds, and not
 * contains minutes, like "HH:SS". Or format contains only minutes and milliseconds and
 * not contains seconds, like "MM:LLL". Such formats considered wrong, because there is no
 * definite way to format remaining time, and in most cases these formats are
 * unusual.<br/><br/>
 *
 * Full list of unsupported symbols combination presented below, but there are two notes
 * to this:
 * <p>1. Positions of particular symbols or number of special symbols doesn't matter,
 * e.g. since "HH:LL" format is incorrect, then "L:HHH" format is also incorrect)</p>
 * <p>2. Escaped symbols like "#H" or "#S" is not considered as an special symbols.
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
