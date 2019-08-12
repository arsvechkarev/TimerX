package timerx.exceptions;

/**
 * Exception throws when symbols in input format are in unsupported position, when format
 * contains only hours and seconds, or only minutes and seconds, e.g. "HH:SS" or "MM:LL".
 * Such formats considered wrong, because there is no definite way to parse it, and in
 * most cases these formats are unusual. Full list of unsupported formats (positions of
 * particular symbols or number of parse symbols doesn't matter, e.g. if "HH:LL" format is
 * incorrect, then "L:HHH" format is also incorrect):
 * <pre>"HS", "ML", "HL", "HSL", "HML"</pre>
 */
public class IllegalSymbolsCombinationException extends RuntimeException {

  public IllegalSymbolsCombinationException(String message) {
    super(message);
  }
}
