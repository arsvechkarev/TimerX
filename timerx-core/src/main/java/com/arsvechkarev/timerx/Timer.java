package com.arsvechkarev.timerx;

import static com.arsvechkarev.timerx.TimeCountingState.INACTIVE;
import static com.arsvechkarev.timerx.TimeCountingState.PAUSED;
import static com.arsvechkarev.timerx.TimeCountingState.RESUMED;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import com.arsvechkarev.timerx.format.Semantic;
import com.arsvechkarev.timerx.format.TimeFormatter;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

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

  private TimeCountingState state = INACTIVE;

  private final SortedSet<NextFormatsHolder> nextFormatsHolders;
  private final SortedSet<ActionsHolder> nextActionsHolders;

  private SortedSet<NextFormatsHolder> copyOfFormatsHolders;
  private SortedSet<ActionsHolder> copyOfActionsHolders;

  @RestrictTo(Scope.LIBRARY)
  Timer(long startTime, Semantic startSemantic, TimerTickListener tickListener,
      SortedSet<NextFormatsHolder> nextFormatsHolders,
      SortedSet<ActionsHolder> nextActionsHolders) {
    this.startTime = startTime;
    this.startSemantic = startSemantic;
    this.tickListener = tickListener;
    this.nextFormatsHolders = nextFormatsHolders;
    this.nextActionsHolders = nextActionsHolders;
    currentTime = startTime;
  }

  public String getFormattedStartTime() {
    return new TimeFormatter(startSemantic).format(startTime);
  }

  public void start() {
    if (state != RESUMED) {
      if (state == INACTIVE) {
        currentTime = startTime;
        copyOfFormatsHolders = new TreeSet<>(nextFormatsHolders);
        copyOfActionsHolders = new TreeSet<>(nextActionsHolders);
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
    state = INACTIVE;
    handler.removeMessages(MSG);
    applyFormat(startSemantic);
  }

  public long getRemainingTimeIn(TimeUnit timeUnit) {
    long timeToFormat = currentTime + timeFormatter.minimumUnitInMillis();
    return (currentTime > 0)
        ? timeUnit.convert(timeToFormat, TimeUnit.MILLISECONDS)
        : 0;
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
        long startExecution = SystemClock.elapsedRealtime();
        currentTime = millisInFuture - SystemClock.elapsedRealtime();
        changeFormatIfNeed();
        makeActionIfNeed();
        Log.d(TAG, "handleMessage: currentTime = " + currentTime);
        if (currentTime <= 0) {
          currentTime = 0;
          tickListener.onTick(timeFormatter.format(currentTime));
          tickListener.onFinish();
          reset();
          return;
        }
        long timeToFormat;
        if (currentTime == startTime) {
          timeToFormat = currentTime;
        } else {
          timeToFormat = currentTime + timeFormatter.minimumUnitInMillis();
        }
        String formattedTime = timeFormatter.format(timeToFormat);
        Log.d(TAG, "handleMessage: formattedTime = " + formattedTime);
        tickListener.onTick(formattedTime);
        long executionTime = SystemClock.elapsedRealtime() - startExecution;
        sendMessageDelayed(obtainMessage(MSG), interval - executionTime);
      }
    }
  };

  private void changeFormatIfNeed() {
    Log.d("wow", "" + copyOfFormatsHolders);
    if (copyOfFormatsHolders.size() > 0
        && !timeFormatter.currentFormat()
        .equals(copyOfFormatsHolders.first().getSemantic().getFormat())
        && currentTime <= copyOfFormatsHolders.first().getMillis()) {
      applyFormat(copyOfFormatsHolders.first().getSemantic());
      copyOfFormatsHolders.remove(copyOfFormatsHolders.first());
    }
  }

  private void makeActionIfNeed() {
    if (copyOfActionsHolders.size() > 0
        && currentTime <= copyOfActionsHolders.first().getMillis()) {
//      Log.d(TAG, "running action with " + copyOfFormatsHolders.first().getMillis());
      copyOfActionsHolders.first().getAction().execute();
      copyOfActionsHolders.remove(copyOfActionsHolders.first());
    }
  }
}




