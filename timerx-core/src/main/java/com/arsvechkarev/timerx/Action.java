package com.arsvechkarev.timerx;

/**
 * Interface to execute some action at the certain time
 *
 * @see TimerBuilder
 * @see StopwatchBuilder
 */
public interface Action {

  void execute();
}
