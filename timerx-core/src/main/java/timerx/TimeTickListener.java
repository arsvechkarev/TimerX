package timerx;

/**
 * Action that will be executed as soon as time in {@link Timer} or {@link Stopwatch}
 * changes
 */
public interface TimeTickListener {

  void onTick(String time);
}
