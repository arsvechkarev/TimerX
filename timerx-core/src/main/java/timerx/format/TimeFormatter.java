package timerx.format;

import static timerx.TimeUnits.HOURS;
import static timerx.TimeUnits.MINUTES;
import static timerx.TimeUnits.R_MILLISECONDS;
import static timerx.TimeUnits.SECONDS;
import static timerx.util.Constants.EMPTY_STRING;
import static timerx.util.Constants.Patterns.STR_HOURS;
import static timerx.util.Constants.Patterns.STR_MINUTES;
import static timerx.util.Constants.Patterns.STR_REM_MILLIS;
import static timerx.util.Constants.Patterns.STR_SECONDS;
import static timerx.util.Constants.STR_ZERO;
import static timerx.util.Constants.TimeValues.MILLIS_IN_HOUR;
import static timerx.util.Constants.TimeValues.MILLIS_IN_MINUTE;
import static timerx.util.Constants.TimeValues.MILLIS_IN_SECOND;
import static timerx.util.Constants.TimeValues.MINUTES_IN_HOUR;
import static timerx.util.Constants.TimeValues.NONE;
import static timerx.util.Constants.TimeValues.SECONDS_IN_MINUTE;

import androidx.annotation.NonNull;
import java.util.concurrent.TimeUnit;
import timerx.TimeUnits;
import timerx.Timer;
import timerx.util.Constants.Patterns;

/**
 * Main class that formatting input milliseconds into string representation according to
 * parse format. Parse format is a string that contains one of followed characters:
 * <p>"H" - represents hours</p>
 * <p>"M" - represents minutes</p>
 * <p>"S" - represents seconds</p>
 * <p>"L" - can represent milliseconds, centiseconds etc. (It depends on amount of the
 * symbols, detailed explanation later)</p><br/>
 *
 * For example, let's consider format like "MM:SS". It consist of hours, minutes and
 * seconds. So if current time is 1 minute and 37 seconds, result of formatting will be
 * "01:37", and if current time is 1 hour, 2 minutes and 9 seconds, result will be
 * "122:09" and so on.<br/><br/>
 *
 * If you need to use special format characters as a plain text, you can insert the escape
 * symbol "#", For example, if format is "HH#H MM#M", and time is 2 hours 47 minutes, then
 * result will be "02H 47M".<br/><br/>
 *
 * There are some formatting examples:
 *
 * <pre>
 *   | ------------------------------------------------------------ |
 *   |       Format       |  Time(milliseconds) |      Output       |
 *   | ------------------ | --------------------| ----------------- |
 *   |                    |        11478        |       00:11       |
 *   |       MM:SS        |        146229       |       02:26       |
 *   |                    |        8246387      |      137:26       |
 *   | ------------------ | ------------------- | ----------------- |
 *   |                    |        11478        |      00m 11s      |
 *   |      MMm SSs       |        146229       |      02m 26s      |
 *   |                    |        8246387      |      137m 26s     |
 *   | ------------------ | ------------------- | ----------------- |
 *   |                    |        394724       |       00:06       |
 *   |       HH:MM        |        8262249      |       02:17       |
 *   |                    |        71476396     |       19:51       |
 *   | ------------------ | ------------------- | ----------------- |
 *   |                    |        394724       |  00H 06M and 34S  |
 *   | HH#H MM#M and SS#S |        8262249      |  02H 17M and 42S  |
 *   |                    |        71476396     |  19H 51M and 16S  |
 *   | ------------------ | ------------------- | ----------------- |
 * </pre>
 *
 * Some formats are unacceptable. There are three types of such formats:
 * <p>
 * 1. Formats that not contain any special characters ("H", "M", "S" or "L").
 * </p>
 * <p>
 * 2. Formats that contain same special symbols in different positions. Example: "HH:HH",
 * or "MM:SS ML" (See {@link timerx.exceptions.IllegalSymbolsPositionException} for more
 * detailed information)
 * </p>
 * <p>
 * 3. Formats that contain <b>incompatible</b> symbols together. To find out what is
 * incompatible symbols, see {@link timerx.exceptions.IllegalSymbolsCombinationException}
 * </p><br/>
 *
 * Now, let's take a look to the character like "L". It can represents represents
 * milliseconds, centiseconds, and decisecond, depending on amount and other characters.
 * Consider format "M:SS.LL" and time 36698 milliseconds (36 seconds and 698
 * milliseconds). In this case, since amount of "L" characters is 2, last digit in
 * milliseconds is omitted, and result will be "0:36.69". In case if there is no special
 * symbols except "L", or it amount is three or more, then it will be formats as a plain
 * milliseconds.<br/><br/>
 *
 * Here some examples of formatting with "L" symbol:
 * <pre>
 *   | ---------------------------------------------- |
 *   |   Format    |  Time(milliseconds)  |  Output   |
 *   | ----------- | -------------------- | --------- |
 *   |             |         367          |   00:3    |
 *   |    SS:L     |         1322         |   01:3    |
 *   |             |         15991        |   15:9    |
 *   | ----------- | -------------------- | --------- |
 *   |             |         367          |   00:36   |
 *   |    SS:LL    |         1322         |   01:32   |
 *   |             |         15991        |   15:99   |
 *   | ----------- | -------------------- | --------- |
 *   |             |         367          |  00:367   |
 *   |    SS:LLL   |         1322         |  01:322   |
 *   |             |         15991        |  15:991   |
 *   | ----------- | -------------------- | --------- |
 *   |             |         367          |  00:0367  |
 *   |    SS:LLLL  |         1322         |  01:0322  |
 *   |             |         15991        |  15:0991  |
 *   | ----------- | -------------------- | --------- |
 *   |             |         367          |   0367    |
 *   |    LLLL     |         1322         |   1322    |
 *   |             |         15991        |   15991   |
 *   | ----------- | -------------------- | --------- |
 * </pre>
 *
 * @see Analyzer
 */
