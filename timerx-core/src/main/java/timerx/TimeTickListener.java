package timerx;

/**
 * Action that will be executed as soon as time in {@link TimerImpl} or {@link
 * StopwatchImpl} changes
 */
public interface TimeTickListener {

  void onTick(CharSequence time);
}
