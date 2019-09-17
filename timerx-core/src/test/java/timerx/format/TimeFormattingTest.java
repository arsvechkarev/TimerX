package timerx.format;

import static org.junit.Assert.assertEquals;
import static timerx.TestHelper.updateFormatIfNecessary;
import static timerx.util.Constants.TimeValues.MILLIS_IN_HOUR;
import static timerx.util.Constants.TimeValues.MILLIS_IN_MINUTE;
import static timerx.util.Constants.TimeValues.MILLIS_IN_SECOND;

import java.util.concurrent.TimeUnit;
import org.junit.Test;

@SuppressWarnings("SpellCheckingInspection")
public class TimeFormattingTest {

  private static TimeFormatter formatTime(String format) {
    return new TimeFormatter(Analyzer.check(updateFormatIfNecessary(format)));
  }

  private String formatTime(String format, long millis) {
    return new TimeFormatter(Analyzer.check(updateFormatIfNecessary(format)))
        .format(millis);
  }

  private long millisOf(long hours, long minutes, long seconds, long millis) {
    long result = 0;
    result += hours * MILLIS_IN_HOUR;
    result += minutes * MILLIS_IN_MINUTE;
    result += seconds * MILLIS_IN_SECOND;
    result += millis;
    return result;
  }

  @Test
  public void straightForwardFormatTest() {
    String format = "MM:SS.L";
    assertEquals("01:00.0", TimeFormatter.with(format).format(1, TimeUnit.MINUTES));
  }

  @Test
  public void optimizedDelayTest1() {
    String format = "MM:SS:LLLLL";
    assertEquals(1, formatTime(format).getOptimizedDelay());
  }

  @Test
  public void optimizedDelayTest2() {
    String format = "MM:SS:LLL";
    assertEquals(1, formatTime(format).getOptimizedDelay());
  }

  @Test
  public void optimizedDelayTest3() {
    String format = "MM:SS:LL";
    assertEquals(10, formatTime(format).getOptimizedDelay());
  }

  @Test
  public void optimizedDelayTest4() {
    String format = "MM:SS:L";
    assertEquals(100, formatTime(format).getOptimizedDelay());
  }

  @Test
  public void optimizedDelayTest5() {
    String format = "MM:SS::";
    assertEquals(100, formatTime(format).getOptimizedDelay());
  }

  @Test
  public void minimumUnitInMillis1() {
    Semantic semantic = Analyzer.check("HH");
    TimeFormatter timeFormatter = new TimeFormatter(semantic);
    assertEquals(MILLIS_IN_HOUR, timeFormatter.minimumUnitInMillis());
  }

  @Test
  public void minimumUnitInMillis2() {
    Semantic semantic = Analyzer.check("HH - MM");
    TimeFormatter timeFormatter = new TimeFormatter(semantic);
    assertEquals(MILLIS_IN_MINUTE, timeFormatter.minimumUnitInMillis());
  }

  @Test
  public void minimumUnitInMillis3() {
    Semantic semantic = Analyzer.check("MM:SS");
    TimeFormatter timeFormatter = new TimeFormatter(semantic);
    assertEquals(MILLIS_IN_SECOND, timeFormatter.minimumUnitInMillis());
  }

  @Test
  public void minimumUnitInMillis4() {
    Semantic semantic = Analyzer.check("HH:MM:SS:LL");
    TimeFormatter timeFormatter = new TimeFormatter(semantic);
    assertEquals(1, timeFormatter.minimumUnitInMillis());
  }

  @Test
  public void currentFormatTest() {
    Semantic semantic = Analyzer.check("MM:SS");
    TimeFormatter timeFormatter = new TimeFormatter(semantic);
    assertEquals("MM:SS", timeFormatter.currentFormat());
  }

  @Test
  public void zeroTest() {
    String format = "MM:SS.LL";
    long millis = 0;
    assertEquals("00:00.00", formatTime(format, millis));
  }

  @Test
  public void soloMillisTest1() {
    String format = "LLLLL";
    long millis = 1200;
    assertEquals("01200", formatTime(format, millis));
  }

  @Test
  public void soloMillisTest2() {
    String format = "LLLL";
    long millis = 1599;
    assertEquals("1599", formatTime(format, millis));
  }

  @Test
  public void soloMillisTest3() {
    String format = "LLL";
    long millis = 159999;
    assertEquals("159999", formatTime(format, millis));
  }

  @Test
  public void soloSeconds() {
    String format = "SSS";
    long millis = millisOf(0, 1, 3, 789);
    assertEquals("063", formatTime(format, millis));
  }

  @Test
  public void soloMinutes() {
    String format = "MM min";
    long millis = millisOf(0, 49, 35, 123);
    assertEquals("49 min", formatTime(format, millis));
  }

  @Test
  public void soloHours() {
    String format = "HHH_";
    long millis = millisOf(55, 6, 3, 789);
    assertEquals("055_", formatTime(format, millis));
  }

  @Test
  public void formatWithRemMillis1() {
    String format = "HHH_MMM qwerty SS lol LLL";
    long millis = millisOf(36, 6, 3, 19);
    assertEquals("036_006 qwerty 03 lol 019", formatTime(format, millis));
  }

  @Test
  public void formatWithRemMillis2() {
    String format = "MM:SS:LLL";
    long millis = millisOf(0, 19, 29, 11);
    assertEquals("19:29:011", formatTime(format, millis));
  }

  @Test
  public void formatWithRemMillis3() {
    String format = "SS lol LLL";
    long millis = millisOf(0, 13, 29, 1);
    assertEquals("809 lol 001", formatTime(format, millis));
  }

  @Test
  public void formatWithRemMillis4() {
    String format = "SS lol LLLLLL";
    long millis = millisOf(0, 13, 29, 2);
    assertEquals("809 lol 000002", formatTime(format, millis));
  }

  @Test
  public void formatWithRemMillis5() {
    String format = "SS lol L";
    long millis = millisOf(0, 0, 0, 17);
    assertEquals("00 lol 0", formatTime(format, millis));
  }

  @Test
  public void standardFormat2() {
    String format = "HHh MMm SSs";
    long millis = millisOf(2, 55, 59, 0);
    assertEquals("02h 55m 59s", formatTime(format, millis));
  }

  @Test
  public void standardFormat3() {
    String format = "HH bang MM";
    long millis = millisOf(1, 12, 36, 2);
    assertEquals("01 bang 12", formatTime(format, millis));
  }

  @Test
  public void standardFormat1() {
    String format = "MM blah SS";
    long millis = millisOf(0, 37, 5, 28);
    assertEquals("37 blah 05", formatTime(format, millis));
  }

  @Test
  public void formatWithEscaping1() {
    String format = "HH#H MM#M SS#S LL#L#L";
    long millis = millisOf(4, 7, 49, 219);
    assertEquals("04H 07M 49S 21LL", formatTime(format, millis));
  }

  @Test
  public void formatWithEscaping2() {
    String format = "MM#M SS#S";
    long millis = millisOf(0, 31, 5, 2);
    assertEquals("31M 05S", formatTime(format, millis));
  }

  @Test
  public void formatWithEscaping3() {
    String format = "#Hello +  SS:LL";
    long millis = millisOf(0, 1, 22, 167);
    assertEquals("Hello +  82:16", formatTime(format, millis));
  }

  @Test
  public void formatWithEscaping4() {
    String format = "#LA#LA#LA : MM-SS";
    long millis = millisOf(0, 8, 5, 23);
    assertEquals("LALALA : 08-05", formatTime(format, millis));
  }
}
