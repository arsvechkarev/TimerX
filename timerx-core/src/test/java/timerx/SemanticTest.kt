package timerx

import org.junit.Assert
import org.junit.Test
import timerx.Analyzer.analyze

class SemanticTest {
  
  @Test
  fun basicFormatTest() {
    val format = "MM-SS-LL"
    val semantic = createSemantic(format)
    Assert.assertEquals(semantic.format, format)
    Assert.assertEquals(semantic.format, format)
    Assert.assertEquals(semantic.format, format)
  }
  
  @Test
  fun strippedFormatTest() {
    val format = "#Hello: HH:MM.SS ##LOL"
    val strippedFormat = "Hello: HH:MM.SS #LOL"
    val semantic = createSemantic(format)
    Assert.assertEquals(semantic.format, format)
    Assert.assertEquals(semantic.strippedFormat, strippedFormat)
    Assert.assertEquals(semantic.strippedFormat, strippedFormat)
  }
  
  @Test
  fun positionsTest() {
    val semantic = createSemantic("QHH:SS:MM--")
    Assert.assertTrue(hasRange(semantic.hoursPosition, 1, 2))
    Assert.assertTrue(hasRange(semantic.minutesPosition, 7, 8))
    Assert.assertTrue(hasRange(semantic.secondsPosition, 4, 5))
    Assert.assertTrue(semantic.rMillisPosition.isEmpty)
  }
  
  @Test
  fun hasUnitsTest() {
    val semantic = createSemantic("HH:MM")
    Assert.assertTrue(semantic.has(TimeUnitType.HOURS))
    Assert.assertTrue(semantic.has(TimeUnitType.MINUTES))
    Assert.assertFalse(semantic.has(TimeUnitType.SECONDS))
    Assert.assertFalse(semantic.has(TimeUnitType.R_MILLISECONDS))
  }
  
  @Test
  fun hasNotOnlyRMillisTest() {
    val semantic = createSemantic("MM:SS:LL")
    Assert.assertFalse(semantic.hasOnlyRMillis())
  }
  
  @Test
  fun hasOnlyRMillisTest() {
    val semantic = createSemantic("#Hello: LLLL")
    Assert.assertTrue(semantic.hasOnlyRMillis())
  }
  
  private fun createSemantic(format: String): Semantic {
    return analyze(format)
  }
  
  private fun hasRange(position: Position, start: Int, end: Int): Boolean {
    return position.start == start && position.end == end
  }
}