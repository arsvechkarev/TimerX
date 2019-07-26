package com.arsvechkarev.timerx.format;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;

public class TimeFormatterTest {

  @Test
  public void test() {
    List<String> strings = Arrays.asList("one", "two", "la");
    Iterator<String> iterator = strings.iterator();
    strings.add("q");
    while (iterator.hasNext()) {
      System.out.println(iterator.next());
    }
  }

  private TimeFormatter getFormatterOf(String format) {
    return new TimeFormatter(Analyzer.check(format));
  }

  @Test
  public void optimizedDelayTest1() {
    String format = "MM:SS:LLLLL";
    assertEquals(1, getFormatterOf(format).getOptimizedInterval());
  }

  @Test
  public void optimizedDelayTest2() {
    String format = "MM:SS:LLL";
    assertEquals(1, getFormatterOf(format).getOptimizedInterval());
  }

  @Test
  public void optimizedDelayTest3() {
    String format = "MM:SS:LL";
    assertEquals(10, getFormatterOf(format).getOptimizedInterval());
  }

  @Test
  public void optimizedDelayTest4() {
    String format = "MM:SS:L";
    assertEquals(100, getFormatterOf(format).getOptimizedInterval());
  }

  @Test
  public void optimizedDelayTest5() {
    String format = "MM:SS::";
    assertEquals(100, getFormatterOf(format).getOptimizedInterval());
  }

}