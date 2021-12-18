package timerx.formatting

/**
 * This exception is thrown when symbols in input format are in unacceptable combination,
 * such as:<br></br>
 * 1) Input format has hours with seconds or milliseconds, but does not have minutes.<br></br>
 * 2) Input format has hours, minutes, and milliseconds, but does not have seconds<br></br>
 * 3) Input format has minutes and milliseconds, but does not have seconds
 */
class IllegalSymbolsCombinationException(message: String?) : RuntimeException(message)
