package com.arsvechkarev.timerx.format;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TimeFormatterTest {

  private TimeFormatter formatterOf(String format) {
    return new TimeFormatter(Analyzer.check(format));
  }

  @Test
  public void optimizedDelayTest1() {
    String format = "MM:SS:LLLLL";
    assertEquals(1, formatterOf(format).getOptimizedInterval());
  }

  @Test
  public void optimizedDelayTest2() {
    String format = "MM:SS:LLL";
    assertEquals(1, formatterOf(format).getOptimizedInterval());
  }

  @Test
  public void optimizedDelayTest3() {
    String format = "MM:SS:LL";
    assertEquals(10, formatterOf(format).getOptimizedInterval());
  }

  @Test
  public void optimizedDelayTest4() {
    String format = "MM:SS:L";
    assertEquals(100, formatterOf(format).getOptimizedInterval());
  }

  @Test
  public void optimizedDelayTest5() {
    String format = "MM:SS::";
    assertEquals(100, formatterOf(format).getOptimizedInterval());
  }

}