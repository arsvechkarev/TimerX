package com.arsvechkarev.timerx;

import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class TimerTest {

  @Test
  public void test() {
    long time = 10000; // 10s
    TimeUnit timeUnit = TimeUnit.SECONDS;
    System.out.println(timeUnit.convert(time, TimeUnit.MILLISECONDS));
  }

}