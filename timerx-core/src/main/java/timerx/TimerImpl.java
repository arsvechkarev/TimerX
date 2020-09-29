package timerx;

import static timerx.TimeCountingState.INACTIVE;
import static timerx.TimeCountingState.PAUSED;
import static timerx.TimeCountingState.RESUMED;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

class TimerImpl implements Timer {

  private final int MSG = 3;

  // Start time in milliseconds
  private final long startTime;

  // Current time (left time) in millis
  private long currentTime;

  // Millis when timer should stop
  private long millisInFuture;

  // Interval for timer
  private long interval;

  private TimeTickListener tickListener;
  private final Action finishAction;
  private final Semantic startSemantic;

  private TimeCountingState state = INACTIVE;
  private StringBuilderTimeFormatter timeFormatter;

  private final SortedSet<NextFormatsHolder> nextFormatsHolders;
  private final SortedSet<ActionsHolder> nextActionsHolders;
  private SortedSet<NextFormatsHolder> copyOfFormatsHolders;
  private SortedSet<ActionsHolder> copyOfNextActionsHolders;

  TimerImpl(long startTime, Semantic startSemantic, TimeTickListener tickListener,
      Action finishAction, SortedSet<NextFormatsHolder> nextFormatsHolders,
      SortedSet<ActionsHolder> nextActionsHolders) {
    this.startTime = startTime;
    this.startSemantic = startSemantic;
    this.tickListener = tickListener;
    this.finishAction = finishAction;
    this.nextFormatsHolders = nextFormatsHolders;
    this.nextActionsHolders = nextActionsHolders;
    currentTime = startTime;
  }

  @NonNull
  @Override
  public CharSequence getFormattedStartTime() {
    return new StringBuilderTimeFormatter(startSemantic).format(startTime);
  }

  @Override
  public void start() {
    if (state != RESUMED) {
      if (state == INACTIVE) {
        currentTime = startTime;
        copyOfFormatsHolders = new TreeSet<>(nextFormatsHolders);
        copyOfNextActionsHolders = new TreeSet<>(nextActionsHolders);
        applyFormat(startSemantic);
        millisInFuture = SystemClock.elapsedRealtime() + startTime;
      } else {
        millisInFuture = SystemClock.elapsedRealtime() + currentTime;
      }
      handler.sendMessage(handler.obtainMessage(MSG));
      state = RESUMED;
    }
  }

  @Override
  public void stop() {
    state = PAUSED;
    handler.removeMessages(MSG);
  }

  @Override
  public void reset() {
    state = INACTIVE;
    handler.removeMessages(MSG);
    applyFormat(startSemantic);
  }

  @Override
  public long getRemainingTimeIn(@NonNull TimeUnit timeUnit) {
    return timeUnit.convert(currentTime, TimeUnit.MILLISECONDS);
  }

  @Override
  public void release() {
    nextFormatsHolders.clear();
    copyOfFormatsHolders.clear();
    nextActionsHolders.clear();
    copyOfNextActionsHolders.clear();
    tickListener = null;
    handler.removeCallbacksAndMessages(null);
    handler = null;
  }

  private void applyFormat(Semantic semantic) {
    timeFormatter = new StringBuilderTimeFormatter(semantic);
    interval = timeFormatter.getOptimalDelay();
  }

  @SuppressLint("HandlerLeak")
  private Handler handler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      synchronized (TimerImpl.this) {
        long startExecution = SystemClock.elapsedRealtime();
        currentTime = millisInFuture - SystemClock.elapsedRealtime();
        changeFormatIfNeed();
        makeActionIfNeed();
        if (currentTime <= 0) {
          finishTimer();
          return;
        }
        if (tickListener != null) {
          CharSequence formattedTime = timeFormatter.format(currentTime);
          tickListener.onTick(formattedTime);
        }
        long executionTime = SystemClock.elapsedRealtime() - startExecution;
        sendMessageDelayed(obtainMessage(MSG), interval - executionTime);
      }
    }
  };

  private void changeFormatIfNeed() {
    if (copyOfFormatsHolders.size() > 0
        && !timeFormatter.getFormat()
        .equals(copyOfFormatsHolders.first().getSemantic().getFormat())
        && currentTime <= copyOfFormatsHolders.first().getMillis()) {
      applyFormat(copyOfFormatsHolders.first().getSemantic());
      copyOfFormatsHolders.remove(copyOfFormatsHolders.first());
    }
  }

  private void makeActionIfNeed() {
    if (copyOfNextActionsHolders.size() > 0
        && currentTime <= copyOfNextActionsHolders.first().getMillis()) {
      copyOfNextActionsHolders.first().getAction().run();
      copyOfNextActionsHolders.remove(copyOfNextActionsHolders.first());
    }
  }

  private void finishTimer() {
    currentTime = 0;
    if (tickListener != null) {
      tickListener.onTick(timeFormatter.format(currentTime));
    }
    if (finishAction != null) {
      finishAction.run();
    }
    reset();
  }
}




