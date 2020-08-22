package timerx;

import static timerx.TimeCountingState.INACTIVE;
import static timerx.TimeCountingState.PAUSED;
import static timerx.TimeCountingState.RESUMED;
import static timerx.util.Checker.assertThat;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import timerx.format.Semantic;
import timerx.format.StringBuilderTimeFormatter;

@RestrictTo(Scope.LIBRARY)
class StopwatchImpl implements Stopwatch {

  // Message id for handler
  private static final int MSG = 2;

  // Current time of stopwatch (in millis)
  private long currentTime;

  // Time when stopwatch started (in millis)
  private long baseTime;

  // Delay for handler in millis
  private long delay;

  private TimeCountingState state = INACTIVE;

  private final Semantic startSemantic;
  private TimeTickListener tickListener;
  private StringBuilderTimeFormatter timeFormatter;

  private final SortedSet<NextFormatsHolder> nextFormatsHolders;
  private final SortedSet<ActionsHolder> nextActionsHolders;
  private SortedSet<NextFormatsHolder> copyOfNextFormatsHolder;
  private SortedSet<ActionsHolder> copyOfNextActionsHolders;

  StopwatchImpl(Semantic startSemantic, TimeTickListener tickListener,
      SortedSet<NextFormatsHolder> nextFormatsHolders,
      SortedSet<ActionsHolder> nextActionsHolders) {
    this.startSemantic = startSemantic;
    this.tickListener = tickListener;
    this.nextFormatsHolders = nextFormatsHolders;
    this.nextActionsHolders = nextActionsHolders;
  }

  @Override
  @NonNull
  public CharSequence getFormattedStartTime() {
    return new StringBuilderTimeFormatter(startSemantic).format(0L);
  }

  @Override
  public void start() {
    if (state != RESUMED) {
      if (state == INACTIVE) {
        copyOfNextFormatsHolder = new TreeSet<>(nextFormatsHolders);
        copyOfNextActionsHolders = new TreeSet<>(nextActionsHolders);
        applyFormat(startSemantic);
        baseTime = SystemClock.elapsedRealtime();
      } else {
        assertThat(state == PAUSED);
        baseTime = SystemClock.elapsedRealtime() - currentTime;
      }
      handler.sendEmptyMessage(MSG);
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
    currentTime = 0;
    baseTime = 0;
    state = INACTIVE;
    handler.removeMessages(MSG);
  }

  @Override
  public long getTimeIn(@NonNull TimeUnit timeUnit) {
    return timeUnit.convert(currentTime, TimeUnit.MILLISECONDS);
  }

  @Override
  public void release() {
    nextFormatsHolders.clear();
    copyOfNextFormatsHolder.clear();
    nextActionsHolders.clear();
    copyOfNextActionsHolders.clear();
    tickListener = null;
    handler.removeCallbacksAndMessages(null);
    handler = null;
  }

  public void applyFormat(Semantic semantic) {
    timeFormatter = new StringBuilderTimeFormatter(semantic);
    delay = timeFormatter.getOptimalDelay();
  }

  @SuppressLint("HandlerLeak")
  private Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      synchronized (StopwatchImpl.this) {
        long executionStartedTime = SystemClock.elapsedRealtime();
        currentTime = SystemClock.elapsedRealtime() - baseTime;
        changeFormatIfNeeded();
        notifyActionIfNeeded();
        if (tickListener != null) {
          CharSequence format = timeFormatter.format(currentTime);
          tickListener.onTick(format);
        }
        long executionDelay = SystemClock.elapsedRealtime() - executionStartedTime;
        sendEmptyMessageDelayed(MSG, delay - executionDelay);
      }
    }
  };

  private void notifyActionIfNeeded() {
    if (copyOfNextActionsHolders.size() > 0
        && currentTime >= copyOfNextActionsHolders.first().getMillis()) {
      copyOfNextActionsHolders.first().getAction().run();
      copyOfNextActionsHolders.remove(copyOfNextActionsHolders.first());
    }
  }

  private void changeFormatIfNeeded() {
    if (copyOfNextFormatsHolder.size() > 0
        && !timeFormatter.getFormat()
        .equals(copyOfNextFormatsHolder.first().getSemantic().getFormat())
        && currentTime >= copyOfNextFormatsHolder.first().getMillis()) {
      applyFormat(copyOfNextFormatsHolder.first().getSemantic());
      copyOfNextFormatsHolder.remove(copyOfNextFormatsHolder.first());
    }
  }
}
