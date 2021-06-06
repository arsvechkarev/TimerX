package timerx

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import timerx.Constants.TimeValues
import timerx.TimeCountingState.INACTIVE
import timerx.TimeCountingState.PAUSED
import timerx.TimeCountingState.RESUMED
import java.util.concurrent.TimeUnit

internal class TimerImpl(
  private var tickListener: TimeTickListener?,
  private val finishAction: Runnable?,
  private val semanticsHolders: MutableList<SemanticsHolder>,
  private val actionsHolders: MutableList<ActionsHolder>
) : Timer {
  
  // Remaining (current time) in millis
  private var remainingTime: Long = semanticsHolders.first().millis
  
  // Time in future when timer should stop
  private var millisInFuture: Long = TimeValues.NONE
  
  // Delay (in millis) for timer
  private var delay: Long = 0
  
  // Handler for scheduling actions and formats change
  private val workerHandler = Handler()
  
  // State of the timer
  private var state = INACTIVE
  
  // Current time formatter
  private var timeFormatter = StringBuilderTimeFormatter(semanticsHolders.first().semantic)
  
  override val formattedStartTime: CharSequence
    get() {
      val holder = semanticsHolders.first()
      return StringBuilderTimeFormatter(holder.semantic).format(holder.millis)
    }
  
  override val remainingTimeInMillis: Long
    get() = remainingTime
  
  override val remainingFormattedTime: CharSequence
    get() = timeFormatter.format(remainingTime)
  
  override fun start() {
    if (state == RESUMED) return
    if (millisInFuture == TimeValues.NONE) {
      val startTime = semanticsHolders.first().millis
      remainingTime = startTime
      applyFormat(semanticsHolders.first().semantic)
      millisInFuture = SystemClock.elapsedRealtime() + startTime
    } else {
      millisInFuture = SystemClock.elapsedRealtime() + remainingTime
    }
    handler!!.sendMessage(handler!!.obtainMessage())
    scheduleFormatsAndActionsChange()
    state = RESUMED
  }
  
  override fun stop() {
    state = PAUSED
    handler!!.removeCallbacksAndMessages(null)
    cancelFormatsAndActionsChanges()
  }
  
  override fun setTime(time: Long, timeUnit: TimeUnit) {
    val newTime = timeUnit.toMillis(time)
    millisInFuture = SystemClock.elapsedRealtime() + newTime
    remainingTime = newTime
    cancelFormatsAndActionsChanges()
    applyAppropriateFormat()
    scheduleFormatsAndActionsChange()
  }
  
  override fun reset() {
    state = INACTIVE
    millisInFuture = TimeValues.NONE
    remainingTime = semanticsHolders.first().millis
    handler!!.removeCallbacksAndMessages(null)
    cancelFormatsAndActionsChanges()
    applyFormat(semanticsHolders.first().semantic)
  }
  
  override fun release() {
    semanticsHolders.clear()
    actionsHolders.clear()
    tickListener = null
    cancelFormatsAndActionsChanges()
    handler?.removeCallbacksAndMessages(null)
    handler = null
  }
  
  private fun applyFormat(semantic: Semantic) {
    synchronized(this) {
      timeFormatter = StringBuilderTimeFormatter(semantic)
      delay = timeFormatter.optimalDelay
    }
  }
  
  @SuppressLint("HandlerLeak")
  private var handler: Handler? = object : Handler() {
    
    override fun handleMessage(msg: Message) {
      synchronized(this@TimerImpl) {
        val startExecution = SystemClock.elapsedRealtime()
        remainingTime = millisInFuture - SystemClock.elapsedRealtime()
        if (remainingTime <= 0) {
          finishTimer()
          return
        }
        tickListener?.onTick(timeFormatter.format(remainingTime))
        val executionTime = SystemClock.elapsedRealtime() - startExecution
        sendMessageDelayed(obtainMessage(), delay - executionTime)
      }
    }
  }
  
  private fun applyAppropriateFormat() {
    if (remainingTime > semanticsHolders.first().millis) {
      applyFormat(semanticsHolders.first().semantic)
      return
    }
    for (i in semanticsHolders.indices.reversed()) {
      if (semanticsHolders[i].millis > remainingTime) {
        applyFormat(semanticsHolders[i].semantic)
        break
      }
    }
  }
  
  private fun scheduleFormatsAndActionsChange() {
    semanticsHolders.forEach { holder ->
      if (holder.millis < remainingTime) {
        workerHandler.postDelayed({ applyFormat(holder.semantic) },
          remainingTime - holder.millis)
      }
    }
    actionsHolders.forEach { holder ->
      if (holder.millis < remainingTime) {
        workerHandler.postDelayed({ holder.action.run() },
          remainingTime - holder.millis)
      }
    }
  }
  
  private fun cancelFormatsAndActionsChanges() {
    workerHandler.removeCallbacksAndMessages(null)
  }
  
  private fun finishTimer() {
    remainingTime = 0
    tickListener?.onTick(timeFormatter.format(remainingTime))
    finishAction?.run()
    reset()
  }
}