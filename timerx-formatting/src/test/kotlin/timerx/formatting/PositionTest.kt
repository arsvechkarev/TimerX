package timerx.formatting

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PositionTest {
  
  @Test
  fun emptinessTest() {
    val position = Position(-1, -1)
    assertTrue(position.isEmpty)
  }
  
  @Test
  fun nonEmptinessTest() {
    val position = Position(2, 6)
    assertTrue(position.isNotEmpty)
    assertEquals(2, position.start.toLong())
    assertEquals(6, position.end.toLong())
  }
  
  @Test
  fun normalLengthTest() {
    val position = Position(2, 6)
    assertEquals(5, position.length)
  }
  
  @Test
  fun emptyLengthTest() {
    val position = Position(-1, -1)
    assertEquals(0, position.length)
  }
  
  @Test(expected = IllegalArgumentException::class)
  fun incorrectArgumentsTest() {
    Position(8, 3)
  }
}
