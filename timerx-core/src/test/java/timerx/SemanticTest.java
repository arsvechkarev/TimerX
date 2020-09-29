package timerx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static timerx.TimeUnitType.HOURS;
import static timerx.TimeUnitType.MINUTES;
import static timerx.TimeUnitType.R_MILLISECONDS;
import static timerx.TimeUnitType.SECONDS;

import org.junit.Test;

public class SemanticTest {

  private static Semantic createSemantic(String format) {
    return Analyzer.analyze(format);
  }

  private static boolean hasRange(Position position, int start, int end) {
    return position.start == start && position.end == end;
  }

  @Test
  public void basicFormatTest() {
    String format = "MM-SS-LL";
    Semantic semantic = createSemantic(format);
    assertEquals(semantic.format, format);
    assertEquals(semantic.strippedFormat, format);
    assertEquals(semantic.getFormat(), format);
  }

  @Test
  public void strippedFormatTest() {
    String format = "#Hello: HH:MM.SS ##LOL";
    String strippedFormat = "Hello: HH:MM.SS #LOL";
    Semantic semantic = createSemantic(format);
    assertEquals(semantic.format, format);
    assertEquals(semantic.strippedFormat, strippedFormat);
    assertEquals(semantic.getFormat(), strippedFormat);
  }

  @Test
  public void positionsTest() {
    Semantic semantic = createSemantic("QHH:SS:MM--");
    assertTrue(hasRange(semantic.hoursPosition, 1, 2));
    assertTrue(hasRange(semantic.minutesPosition, 7, 8));
    assertTrue(hasRange(semantic.secondsPosition, 4, 5));
    assertTrue(semantic.rMillisPosition.isEmpty());
  }

  @Test
  public void hasUnitsTest() {
    Semantic semantic = createSemantic("HH:MM");
    assertTrue(semantic.has(HOURS));
    assertTrue(semantic.has(MINUTES));
    assertFalse(semantic.has(SECONDS));
    assertFalse(semantic.has(R_MILLISECONDS));
  }

  @Test
  public void hasNotOnlyRMillisTest() {
    Semantic semantic = createSemantic("MM:SS:LL");
    assertFalse(semantic.hasOnlyRMillis());
  }

  @Test
  public void hasOnlyRMillisTest() {
    Semantic semantic = createSemantic("#Hello: LLLL");
    assertTrue(semantic.hasOnlyRMillis());
  }
}