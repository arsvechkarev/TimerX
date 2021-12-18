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
import timerx.formatting.TimeUnitType
import java.util.concurrent.TimeUnit
import kotlin.math.abs

internal class TimerImpl(
  private val useExactDelay: Boolean,
  private var tickListener: TimeTickListener?,
  private val finishAction: Runnable?,
  private val semantics: MutableList<SemanticHolder>,
  private val actions: MutableList<ActionsHolder>
) : Timer {
  
  // Remaining (current time) in millis
  private var remainingTime: Long = semantics.first().millis
  
  // Time in future when timer should stop
  private var millisInFuture: Long = TimeValues.NONE
  
  // Delay (in millis) for delaying timer
  private var currentDelay: Long = 0
  
  // Delay (in millis) for correcting timer ticking
  private var currentExactDelay: Long = 0
  
  // Handler for scheduling actions and formats change
  private val workerHandler = Handler()
  
  // State of the timer
  private var state = INACTIVE
  
  // Current time formatter
  private var timeFormatter = TimeFormatterFactory.create(semantics.first().semantic)
  
  // Mode when all formats don't have milliseconds
  private val isInSimpleSecondsMode = semantics.all {
    !it.semantic.has(TimeUnitType.R_MILLISECONDS)
  }
  
  override val formattedStartTime: CharSequence
    get() {
      val holder = semantics.first()
      return TimeFormatterFactory.create(holder.semantic).format(holder.millis)
    }
  
  override val remainingTimeInMillis: Long
    get() = remainingTime
  
  override val remainingFormattedTime: CharSequence
    get() = timeFormatter.format(remainingTime)
  
  override fun start() {
    if (state == RESUMED) return
    if (millisInFuture == TimeValues.NONE) {
      val startTime = semantics.first().millis
      remainingTime = startTime
      applyFormat(semantics.first().semantic)
      val timeToSkip = if (useExactDelay) currentExactDelay else 0
      millisInFuture = SystemClock.elapsedRealtime() + startTime - timeToSkip
    } else {
      val timeToSkip = if (useExactDelay) currentExactDelay else 0
      millisInFuture = SystemClock.elapsedRealtime() + remainingTime - timeToSkip
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
    remainingTime = semantics.first().millis
    handler!!.removeCallbacksAndMessages(null)
    cancelFormatsAndActionsChanges()
    applyFormat(semantics.first().semantic)
  }
  
  override fun release() {
    semantics.clear()
    actions.clear()
    tickListener = null
    cancelFormatsAndActionsChanges()
    handler?.removeCallbacksAndMessages(null)
    handler = null
  }
  
  private fun applyFormat(semantic: Semantic) {
    synchronized(this) {
      timeFormatter = TimeFormatterFactory.create(semantic)
      currentDelay = timeFormatter.getWaitingDelay(useExactDelay)
      currentExactDelay = timeFormatter.getWaitingDelay(useExactDelay = true)
    }
  }
  
  @SuppressLint("HandlerLeak")
  private var handler: Handler? = object : Handler() {
    
    override fun handleMessage(msg: Message) {
      synchronized(this@TimerImpl) {
        val startExecution = SystemClock.elapsedRealtime()
        if (isInSimpleSecondsMode && useExactDelay) {
          remainingTime = remainingTime.coerceAtLeast(0)
          val oldRemainingTime = remainingTime
          tickListener?.onTick(remainingTime, timeFormatter.format(remainingTime))
          remainingTime = millisInFuture - SystemClock.elapsedRealtime()
          val expectedTime = oldRemainingTime - currentExactDelay
          val diff = abs(remainingTime - expectedTime)
          remainingTime += diff
          if (remainingTime <= 0) {
            postDelayed(::finishTimer, currentExactDelay)
            return
          }
        } else {
          remainingTime = millisInFuture - SystemClock.elapsedRealtime()
          remainingTime = remainingTime.coerceAtLeast(0)
          tickListener?.onTick(remainingTime, timeFormatter.format(remainingTime))
          if (remainingTime <= 0) {
            finishAction?.run()
            reset()
            return
          }
        }
        val executionTime = SystemClock.elapsedRealtime() - startExecution
        sendMessageDelayed(obtainMessage(), currentDelay - executionTime)
      }
    }
  }
  
  private fun applyAppropriateFormat() {
    if (remainingTime > semantics.first().millis) {
      applyFormat(semantics.first().semantic)
      return
    }
    for (i in semantics.indices.reversed()) {
      if (semantics[i].millis > remainingTime) {
        applyFormat(semantics[i].semantic)
        break
      }
    }
  }
  
  private fun scheduleFormatsAndActionsChange() {
    semantics.forEach { holder ->
      if (holder.millis < remainingTime) {
        workerHandler.postDelayed({ applyFormat(holder.semantic) },
          remainingTime - holder.millis)
      }
    }
    actions.forEach { holder ->
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
    tickListener?.onTick(remainingTime, timeFormatter.format(remainingTime))
    finishAction?.run()
    reset()
  }
}
