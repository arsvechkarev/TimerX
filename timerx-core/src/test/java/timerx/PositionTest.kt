package timerx

import org.junit.Assert
import org.junit.Test

class PositionTest {
  
  @Test
  fun emptinessTest() {
    val position = Position(-1, -1)
    Assert.assertTrue(position.isEmpty)
  }
  
  @Test
  fun nonEmptinessTest() {
    val position = Position(2, 6)
    Assert.assertTrue(position.isNotEmpty)
    Assert.assertEquals(2, position.start.toLong())
    Assert.assertEquals(6, position.end.toLong())
  }
  
  @Test
  fun normalLengthTest() {
    val position = Position(2, 6)
    Assert.assertEquals(5, position.length())
  }
  
  @Test
  fun emptyLengthTest() {
    val position = Position(-1, -1)
    Assert.assertEquals(0, position.length())
  }
  
  @Test(expected = IllegalArgumentException::class)
  fun incorrectArgumentsTest() {
    Position(8, 3)
  }
}