public class TimeFormatter {

  /**
   * Semantic representation of input format creates by {@link Analyzer}
   *
   * @see Semantic
   */
  private final Semantic semantic;

  /**
   * Temporary container for time units
   */
  private final TimeContainer timeContainer;

  /**
   * Input string format format
   */
  private String format;

  /**
   * Helper method to just format time.<br/> Usage example:
   * <pre>{@code
   *    String formattedTime = TimeFormatter.with("MMm SSs").format(5, TimeUnit.MINUTES);
   *    System.out.println(formattedTime) // Output: "05m 00s"
   * }</pre>
   */
  public static TimeFormatter with(String format) {
    return new TimeFormatter(Analyzer.check(format));
  }

  public TimeFormatter(@NonNull Semantic semantic) {
    this.semantic = semantic;
    this.format = semantic.getFormat();
    timeContainer = new TimeContainer();
  }

  /**
   * Returns optimized delay for handler in Timer and Stopwatch. <br/><br/> Optimized
   * delay calculates depending on which symbols contains in parse format. For example, if
   * input format contains {@link Patterns#STR_REM_MILLIS}, then delay for handler should
   * be every millisecond (on every ten milliseconds, depending on number of {@link
   * Patterns#STR_REM_MILLIS} symbols in format), but if input format do not contains
   * {@link Patterns#STR_REM_MILLIS}, there is no reason to notify user about changing
   * time every milliseconds, because millis will not displayed. But delay should be at
   * most 100 for correct pause/resume handling
   */
  public long getOptimizedDelay() {
    long delay = 100;
    if (semantic.has(R_MILLISECONDS)) {
      if (semantic.countOf(R_MILLISECONDS) == 2) {
        delay = 10;
      } else if (semantic.countOf(R_MILLISECONDS) > 2) {
        delay = 1;
      }
    }
    return delay;
  }

  /**
   * Returns minimum unit of semantic converted to millis
   *
   * @see Timer
   */
  public long minimumUnitInMillis() {
    if (semantic.minimumUnit() == R_MILLISECONDS) {
      return 1;
    } else if (semantic.minimumUnit() == SECONDS) {
      return MILLIS_IN_SECOND;
    } else if (semantic.minimumUnit() == MINUTES) {
      return MILLIS_IN_MINUTE;
    }
    return MILLIS_IN_HOUR;
  }

  /**
   * Returns current format of semantic
   */
  public String currentFormat() {
    return semantic.getFormat();
  }

  /**
   * Like {@link #format(long)}, but also converts time into particular time unit for more
   * convenient usage. Uses basically together with {@link #with(String)} method
   */
  public String format(long time, TimeUnit timeUnit) {
    return format(timeUnit.toMillis(time));
  }

