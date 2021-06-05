package timerx

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import timerx.Constants.TimeValues
import timerx.TimeCountingState.INACTIVE
import timerx.TimeCountingState.PAUSED
import timerx.TimeCountingState.RESUMED
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
  private var millisInFuture: Long = TimeValues.NONE
  
  // Interval for timer
  private var interval: Long = 0
  
  private var state = INACTIVE
  private var timeFormatter = StringBuilderTimeFormatter(startSemantic)
  private val copyOfSemanticsHolders: SortedSet<SemanticsHolder> = TreeSet(semanticsHolders)
  private val copyOfNextActionsHolders: SortedSet<ActionsHolder> = TreeSet(nextActionsHolders)
  
  override val formattedStartTime: CharSequence
    get() = StringBuilderTimeFormatter(startSemantic).format(startTime)
  
  override val remainingTimeInMillis: Long
    get() = currentTime
  
  override val remainingFormattedTime: CharSequence
    get() = timeFormatter.format(currentTime)
  
  override fun start() {
    if (state == RESUMED) return
    if (millisInFuture == TimeValues.NONE) {
      currentTime = startTime
      applyFormat(startSemantic)
      millisInFuture = SystemClock.elapsedRealtime() + startTime
    } else {
      millisInFuture = SystemClock.elapsedRealtime() + currentTime
    }
    handler!!.sendMessage(handler!!.obtainMessage())
    state = RESUMED
  }
  
  override fun stop() {
    state = PAUSED
    handler!!.removeCallbacksAndMessages(null)
  }
  
  override fun setTimeTo(time: Long, timeUnit: TimeUnit) {
    val millis = timeUnit.toMillis(time)
    millisInFuture = SystemClock.elapsedRealtime() + millis
    currentTime = millis
  }
  
  override fun reset() {
    millisInFuture = TimeValues.NONE
    state = INACTIVE
    copyOfNextActionsHolders.clear()
    copyOfSemanticsHolders.clear()
    copyOfNextActionsHolders.addAll(nextActionsHolders)
    copyOfSemanticsHolders.addAll(semanticsHolders)
    handler!!.removeCallbacksAndMessages(null)
    applyFormat(startSemantic)
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
    interval = timeFormatter.optimalDelay
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
        tickListener?.onTick(timeFormatter.format(currentTime))
        val executionTime = SystemClock.elapsedRealtime() - startExecution
        sendMessageDelayed(obtainMessage(), interval - executionTime)
      }
    }
  }
  
  private fun changeFormatIfNeed() {
    if (copyOfSemanticsHolders.size > 0 && timeFormatter.format != copyOfSemanticsHolders.first().semantic.format
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
    tickListener?.onTick(timeFormatter.format(currentTime))
    finishAction?.run()
    reset()
  }
}