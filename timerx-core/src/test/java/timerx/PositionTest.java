package timerx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PositionTest {

  @Test
  public void emptinessTest() {
    Position position = new Position(-1, -1);
    assertTrue(position.isEmpty());
  }

  @Test
  public void nonEmptinessTest() {
    Position position = new Position(2, 6);
    assertTrue(position.isNotEmpty());
    assertEquals(2, position.start);
    assertEquals(6, position.end);
  }

  @Test
  public void normalLengthTest() {
    Position position = new Position(2, 6);
    assertEquals(5, position.length());
  }

  @Test
  public void emptyLengthTest() {
    Position position = new Position(-1, -1);
    assertEquals(0, position.length());
  }

  @Test(expected = IllegalArgumentException.class)
  public void incorrectArgumentsTest() {
    new Position(8, 3);
  }
}