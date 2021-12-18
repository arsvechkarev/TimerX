package timerx.formatting

import org.junit.Assert.assertEquals
import org.junit.Test
import timerx.formatting.AnalyzerImpl.analyze
import timerx.formatting.TestHelper.updateFormatIfNeeded
import java.util.concurrent.TimeUnit

class TimeFormatterTest {
  
  @Test
  fun straightforwardTest() {
    val result: CharSequence = TimeFormatter.format("SS:LL", 12365.millis).toString()
    assertEquals("12:36", result)
  }
  
  @Test
  fun waitingDelayTest1() {
    val format = "MM:SS:LLLLL"
    assertEquals(1.millis, getFormatter(format).getWaitingDelay(useExactDelay = true))
    assertEquals(1.millis, getFormatter(format).getWaitingDelay(useExactDelay = false))
  }
  
  @Test
  fun waitingDelayTest2() {
    val format = "MM:SS:LLL"
    assertEquals(1.millis, getFormatter(format).getWaitingDelay(useExactDelay = true))
    assertEquals(1.millis, getFormatter(format).getWaitingDelay(useExactDelay = false))
  }
  
  @Test
  fun waitingDelayTest3() {
    val format = "MM:SS:LL"
    assertEquals(10.millis, getFormatter(format).getWaitingDelay(useExactDelay = true))
    assertEquals(10.millis, getFormatter(format).getWaitingDelay(useExactDelay = false))
  }
  
  @Test
  fun waitingDelayTest4() {
    val format = "MM:SS:L"
    assertEquals(100.millis, getFormatter(format).getWaitingDelay(useExactDelay = true))
    assertEquals(100.millis, getFormatter(format).getWaitingDelay(useExactDelay = false))
  }
  
  @Test
  fun waitingDelayTest5() {
    val format = "MM:SS::"
    assertEquals(1.seconds, getFormatter(format).getWaitingDelay(useExactDelay = true))
    assertEquals(100.millis, getFormatter(format).getWaitingDelay(useExactDelay = false))
  }
  
  @Test
  fun waitingDelayTest6() {
    val format = "HH:MM"
    assertEquals(1.minutes, getFormatter(format).getWaitingDelay(useExactDelay = true))
    assertEquals(100.millis, getFormatter(format).getWaitingDelay(useExactDelay = false))
  }
  
  @Test
  fun currentFormatTest() {
    val semantic = analyze("MM:SS")
    val timeFormatter = StringBuilderTimeFormatter(semantic)
    assertEquals("MM:SS", timeFormatter.format)
  }
  
  @Test
  fun zeroTest() {
    val format = "MM:SS.LL"
    assertEquals("00:00.00", formatTime(format, 0.millis))
  }
  
  @Test
  fun soloMillisTest1() {
    val format = "LLLLL"
    assertEquals("01200", formatTime(format, 1200.millis))
  }
  
  @Test
  fun soloMillisTest2() {
    val format = "LLLL"
    assertEquals("1599", formatTime(format, 1599.millis))
  }
  
  @Test
  fun soloMillisTest3() {
    val format = "LLL"
    assertEquals("159999", formatTime(format, 159999.millis))
  }
  
  @Test
  fun soloSeconds() {
    val format = "SSS"
    val millis = time(minutes = 1, seconds = 3, millis = 789)
    assertEquals("063", formatTime(format, millis))
  }
  
  @Test
  fun soloMinutes() {
    val format = "MM min"
    val millis = time(minutes = 49, seconds = 35, millis = 123)
    assertEquals("49 min", formatTime(format, millis))
  }
  
  @Test
  fun soloHours() {
    val format = "HHH_"
    val millis = time(hours = 55, minutes = 6, seconds = 3, millis = 789)
    assertEquals("055_", formatTime(format, millis))
  }
  
  @Test
  fun formatWithRemMillis1() {
    val format = "HHH_MMM qwerty SS lol LLL"
    val millis = time(hours = 36, minutes = 6, seconds = 3, millis = 19)
    assertEquals("036_006 qwerty 03 lol 019", formatTime(format, millis))
  }
  
  @Test
  fun formatWithRemMillis2() {
    val format = "MM:SS:LLL"
    val millis = time(minutes = 19, seconds = 29, millis = 11)
    assertEquals("19:29:011", formatTime(format, millis))
  }
  
