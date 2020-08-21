package timerx.format;

import static timerx.format.TimeUnitType.HOURS;
import static timerx.format.TimeUnitType.MINUTES;
import static timerx.format.TimeUnitType.R_MILLISECONDS;
import static timerx.format.TimeUnitType.SECONDS;
import static timerx.util.Constants.TimeValues.MILLIS_IN_HOUR;
import static timerx.util.Constants.TimeValues.MILLIS_IN_MINUTE;
import static timerx.util.Constants.TimeValues.MILLIS_IN_SECOND;
import static timerx.util.Constants.TimeValues.MINUTES_IN_HOUR;
import static timerx.util.Constants.TimeValues.NONE;
import static timerx.util.Constants.TimeValues.SECONDS_IN_MINUTE;

import androidx.annotation.NonNull;

/**
 * Main class for formatting input milliseconds into char sequence representation
 * according to parse format. Parse format is a string that contains one of the following
 * characters:
 * <p>"H" - hours</p>
 * <p>"M" - minutes</p>
 * <p>"S" - seconds</p>
 * <p>"L" - can be milliseconds, centiseconds etc. (It depends on amount of the
 * symbols, detailed explanation later)</p><br/>
 *
 * For example, let's consider a format like "MM:SS". It contains minutes and seconds. If
 * current time is 1 minute and 37 seconds, result of formatting will be "01:37", and if
 * current time is 1 hour, 2 minutes and 9 seconds, result will be "122:09" and so
 * on.<br/><br/>
 *
 * If you need to use special format characters ("H", "M", "S", or "L") as a plain text,
 * you can put the escape symbol before these symbols "#", For example, if format is "HH#H
 * MM#M", and time is 2 hours 47 minutes, then result will be "02H 47M".<br/><br/>
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
 * 1. Formats that don't contain any special characters ("H", "M", "S" or "L").
 * </p>
 * <p>
 * 2. Formats that contain same special symbols in different positions. Example: "HH:HH",
 * or "MM:SS ML" (See {@link timerx.exceptions.NonContiguousFormatSymbolsException} for
 * more detailed explanation)
 * </p>
 * <p>
 * 3. Formats that contain <b>incompatible</b> symbols together. To find out what is
 * incompatible symbols, see {@link timerx.exceptions.IllegalSymbolsCombinationException}
 * </p><br/>
 *
 * Now, let's take a look to the character like "L". It can be formatted as milliseconds,
 * centiseconds, and decisecond, depending on amount and other characters. Consider format
 * "M:SS.LL" and time 36698 milliseconds (36 seconds and 698 milliseconds). In this case,
 * since amount of "L" characters is 2, last digit of 698 milliseconds will be omitted,
 * and the result will be "0:36.69". In case if there is no special symbols except "L", or
 * it amount is three or more, then it will be formatted as milliseconds.<br/><br/>
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
 * @author Arseniy Svechkarev
 */
public class TimeFormatter {

  public final Semantic semantic;
  private final TimeContainer timeContainer = new TimeContainer();
  private final StringBuilder mutableString;

  public TimeFormatter(@NonNull Semantic semantic) {
    this.semantic = semantic;
    mutableString = new StringBuilder(semantic.strippedFormat.length());
    mutableString.append(semantic.strippedFormat);
  }

  public long getOptimizedDelay() {
    long delay = 100;
    if (semantic.has(R_MILLISECONDS)) {
      if (semantic.rMillisPosition.length() == 2) {
        delay = 10;
      } else if (semantic.rMillisPosition.length() > 2) {
        delay = 1;
      }
    }
    return delay;
  }

  @NonNull
  public String currentFormat() {
    return semantic.getFormat();
  }

  public long minimumUnitInMillis() {
    if (semantic.smallestAvailableUnit == R_MILLISECONDS) {
      return 1;
    } else if (semantic.smallestAvailableUnit == SECONDS) {
      return MILLIS_IN_SECOND;
    } else if (semantic.smallestAvailableUnit == MINUTES) {
      return MILLIS_IN_MINUTE;
    }
    return MILLIS_IN_HOUR;
  }

  @NonNull
  public CharSequence format(long millis) {
    TimeContainer units = timeUnitsOf(millis);
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
    return applyFormat(millisToShow, secondsToShow, minutesToShow, hoursToShow);
  }

  @NonNull
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

  @NonNull
  private CharSequence applyFormat(long millisToShow, long secondsToShow,
      long minutesToShow, long hoursToShow) {
    if (millisToShow != NONE) updateString(millisToShow, R_MILLISECONDS);
    if (secondsToShow != NONE) updateString(secondsToShow, SECONDS);
    if (minutesToShow != NONE) updateString(minutesToShow, MINUTES);
    if (hoursToShow != NONE) updateString(hoursToShow, HOURS);
    return mutableString;
  }

  private void updateString(long time, TimeUnitType timeUnitType) {
    Position position = positionOfUnit(timeUnitType);
    int timeLength = lengthOf(time);
    if (!semantic.hasOnlyRMillis() && timeUnitType == R_MILLISECONDS) {
      if (semantic.rMillisPosition.length() < 3 && timeLength < 3) {
        int difference = 3 - semantic.rMillisPosition.length();
        time /= Math.pow(10, difference);
      }
      if (semantic.rMillisPosition.length() < timeLength) {
        int difference = timeLength - semantic.rMillisPosition.length();
        time /= Math.pow(10, difference);
      }
    }
    int updatedTimeLength = lengthOf(time);
    int range = Math.max(position.end - position.start, updatedTimeLength - 1);
    for (int i = position.end; i >= position.end - range; i--) {
      char ch = (char) ('0' + (time % 10));
      if (i >= position.start) {
        mutableString.setCharAt(i, ch);
      } else {
        mutableString.insert(Math.max(i + 1, 0), ch);
      }
      time /= 10;
    }
  }

  @NonNull
  private Position positionOfUnit(TimeUnitType timeUnitType) {
    switch (timeUnitType) {
      case HOURS:
        return semantic.hoursPosition;
      case MINUTES:
        return semantic.minutesPosition;
      case SECONDS:
        return semantic.secondsPosition;
      case R_MILLISECONDS:
        return semantic.rMillisPosition;
    }
    throw new IllegalStateException();
  }

  private int lengthOf(long number) {
    int length = 0;
    long temp = 1;
    while (temp <= number) {
      length++;
      temp *= 10;
    }
    return length;
  }
}
