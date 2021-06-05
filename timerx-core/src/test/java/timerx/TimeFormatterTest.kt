package timerx

import org.junit.Assert.assertEquals
import org.junit.Test
import timerx.Analyzer.analyze
import timerx.Constants.TimeValues.MILLIS_IN_HOUR
import timerx.Constants.TimeValues.MILLIS_IN_MINUTE
import timerx.Constants.TimeValues.MILLIS_IN_SECOND
import timerx.TestHelper.updateFormatIfNeeded

class TimeFormatterTest {
  
  @Test
  fun straightforwardTest() {
    val result: CharSequence = TimeFormatter.format("SS:LL", 12365).toString()
    assertEquals("12:36", result)
  }
  
  @Test
  fun optimizedDelayTest1() {
    val format = "MM:SS:LLLLL"
    assertEquals(1, getFormatter(format).optimalDelay)
  }
  
  @Test
  fun optimizedDelayTest2() {
    val format = "MM:SS:LLL"
    assertEquals(1, getFormatter(format).optimalDelay)
  }
  
  @Test
  fun optimizedDelayTest3() {
    val format = "MM:SS:LL"
    assertEquals(10, getFormatter(format).optimalDelay)
  }
  
  @Test
  fun optimizedDelayTest4() {
    val format = "MM:SS:L"
    assertEquals(100, getFormatter(format).optimalDelay)
  }
  
  @Test
  fun optimizedDelayTest5() {
    val format = "MM:SS::"
    assertEquals(100, getFormatter(format).optimalDelay)
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
    assertEquals("00:00.00", formatTime(format, 0))
  }
  
  @Test
  fun soloMillisTest1() {
    val format = "LLLLL"
    assertEquals("01200", formatTime(format, 1200))
  }
  
  @Test
  fun soloMillisTest2() {
    val format = "LLLL"
    assertEquals("1599", formatTime(format, 1599))
  }
  
  @Test
  fun soloMillisTest3() {
    val format = "LLL"
    assertEquals("159999", formatTime(format, 159999))
  }
  
  @Test
  fun soloSeconds() {
    val format = "SSS"
    val millis = millisOf(0, 1, 3, 789)
    assertEquals("063", formatTime(format, millis))
  }
  
  @Test
  fun soloMinutes() {
    val format = "MM min"
    val millis = millisOf(0, 49, 35, 123)
    assertEquals("49 min", formatTime(format, millis))
  }
  
  @Test
  fun soloHours() {
    val format = "HHH_"
    val millis = millisOf(55, 6, 3, 789)
    assertEquals("055_", formatTime(format, millis))
  }
  
  @Test
  fun formatWithRemMillis1() {
    val format = "HHH_MMM qwerty SS lol LLL"
    val millis = millisOf(36, 6, 3, 19)
    assertEquals("036_006 qwerty 03 lol 019", formatTime(format, millis))
  }
  
  @Test
  fun formatWithRemMillis2() {
    val format = "MM:SS:LLL"
    val millis = millisOf(0, 19, 29, 11)
    assertEquals("19:29:011", formatTime(format, millis))
  }
  
  @Test
  fun formatWithRemMillis3() {
    val format = "SS lol LLL"
    val millis = millisOf(0, 13, 29, 1)
    assertEquals("809 lol 001", formatTime(format, millis))
  }
  
  @Test
  fun formatWithRemMillis4() {
    val format = "SS lol LLLLLL"
    val millis = millisOf(0, 13, 29, 2)
    assertEquals("809 lol 000002", formatTime(format, millis))
  }
  
  @Test
  fun formatWithRemMillis5() {
    val format = "SS lol L"
    val millis = millisOf(0, 0, 0, 17)
    assertEquals("00 lol 0", formatTime(format, millis))
  }
  
  @Test
  fun formatWithRemMillis6() {
    val format = "SS lol LL"
    val millis = millisOf(0, 0, 0, 17)
    assertEquals("00 lol 01", formatTime(format, millis))
  }
  
  @Test
  fun standardFormat2() {
    val format = "HHh MMm SSs"
    val millis = millisOf(2, 55, 59, 0)
    assertEquals("02h 55m 59s", formatTime(format, millis))
  }
  
  @Test
  fun standardFormat3() {
    val format = "HH bang MM"
    val millis = millisOf(1, 12, 36, 2)
    assertEquals("01 bang 12", formatTime(format, millis))
  }
  
  @Test
  fun standardFormat1() {
    val format = "MM blah SS"
    val millis = millisOf(0, 37, 5, 28)
    assertEquals("37 blah 05", formatTime(format, millis))
  }
  
  @Test
  fun formatWithEscaping1() {
    val format = "HH#H MM#M SS#S LL#L#L"
    val millis = millisOf(4, 7, 49, 219)
    assertEquals("04H 07M 49S 21LL", formatTime(format, millis))
  }
  
  @Test
  fun formatWithEscaping2() {
    val format = "MM#M SS#S"
    val millis = millisOf(0, 31, 5, 2)
    assertEquals("31M 05S", formatTime(format, millis))
  }
  
  @Test
  fun formatWithEscaping3() {
    val format = "#Hello +  SS:LL"
    val millis = millisOf(0, 1, 22, 167)
    assertEquals("Hello +  82:16", formatTime(format, millis))
  }
  
  @Test
  fun formatWithEscaping4() {
    val format = "#LA#LA#LA : MM-SS"
    val millis = millisOf(0, 8, 5, 23)
    assertEquals("LALALA : 08-05", formatTime(format, millis))
  }
  
  @Test
  fun formatWithEscaping5() {
    val format = "#Hello ## SS:LL"
    val millis = millisOf(0, 0, 36, 23)
    assertEquals("Hello ## 36:02", formatTime(format, millis))
  }
  
  @Test
  fun multipleFormats1() {
    val format = "SS:LLL"
    val formatter = getFormatter(format)
    
    val millis1 = 99077L
    assertEquals("99:077", formatter.formatToString(millis1))
    
    val millis2 = 101049L
    assertEquals("101:049", formatter.formatToString(millis2))
    
    val millis3 = 105921L
    assertEquals("105:921", formatter.formatToString(millis3))
    
    val millis4 = 883310210L
    assertEquals("883310:210", formatter.formatToString(millis4))
  }
  
  @Test
  fun multipleFormats2() {
    val format = "SS:LLL"
    val formatter = getFormatter(format)
    
    val millis1 = 99077L
    assertEquals("99:077", formatter.formatToString(millis1))
    
    val millis2 = 35L
    assertEquals("00:035", formatter.formatToString(millis2))
    
    val millis3 = 101049L
    assertEquals("101:049", formatter.formatToString(millis3))
  
    val millis4 = 883310210L
    assertEquals("883310:210", formatter.formatToString(millis4))
  }
  
  private fun TimeFormatter.formatToString(millis: Long) = format(millis).toString()
  
  private fun getFormatter(format: String): TimeFormatter {
    return StringBuilderTimeFormatter(analyze(updateFormatIfNeeded(format)))
  }
  
  private fun formatTime(format: String, millis: Long): String {
    return getFormatter(format).format(millis).toString()
  }
  
  private fun millisOf(hours: Long, minutes: Long, seconds: Long, millis: Long): Long {
    var result: Long = 0
    result += hours * MILLIS_IN_HOUR
    result += minutes * MILLIS_IN_MINUTE
    result += seconds * MILLIS_IN_SECOND
    result += millis
    return result
  }
}