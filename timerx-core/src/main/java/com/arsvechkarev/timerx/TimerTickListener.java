package com.arsvechkarev.timerx;

public interface TimerTickListener {

  void onTick(String time);

  void onFinish();
}
