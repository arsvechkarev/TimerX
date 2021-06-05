package timerx

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import java.util.SortedSet
import java.util.TreeSet
import java.util.concurrent.TimeUnit

internal class TimerImpl(
  private val startTime: Long,
  private val startSemantic: Semantic,
  private var tickListener: TimeTickListener?,
  private val finishAction: Runnable?,
  private val semanticsHolders: SortedSet<SemanticsHolder>,
  private val nextActionsHolders: SortedSet<ActionsHolder>
) : Timer {
  
  // Current time (left time) in millis
  private var currentTime: Long = startTime
  
  // Millis when timer should stop
  private var millisInFuture: Long = 0
  
  // Interval for timer
  private var interval: Long = 0
  
  private var state = TimeCountingState.INACTIVE
  private var timeFormatter: StringBuilderTimeFormatter? = null
  private val copyOfSemanticsHolders: SortedSet<SemanticsHolder> = TreeSet(semanticsHolders)
  private val copyOfNextActionsHolders: SortedSet<ActionsHolder> = TreeSet(nextActionsHolders)
  
  override val formattedStartTime: CharSequence
    get() = StringBuilderTimeFormatter(startSemantic).format(startTime)
  
  override val currentTimeInMillis: Long get() = currentTime
  
  override fun start() {
    if (state != TimeCountingState.RESUMED) {
      if (state == TimeCountingState.INACTIVE) {
        currentTime = startTime
        applyFormat(startSemantic)
        millisInFuture = SystemClock.elapsedRealtime() + startTime
      } else {
        millisInFuture = SystemClock.elapsedRealtime() + currentTime
      }
      handler!!.sendMessage(handler!!.obtainMessage())
      state = TimeCountingState.RESUMED
    }
  }
  
  override fun stop() {
    state = TimeCountingState.PAUSED
    handler!!.removeCallbacksAndMessages(null)
  }
  
  override fun setTimeTo(time: Long, timeUnit: TimeUnit) {
  }
  
  override fun reset() {
    state = TimeCountingState.INACTIVE
    copyOfNextActionsHolders.clear()
    copyOfSemanticsHolders.clear()
    copyOfNextActionsHolders.addAll(nextActionsHolders)
    copyOfSemanticsHolders.addAll(semanticsHolders)
    handler!!.removeCallbacksAndMessages(null)
    applyFormat(startSemantic)
  }
  
  override fun getRemainingTimeIn(timeUnit: TimeUnit): Long {
    return timeUnit.convert(currentTime, TimeUnit.MILLISECONDS)
  }
  
  override fun release() {
    semanticsHolders.clear()
    copyOfSemanticsHolders.clear()
    nextActionsHolders.clear()
    copyOfNextActionsHolders.clear()
    tickListener = null
    handler?.removeCallbacksAndMessages(null)
    handler = null
  }
  
  private fun applyFormat(semantic: Semantic) {
    timeFormatter = StringBuilderTimeFormatter(semantic)
    interval = timeFormatter!!.optimalDelay
  }
  
  @SuppressLint("HandlerLeak")
  private var handler: Handler? = object : Handler() {
    
    override fun handleMessage(msg: Message) {
      synchronized(this@TimerImpl) {
        val startExecution = SystemClock.elapsedRealtime()
        currentTime = millisInFuture - SystemClock.elapsedRealtime()
        changeFormatIfNeed()
        makeActionIfNeed()
        if (currentTime <= 0) {
          finishTimer()
          return
        }
        tickListener?.onTick(timeFormatter!!.format(currentTime))
        val executionTime = SystemClock.elapsedRealtime() - startExecution
        sendMessageDelayed(obtainMessage(), interval - executionTime)
      }
    }
  }
  
  private fun changeFormatIfNeed() {
    if (copyOfSemanticsHolders.size > 0 && timeFormatter!!.format != copyOfSemanticsHolders.first().semantic.format
        && currentTime <= copyOfSemanticsHolders.first().millis) {
      applyFormat(copyOfSemanticsHolders.first().semantic)
      copyOfSemanticsHolders.remove(copyOfSemanticsHolders.first())
    }
  }
  
  private fun makeActionIfNeed() {
    if (copyOfNextActionsHolders.size > 0
        && currentTime <= copyOfNextActionsHolders.first().millis) {
      copyOfNextActionsHolders.first().action.run()
      copyOfNextActionsHolders.remove(copyOfNextActionsHolders.first())
    }
  }
  
  private fun finishTimer() {
    currentTime = 0
    if (tickListener != null) {
      tickListener!!.onTick(timeFormatter!!.format(currentTime))
    }
    finishAction?.run()
    reset()
  }
}