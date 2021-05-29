package timerx

import org.junit.Assert
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
    Assert.assertEquals("12:36", result)
  }
  
  @Test
  fun optimizedDelayTest1() {
    val format = "MM:SS:LLLLL"
    Assert.assertEquals(1, formatTime(format).optimalDelay)
  }
  
  @Test
  fun optimizedDelayTest2() {
    val format = "MM:SS:LLL"
    Assert.assertEquals(1, formatTime(format).optimalDelay)
  }
  
  @Test
  fun optimizedDelayTest3() {
    val format = "MM:SS:LL"
    Assert.assertEquals(10, formatTime(format).optimalDelay)
  }
  
  @Test
  fun optimizedDelayTest4() {
    val format = "MM:SS:L"
    Assert.assertEquals(100, formatTime(format).optimalDelay)
  }
  
  @Test
  fun optimizedDelayTest5() {
    val format = "MM:SS::"
    Assert.assertEquals(100, formatTime(format).optimalDelay)
  }
  
  @Test
  fun currentFormatTest() {
    val semantic = analyze("MM:SS")
    val timeFormatter = StringBuilderTimeFormatter(semantic)
    Assert.assertEquals("MM:SS", timeFormatter.format)
  }
  
  @Test
  fun zeroTest() {
    val format = "MM:SS.LL"
    Assert.assertEquals("00:00.00", formatTime(format, 0))
  }
  
  @Test
  fun soloMillisTest1() {
    val format = "LLLLL"
    Assert.assertEquals("01200", formatTime(format, 1200))
  }
  
  @Test
  fun soloMillisTest2() {
    val format = "LLLL"
    Assert.assertEquals("1599", formatTime(format, 1599))
  }
  
  @Test
  fun soloMillisTest3() {
    val format = "LLL"
    Assert.assertEquals("159999", formatTime(format, 159999))
  }
  
  @Test
  fun soloSeconds() {
    val format = "SSS"
    val millis = millisOf(0, 1, 3, 789)
    Assert.assertEquals("063", formatTime(format, millis))
  }
  
  @Test
  fun soloMinutes() {
    val format = "MM min"
    val millis = millisOf(0, 49, 35, 123)
    Assert.assertEquals("49 min", formatTime(format, millis))
  }
  
  @Test
  fun soloHours() {
    val format = "HHH_"
    val millis = millisOf(55, 6, 3, 789)
    Assert.assertEquals("055_", formatTime(format, millis))
  }
  
  @Test
  fun formatWithRemMillis1() {
    val format = "HHH_MMM qwerty SS lol LLL"
    val millis = millisOf(36, 6, 3, 19)
    Assert.assertEquals("036_006 qwerty 03 lol 019", formatTime(format, millis))
  }
  
  @Test
  fun formatWithRemMillis2() {
    val format = "MM:SS:LLL"
    val millis = millisOf(0, 19, 29, 11)
    Assert.assertEquals("19:29:011", formatTime(format, millis))
  }
  
  @Test
  fun formatWithRemMillis3() {
    val format = "SS lol LLL"
    val millis = millisOf(0, 13, 29, 1)
    Assert.assertEquals("809 lol 001", formatTime(format, millis))
  }
  
  @Test
  fun formatWithRemMillis4() {
    val format = "SS lol LLLLLL"
    val millis = millisOf(0, 13, 29, 2)
    Assert.assertEquals("809 lol 000002", formatTime(format, millis))
  }
  
  @Test
  fun formatWithRemMillis5() {
    val format = "SS lol L"
    val millis = millisOf(0, 0, 0, 17)
    Assert.assertEquals("00 lol 0", formatTime(format, millis))
  }
  
  @Test
  fun formatWithRemMillis6() {
    val format = "SS lol LL"
    val millis = millisOf(0, 0, 0, 17)
    Assert.assertEquals("00 lol 01", formatTime(format, millis))
  }
  
  @Test
  fun standardFormat2() {
    val format = "HHh MMm SSs"
    val millis = millisOf(2, 55, 59, 0)
    Assert.assertEquals("02h 55m 59s", formatTime(format, millis))
  }
  
  @Test
  fun standardFormat3() {
    val format = "HH bang MM"
    val millis = millisOf(1, 12, 36, 2)
    Assert.assertEquals("01 bang 12", formatTime(format, millis))
  }
  
  @Test
  fun standardFormat1() {
    val format = "MM blah SS"
    val millis = millisOf(0, 37, 5, 28)
    Assert.assertEquals("37 blah 05", formatTime(format, millis))
  }
  
  @Test
  fun formatWithEscaping1() {
    val format = "HH#H MM#M SS#S LL#L#L"
    val millis = millisOf(4, 7, 49, 219)
    Assert.assertEquals("04H 07M 49S 21LL", formatTime(format, millis))
  }
  
  @Test
  fun formatWithEscaping2() {
    val format = "MM#M SS#S"
    val millis = millisOf(0, 31, 5, 2)
    Assert.assertEquals("31M 05S", formatTime(format, millis))
  }
  
  @Test
  fun formatWithEscaping3() {
    val format = "#Hello +  SS:LL"
    val millis = millisOf(0, 1, 22, 167)
    Assert.assertEquals("Hello +  82:16", formatTime(format, millis))
  }
  
  @Test
  fun formatWithEscaping4() {
    val format = "#LA#LA#LA : MM-SS"
    val millis = millisOf(0, 8, 5, 23)
    Assert.assertEquals("LALALA : 08-05", formatTime(format, millis))
  }
  
  @Test
  fun formatWithEscaping5() {
    val format = "#Hello ## SS:LL"
    val millis = millisOf(0, 0, 36, 23)
    Assert.assertEquals("Hello ## 36:02", formatTime(format, millis))
  }
  
  companion object {
    private fun formatTime(format: String): TimeFormatter {
      return StringBuilderTimeFormatter(analyze(updateFormatIfNeeded(format)))
    }
    
    private fun formatTime(format: String, millis: Long): String {
      return StringBuilderTimeFormatter(analyze(updateFormatIfNeeded(format)))
          .format(millis).toString()
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
}