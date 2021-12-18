package timerx.formatting

/**
 * This exception is thrown when special symbols are contained multiple times in the input
 * format, e.g. "MM - MM" or "HH:MM:SS - HH". The exceptions is thrown because there is no
 * unambiguous way to replace special symbols with corresponding time. Keep in mind that
 * format can contain same special symbols together, but not separately ("MMM:SSS" - is
 * correct format, "MM:SS:MM" - not)
 *
 * If you want to use special symbols as a regular letters, you can escape them with
 * symbol "#". Examples: "#Hello HH:MM:SS", "HH#H MM#M SS#S"
 */
class NonContiguousFormatSymbolsException(message: String?) : RuntimeException(message)
