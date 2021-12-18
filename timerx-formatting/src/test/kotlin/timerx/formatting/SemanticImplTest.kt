package timerx.formatting

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import timerx.formatting.AnalyzerImpl.analyze

class SemanticImplTest {
  
  @Test
  fun basicFormatTest() {
    val format = "MM-SS-LL"
    val semantic = createSemantic(format)
    assertEquals(semantic.format, format)
    assertEquals(semantic.format, format)
    assertEquals(semantic.format, format)
  }
  
  @Test
  fun strippedFormatTest() {
    val format = "#Hello: HH:MM.SS ##LOL"
    val strippedFormat = "Hello: HH:MM.SS #LOL"
    val semantic = createSemantic(format)
    assertEquals(semantic.format, format)
    assertEquals(semantic.strippedFormat, strippedFormat)
    assertEquals(semantic.strippedFormat, strippedFormat)
  }
  
  @Test
  fun positionsTest() {
    val semantic = createSemantic("QHH:SS:MM--")
    assertTrue(hasRange(semantic.hoursPosition, 1, 2))
    assertTrue(hasRange(semantic.minutesPosition, 7, 8))
    assertTrue(hasRange(semantic.secondsPosition, 4, 5))
    assertTrue(semantic.rMillisPosition.isEmpty)
  }
  
  @Test
  fun hasUnitsTest() {
    val semantic = createSemantic("HH:MM")
    assertTrue(semantic.has(TimeUnitType.HOURS))
    assertTrue(semantic.has(TimeUnitType.MINUTES))
    assertFalse(semantic.has(TimeUnitType.SECONDS))
    assertFalse(semantic.has(TimeUnitType.R_MILLISECONDS))
  }
  
  @Test
  fun hasNotOnlyRMillisTest() {
    val semantic = createSemantic("MM:SS:LL")
    assertFalse(semantic.hasOnlyRMillis())
  }
  
  @Test
  fun hasOnlyRMillisTest() {
    val semantic = createSemantic("#Hello: LLLL")
    assertTrue(semantic.hasOnlyRMillis())
  }
  
  private fun createSemantic(format: String): SemanticImpl {
    return analyze(format)
  }
  
  private fun hasRange(position: Position, start: Int, end: Int): Boolean {
    return position.start == start && position.end == end
  }
}
