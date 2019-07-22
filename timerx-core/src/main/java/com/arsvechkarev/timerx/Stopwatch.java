package com.arsvechkarev.timerx;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import com.arsvechkarev.timerx.format.Analyzer;
import com.arsvechkarev.timerx.format.Semantic;
import com.arsvechkarev.timerx.format.TimeFormatter;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Class for counting millis in standard order
 *
 * @author Arseny Svechkarev
 */
public class Stopwatch {

  /**
   * Message id for {@link #handler}
   */
  private static final int MSG = 999;

  /**
   * Current millis check stopwatch in millis
   */
  private long currentTime;

  /**
   * Started millis check stopwatch in millis
   */
  private long baseTime;

  /**
   * Delay millis for handler in millis
   */
  private long delay;

  /**
   * Current state check timer
   */
  private TimerState state = TimerState.INACTIVE;

  private final TimeTickListener tickListener;

  private TimeFormatter formatter;
  private final Semantic startSemantic;

  /**
   * Set of formats to change
   */
  private SortedSet<MillisSemanticHolder> formatsHolder = new TreeSet<>();
  private SortedSet<MillisSemanticHolder> copyOfFormatsHolder;

  public Stopwatch(TimeTickListener tickListener, String format) {
    this.tickListener = tickListener;
    this.startSemantic = Analyzer.checkFormat(format);
    applyFormat(startSemantic);
  }

  public Stopwatch changeFormatWhen(long time, TimeUnits timeUnitType, String newFormat) {
    Semantic semantic = Analyzer.checkFormat(newFormat);
    long millis = Utils.millisOf(time, timeUnitType);
    formatsHolder.add(new MillisSemanticHolder(millis, semantic));
    return this;
  }

  public void start() {
    if (state != TimerState.ACTIVE) {
      if (state == TimerState.INACTIVE) {
        copyOfFormatsHolder = new TreeSet<>(formatsHolder);
        applyFormat(startSemantic);
        baseTime = SystemClock.elapsedRealtime();
      } else {
        baseTime = SystemClock.elapsedRealtime() - currentTime;
      }
      handler.sendMessage(handler.obtainMessage(MSG));
      state = TimerState.ACTIVE;
    }
  }

  public void stop() {
    state = TimerState.PAUSED;
    handler.removeMessages(MSG);
  }

  public void reset() {
    currentTime = 0;
    baseTime = 0;
    state = TimerState.INACTIVE;
    handler.removeMessages(MSG);
  }

  public long getTime() {
    return currentTime;
  }

  private void applyFormat(Semantic semantic) {
    formatter = new TimeFormatter(semantic);
    delay = formatter.getOptimizedDelay();
  }

  @SuppressLint("HandlerLeak")
  private final Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      synchronized (Stopwatch.this) {
        long executionStartedTime = SystemClock.elapsedRealtime();
        currentTime = SystemClock.elapsedRealtime() - baseTime;
        changeFormatIfNeed();
        tickListener.onTimeTick(formatter.format(currentTime));
        long executionDelay = SystemClock.elapsedRealtime() - executionStartedTime;
        sendMessageDelayed(obtainMessage(MSG), delay - executionDelay);
      }
    }
  };

  private void changeFormatIfNeed() {
    if (copyOfFormatsHolder.size() > 0
        && !formatter.currentFormat()
        .equals(copyOfFormatsHolder.first().getSemantic().getFormat())
        && currentTime >= copyOfFormatsHolder.first().getMillis()) {
      applyFormat(copyOfFormatsHolder.first().getSemantic());
      copyOfFormatsHolder.remove(copyOfFormatsHolder.first());
    }
  }
}