  @Test
  fun formatWithRemMillis3() {
    val format = "SS lol LLL"
    val millis = time(minutes = 13, seconds = 29, millis = 1)
    assertEquals("809 lol 001", formatTime(format, millis))
  }
  
  @Test
  fun formatWithRemMillis4() {
    val format = "SS lol LLLLLL"
    val millis = time(minutes = 13, seconds = 29, millis = 2)
    assertEquals("809 lol 000002", formatTime(format, millis))
  }
  
  @Test
  fun formatWithRemMillis5() {
    val format = "SS lol L"
    assertEquals("00 lol 0", formatTime(format, 17.millis))
  }
  
  @Test
  fun formatWithRemMillis6() {
    val format = "SS lol LL"
    assertEquals("00 lol 01", formatTime(format, 17.millis))
  }
  
  @Test
  fun standardFormat2() {
    val format = "HHh MMm SSs"
    val millis = time(hours = 2, minutes = 55, seconds = 59)
    assertEquals("02h 55m 59s", formatTime(format, millis))
  }
  
  @Test
  fun standardFormat3() {
    val format = "HH bang MM"
    val millis = time(hours = 1, minutes = 12, seconds = 36, millis = 2)
    assertEquals("01 bang 12", formatTime(format, millis))
  }
  
  @Test
  fun standardFormat1() {
    val format = "MM blah SS"
    val millis = time(minutes = 37, seconds = 5, millis = 28)
    assertEquals("37 blah 05", formatTime(format, millis))
  }
  
  @Test
  fun formatWithEscaping1() {
    val format = "HH#H MM#M SS#S LL#L#L"
    val millis = time(hours = 4, minutes = 7, seconds = 49, millis = 219)
    assertEquals("04H 07M 49S 21LL", formatTime(format, millis))
  }
  
  @Test
  fun formatWithEscaping2() {
    val format = "MM#M SS#S"
    val millis = time(minutes = 31, seconds = 5, millis = 2)
    assertEquals("31M 05S", formatTime(format, millis))
  }
  
  @Test
  fun formatWithEscaping3() {
    val format = "#Hello +  SS:LL"
    val millis = time(minutes = 1, seconds = 22, millis = 167)
    assertEquals("Hello +  82:16", formatTime(format, millis))
  }
  
  @Test
  fun formatWithEscaping4() {
    val format = "#LA#LA#LA : MM-SS"
    val millis = time(minutes = 8, seconds = 5, millis = 23)
    assertEquals("LALALA : 08-05", formatTime(format, millis))
  }
  
  @Test
  fun formatWithEscaping5() {
    val format = "#Hello ## SS:LL"
    val millis = time(seconds = 36, millis = 23)
    assertEquals("Hello ## 36:02", formatTime(format, millis))
  }
  
  @Test
  fun multipleFormats1() {
    val format = "SS:LLL"
    val formatter = getFormatter(format)
    
    assertEquals("99:077", formatter.formatToString(99077.millis))
    
    assertEquals("101:049", formatter.formatToString(101049.millis))
    
    assertEquals("105:921", formatter.formatToString(105921.millis))
    
    assertEquals("883310:210", formatter.formatToString(883310210.millis))
  }
  
  @Test
  fun multipleFormats2() {
    val format = "SS:LLL"
    val formatter = getFormatter(format)
    
    assertEquals("99:077", formatter.formatToString(99077.millis))
    
    assertEquals("00:035", formatter.formatToString(35.millis))
    
    assertEquals("101:049", formatter.formatToString(101049.millis))
    
    assertEquals("883310:210", formatter.formatToString(883310210.millis))
  }
  
  private fun TimeFormatter.formatToString(millis: Long) = format(millis).toString()
  
  private fun getFormatter(format: String): TimeFormatter {
    return StringBuilderTimeFormatter(analyze(updateFormatIfNeeded(format)))
  }
  
  private fun formatTime(format: String, millis: Long): String {
    return getFormatter(format).format(millis).toString()
  }
  
  private fun time(
    hours: Long = 0,
    minutes: Long = 0,
    seconds: Long = 0,
    millis: Long = 0
  ): Long {
    var result: Long = 0
    result += TimeUnit.HOURS.toMillis(hours)
    result += TimeUnit.MINUTES.toMillis(minutes)
    result += TimeUnit.SECONDS.toMillis(seconds)
    result += millis
    return result
  }
}
