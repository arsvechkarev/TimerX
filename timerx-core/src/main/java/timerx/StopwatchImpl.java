package timerx;

import static timerx.Checker.assertThat;
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

class StopwatchImpl implements Stopwatch {

    // Message id for handler
    private static final int MSG = 2;

    private final long startTime;

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
    private final SortedSet<NextFormatsHolder> copyOfNextFormatsHolder;
    private final SortedSet<ActionsHolder> copyOfNextActionsHolders;

    StopwatchImpl(Semantic startSemantic,
                  TimeTickListener tickListener,
                  SortedSet<NextFormatsHolder> nextFormatsHolders,
                  SortedSet<ActionsHolder> nextActionsHolders,
                  long startTime
    ) {
        this.startSemantic = startSemantic;
        this.tickListener = tickListener;
        this.nextFormatsHolders = nextFormatsHolders;
        this.nextActionsHolders = nextActionsHolders;
        this.startTime = startTime;
        copyOfNextFormatsHolder = new TreeSet<>(nextFormatsHolders);
        copyOfNextActionsHolders = new TreeSet<>(nextActionsHolders);

        currentTime = startTime;
    }

    @Override
    @NonNull
    public CharSequence getFormattedStartTime() {
        return new StringBuilderTimeFormatter(startSemantic).format(startTime);
    }

    @Override
    public void start() {
        if (state != RESUMED) {
            if (state == INACTIVE) {
                applyFormat(startSemantic);
            } else {
                assertThat(state == PAUSED);
            }
            baseTime = SystemClock.elapsedRealtime() - currentTime;
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
        currentTime = startTime;
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

    private void applyFormat(Semantic semantic) {
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
