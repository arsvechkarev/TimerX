package timerx

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import timerx.TimeCountingState.INACTIVE
import timerx.TimeCountingState.PAUSED
import timerx.TimeCountingState.RESUMED
import timerx.formatting.Constants.TimeValues
import timerx.formatting.Semantic
import timerx.formatting.TimeFormatterFactory
import java.util.concurrent.TimeUnit

internal class StopwatchImpl(
  private val useExactDelay: Boolean,
  private var tickListener: TimeTickListener?,
  private val semanticHolders: MutableList<SemanticHolder>,
  private val actionsHolders: MutableList<ActionsHolder>
) : Stopwatch {
  
  // Current time of stopwatch (in millis)
  private var currentTime: Long = 0
  
  // Time when stopwatch started (in millis)
  private var initialTime: Long = TimeValues.NONE
  
  // Delay (in millis) for stopwatch
  private var delay: Long = 0
  
  // Handler for scheduling actions and formats change
  private val workerHandler = Handler()
  
  // State of the stopwatch
  private var state = INACTIVE
  
  // Current time formatter
  private var timeFormatter = TimeFormatterFactory.create(semanticHolders.first().semantic)
  
  override val formattedStartTime: CharSequence
    get() {
      val holder = semanticHolders.first()
      return TimeFormatterFactory.create(holder.semantic).format(holder.millis)
    }
  
  override val currentTimeInMillis: Long
    get() = currentTime
  
  override val currentFormattedTime: CharSequence
    get() = timeFormatter.format(currentTime)
  
  override fun start() {
    if (state == RESUMED) return
    if (initialTime == TimeValues.NONE) {
      val startTime = semanticHolders.first().millis
      currentTime = startTime
      applyFormat(semanticHolders.first().semantic)
      initialTime = SystemClock.elapsedRealtime() - startTime
    } else {
      initialTime = SystemClock.elapsedRealtime() - currentTime
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
    initialTime = SystemClock.elapsedRealtime() - newTime
    currentTime = newTime
    cancelFormatsAndActionsChanges()
    applyAppropriateFormat()
    scheduleFormatsAndActionsChange()
  }
  
  override fun reset() {
    state = INACTIVE
    initialTime = TimeValues.NONE
    currentTime = semanticHolders.first().millis
    handler!!.removeCallbacksAndMessages(null)
    cancelFormatsAndActionsChanges()
    applyFormat(semanticHolders.first().semantic)
  }
  
  override fun release() {
    semanticHolders.clear()
    actionsHolders.clear()
    tickListener = null
    cancelFormatsAndActionsChanges()
    handler!!.removeCallbacksAndMessages(null)
    handler = null
  }
  
  private fun applyFormat(semantic: Semantic) {
    synchronized(this) {
      timeFormatter = TimeFormatterFactory.create(semantic)
      delay = timeFormatter.getWaitingDelay(useExactDelay)
    }
  }
  
  @SuppressLint("HandlerLeak")
  private var handler: Handler? = object : Handler() {
    
    override fun handleMessage(msg: Message) {
      synchronized(this@StopwatchImpl) {
        val executionStartedTime = SystemClock.elapsedRealtime()
        currentTime = SystemClock.elapsedRealtime() - initialTime
        tickListener?.onTick(currentTime, timeFormatter.format(currentTime))
        val executionDelay = SystemClock.elapsedRealtime() - executionStartedTime
        sendMessageDelayed(obtainMessage(), delay - executionDelay)
      }
    }
  }
  
  private fun applyAppropriateFormat() {
    if (currentTime < semanticHolders.first().millis) {
      applyFormat(semanticHolders.first().semantic)
      return
    }
    for (i in semanticHolders.indices.reversed()) {
      if (semanticHolders[i].millis < currentTime) {
        applyFormat(semanticHolders[i].semantic)
        break
      }
    }
  }
  
  private fun scheduleFormatsAndActionsChange() {
    semanticHolders.forEach { holder ->
      if (holder.millis > currentTime) {
        workerHandler.postDelayed({ applyFormat(holder.semantic) },
          holder.millis - currentTime)
      }
    }
    actionsHolders.forEach { holder ->
      if (holder.millis > currentTime) {
        workerHandler.postDelayed({ holder.action.run() },
          holder.millis - currentTime)
      }
    }
  }
  
  private fun cancelFormatsAndActionsChanges() {
    workerHandler.removeCallbacksAndMessages(null)
  }
}