  /**
   * Formats milliseconds to string representation according by {@link #format}
   *
   * @param time Time in milliseconds
   * @return Formatted time
   */
  @NonNull
  public String format(long time) {
    TimeContainer units = timeUnitsOf(time);
    long millisToShow = NONE;
    long secondsToShow = NONE;
    long minutesToShow = NONE;
    long hoursToShow = NONE;
    if (semantic.has(R_MILLISECONDS)) {
      millisToShow = (semantic.has(SECONDS)) ? units.remMillis : units.millis;
    }
    if (semantic.has(SECONDS)) {
      secondsToShow = (semantic.has(MINUTES)) ? units.remSeconds : units.seconds;
    }
    if (semantic.has(MINUTES)) {
      minutesToShow = (semantic.has(HOURS)) ? units.remMinutes : units.minutes;
    }
    if (semantic.has(HOURS)) {
      hoursToShow = units.hours;
    }
    return applyFormatOf(millisToShow, secondsToShow, minutesToShow, hoursToShow);
  }

  private TimeContainer timeUnitsOf(long millis) {
    long seconds = millis / MILLIS_IN_SECOND;
    long minutes = seconds / SECONDS_IN_MINUTE;
    long hours = minutes / MINUTES_IN_HOUR;
    long remMillis = millis % MILLIS_IN_SECOND;
    long remSeconds = seconds - minutes * SECONDS_IN_MINUTE;
    long remMinutes = minutes - hours * MINUTES_IN_HOUR;
    return timeContainer
        .setMillis(millis)
        .setSeconds(seconds)
        .setMinutes(minutes)
        .setHours(hours)
        .setRemMillis(remMillis)
        .setRemSeconds(remSeconds)
        .setRemMinutes(remMinutes);
  }

  private String applyFormatOf(long millisToShow, long secondsToShow,
      long minutesToShow, long hoursToShow) {
    String strHours = getFormatOf(hoursToShow, HOURS);
    String strMinutes = getFormatOf(minutesToShow, MINUTES);
    String strSeconds = getFormatOf(secondsToShow, SECONDS);
    String strMillis = getFormatOf(millisToShow, R_MILLISECONDS);

    format = semantic.patternHours.matcher(format).replaceAll(strHours);
    format = semantic.patternMinutes.matcher(format).replaceAll(strMinutes);
    format = semantic.patternSeconds.matcher(format).replaceAll(strSeconds);
    format = semantic.patternRMillis.matcher(format).replaceAll(strMillis);

    format = semantic.patternEscapedHours.matcher(format).replaceAll(STR_HOURS);
    format = semantic.patternEscapedMinutes.matcher(format).replaceAll(STR_MINUTES);
    format = semantic.patternEscapedSeconds.matcher(format).replaceAll(STR_SECONDS);
    format = semantic.patternEscapedRMillis.matcher(format).replaceAll(STR_REM_MILLIS);

    return format;
  }

  private String getFormatOf(long number, TimeUnits numberType) {
    if (number != NONE) {
      if (number == 0) {
        return zerosBy(semantic.countOf(numberType));
      }
      int semanticCount = semantic.countOf(numberType);
      int numberLength = lengthOf(number);
      int diff = semanticCount - numberLength;
      if (numberType == R_MILLISECONDS && !semantic.hasOnlyRMillis()) {
        // Format contains millis and something else, so formatting millis as rem_millis
        return formatRemMillis(number, semanticCount);
      }
      if (diff > 0) {
        return zerosBy(diff) + number;
      }
      // Diff < 0, so just returning number
      return number + EMPTY_STRING;
    }
    // Time is NONE, i.e it does not contained in format
    return EMPTY_STRING;
  }

  private String zerosBy(int num) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < num; i++) {
      builder.append(STR_ZERO);
    }
    return builder.toString();
  }

  private String formatRemMillis(long millis, int semanticCount) {
    String strMillis = formatToThreeOrMoreDigits(millis, semanticCount);
    if (semanticCount <= 2) {
      return strMillis.substring(0, semanticCount);
    }
    return strMillis;
  }

  private String formatToThreeOrMoreDigits(long millis, int semanticCount) {
    int numLength = lengthOf(millis);
    StringBuilder result = new StringBuilder();
    if (numLength == 1) {
      // Length = 1, so result "00X"
      result.append(STR_ZERO).append(STR_ZERO);
    }
    if (numLength == 2) {
      // Length = 2, so result "0XX"
      result.append(STR_ZERO);
    }
    int zerosToAdd = semanticCount - (result.length() + numLength);
    if (zerosToAdd > 0) {
      // Semantic requires more zeros, so inserting them
      result.insert(0, zerosBy(zerosToAdd)).append(millis);
    } else {
      result.append(millis);
    }
    return result.toString();
  }

  private int lengthOf(long number) {
    return (number + EMPTY_STRING).length();
  }
}
