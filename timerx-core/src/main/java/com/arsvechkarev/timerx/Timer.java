package com.arsvechkarev.timerx;

import static com.arsvechkarev.timerx.Checker.checkInitialized;
import static com.arsvechkarev.timerx.Checker.checkNotNull;
import static com.arsvechkarev.timerx.TimeCountingStateState.INACTIVE;
import static com.arsvechkarev.timerx.TimeCountingStateState.PAUSED;
import static com.arsvechkarev.timerx.TimeCountingStateState.RESUMED;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import com.arsvechkarev.timerx.format.Semantic;
import com.arsvechkarev.timerx.format.TimeFormatter;
import java.util.SortedSet;
import java.util.TreeSet;

public class Timer {

  private static final String TAG = Timer.class.getSimpleName();

  private final int MSG = 3;

  private long startTime;
  private long millisInFuture;

  private long interval;

  private long currentTime;

  private TimerTickListener tickListener;

  private TimeFormatter timeFormatter;

  private Semantic startSemantic;

  private SortedSet<NextFormatsHolder> formatsHolder;
  private SortedSet<NextFormatsHolder> copyOfFormatsHolder;
  private TimeCountingStateState state = INACTIVE;

  Timer(TimerTickListener tickListener, Semantic semantic,
      SortedSet<NextFormatsHolder> formatsHolder, long startTime) {
    checkNotNull(tickListener, "");
    checkNotNull(semantic, "");
    checkNotNull(formatsHolder, "");
    checkInitialized(startTime, "");
    checkInitialized(interval, "");
    this.tickListener = tickListener;
    this.startSemantic = semantic;
    this.formatsHolder = formatsHolder;
    this.startTime = startTime;
    currentTime = startTime;
    copyOfFormatsHolder = new TreeSet<>(formatsHolder);
    timeFormatter = new TimeFormatter(startSemantic);
    interval = timeFormatter.getOptimizedInterval();
  }

  public String getFormattedStartTime() {
    return timeFormatter.format(startTime);
  }

  public void restartWith(long time, TimeUnits unitType) {
    startTime = Utils.millisOf(time, unitType);
  }

  public void start() {
    if (state != RESUMED) {
      if (state == INACTIVE) {
        copyOfFormatsHolder = new TreeSet<>(formatsHolder);
        applyFormat(startSemantic);
        millisInFuture = SystemClock.elapsedRealtime() + startTime;
      } else {
        millisInFuture = SystemClock.elapsedRealtime() + currentTime;
      }
      handler.sendMessage(handler.obtainMessage(MSG));
      state = RESUMED;
    }
  }

  public void stop() {
    state = PAUSED;
    handler.removeMessages(MSG);
  }

  public void reset() {
    currentTime = startTime;
    state = INACTIVE;
    handler.removeMessages(MSG);
    applyFormat(startSemantic);
  }

  public long getTimeIn(TimeUnits unitType) {
    return Utils.timeIn(currentTime, unitType);
  }

  private void applyFormat(Semantic semantic) {
    timeFormatter = new TimeFormatter(semantic);
    interval = timeFormatter.getOptimizedInterval();
  }

  @SuppressLint("HandlerLeak")
  private final Handler handler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      synchronized (Timer.this) {
        long a = SystemClock.elapsedRealtime();
        changeFormatIfNeed();
        currentTime = millisInFuture - SystemClock.elapsedRealtime();
        Log.d(TAG, "handleMessage: currentTime = " + currentTime);
        if (currentTime <= 0) {
          currentTime = 0;
          String format = timeFormatter.format(currentTime);
          tickListener.onTick(format);
          tickListener.onFinish();
          reset();
          return;
        }
        String format = timeFormatter.format(currentTime);
        Log.d(TAG, "handleMessage: format = " + format);
        Log.d(TAG, "=================");
        tickListener.onTick(format);
        long b = SystemClock.elapsedRealtime() - a;
        sendMessageDelayed(obtainMessage(MSG), interval - b);
      }
    }
  };


  private void changeFormatIfNeed() {
    if (copyOfFormatsHolder.size() > 0
        && !timeFormatter.currentFormat()
        .equals(copyOfFormatsHolder.first().getSemantic().getFormat())
        && currentTime <= copyOfFormatsHolder.first().getMillis()) {
      applyFormat(copyOfFormatsHolder.first().getSemantic());
      copyOfFormatsHolder.remove(copyOfFormatsHolder.first());
    }
  }
}
