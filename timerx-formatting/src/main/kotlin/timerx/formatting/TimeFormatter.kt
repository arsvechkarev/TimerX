package timerx.formatting

/**
 * Main class for formatting input milliseconds into char sequence representation
 * according to parse format. Parse format is a string may contain special symbols, such
 * as:
 *
 * "H" - hours
 *
 * "M" - minutes
 *
 * "S" - seconds
 *
 * "L" - can be milliseconds, hundredths of second, etc. (It depends on amount of the
 * symbols, detailed explanation later)<br></br>
 *
 * For example, let's consider a format like "MM:SS". It contains minutes and seconds. If
 * current time is 1 minute and 37 seconds, result of formatting will be "01:37", and if
 * current time is 1 hour, 2 minutes and 9 seconds, result will be "122:09" and so
 * on.<br></br><br></br>
 *
 * If you need to use special format characters ("H", "M", "S", or "L") as a plain text,
 * you can put the escape symbol before these symbols "#", For example, if format is "HH#H
 * MM#M", and time is 2 hours 47 minutes, then result will be "02H 47M".<br></br><br></br>
 *
 * There are some formatting examples:
 *
 * <pre>
 * | ------------------------------------------------------------ |
 * |       Format       |  Time(milliseconds) |      Output       |
 * | ------------------ | --------------------| ----------------- |
 * |                    |        11478        |       00:11       |
 * |       MM:SS        |        146229       |       02:26       |
 * |                    |        8246387      |      137:26       |
 * | ------------------ | ------------------- | ----------------- |
 * |                    |        11478        |      00m 11s      |
 * |      MMm SSs       |        146229       |      02m 26s      |
 * |                    |        8246387      |      137m 26s     |
 * | ------------------ | ------------------- | ----------------- |
 * |                    |        394724       |       00:06       |
 * |       HH:MM        |        8262249      |       02:17       |
 * |                    |        71476396     |       19:51       |
 * | ------------------ | ------------------- | ----------------- |
 * |                    |        394724       |  00H 06M and 34S  |
 * | HH#H MM#M and SS#S |        8262249      |  02H 17M and 42S  |
 * |                    |        71476396     |  19H 51M and 16S  |
 * | ------------------ | ------------------- | ----------------- |
 * </pre>
 *
 * Some formats are unacceptable. There are three types of such formats:
 *
 * 1. Formats that don't contain any special characters ("H", "M", "S" or "L").
 *
 * 2. Formats that contain same special symbols in different positions. Example: "HH:HH",
 * or "MM:SS ML" (See [NonContiguousFormatSymbolsException] for
 * more detailed explanation)
 *
 * 3. Formats that contain **incompatible** symbols together. To find out what is
 * incompatible symbols, see [IllegalSymbolsCombinationException]
 * <br></br>
 *
 * Now, let's take a look at the character "L". It can be formatted as milliseconds,
 * hundredths of second or tenths of second.<br></br><br></br>
 *
 * If there is no other special symbols then "L" is formatted as milliseconds. If there is
 * other special symbols, then "L" is formatted depending on how many "L" characters are
 * in the format. If there is one character - it is formatted as tenths of second, two -
 * hundredths of second, three and more - milliseconds.<br></br><br></br>
 *
 * Consider format "M:SS.LL" and time 36698 milliseconds (36 seconds and 698
 * milliseconds). In this case, since the amount of "L" characters in the format is 2,
 * last digit of 698 milliseconds will be omitted, and the result will be
 * "0:36.69".<br></br><br></br>
 *
 * Here some examples of formatting with "L" symbol:
 * <pre>
 * | ---------------------------------------------- |
 * |   Format    |  Time(milliseconds)  |  Output   |
 * | ----------- | -------------------- | --------- |
 * |             |         367          |   00:3    |
 * |    SS:L     |         1322         |   01:3    |
 * |             |         15991        |   15:9    |
 * | ----------- | -------------------- | --------- |
 * |             |         367          |   00:36   |
 * |    SS:LL    |         1322         |   01:32   |
 * |             |         15991        |   15:99   |
 * | ----------- | -------------------- | --------- |
 * |             |         367          |  00:367   |
 * |    SS:LLL   |         1322         |  01:322   |
 * |             |         15991        |  15:991   |
 * | ----------- | -------------------- | --------- |
 * |             |         367          |  00:0367  |
 * |    SS:LLLL  |         1322         |  01:0322  |
 * |             |         15991        |  15:0991  |
 * | ----------- | -------------------- | --------- |
 * |             |         367          |   0367    |
 * |    LLLL     |         1322         |   1322    |
 * |             |         15991        |   15991   |
 * | ----------- | -------------------- | --------- |
 * </pre>
 *
 * @author Arseniy Svechkarev
 */
interface TimeFormatter {
  
  /**
   * Returns delay for timer/stopwatch based on what the format is. If [useExactDelay] is set to
   * true, then delay should be calculated **exactly** according to format. For example, if format
   * is "MM:SS", then delay should be 1 seconds. If [useExactDelay] is set to false then delay
   * might be less be 1 seconds, like 100 or milliseconds
   */
  fun getWaitingDelay(useExactDelay: Boolean): Long
  
  /**
   * Returns the format of the formatter
   */
  val format: String
  
  /**
   * Formats input milliseconds according to the format
   */
  fun format(millis: Long): CharSequence
  
  companion object {
  
    /**
     * Helper method for formatting time
     *
     * @param format Format to format time with
     * @param millis Time in milliseconds
     */
    fun format(format: String, millis: Long): CharSequence {
      return StringBuilderTimeFormatter(AnalyzerImpl.analyze(format)).format(millis)
    }
  }
}
