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

internal class StopwatchImpl(
  private val startSemantic: Semantic,
  private val startTime: Long,
  private var tickListener: TimeTickListener?,
  private val semanticsHolders: SortedSet<SemanticsHolder>,
  private val nextActionsHolders: SortedSet<ActionsHolder>
) : Stopwatch {
  
  // Current time of stopwatch (in millis)
  private var currentTime: Long = 0
  
  // Time when stopwatch started (in millis)
  private var initialTime: Long = TimeValues.NONE
  
  // Delay for handler in millis
  private var delay: Long = 0
  private var state = INACTIVE
  private var timeFormatter = StringBuilderTimeFormatter(startSemantic)
  private val copyOfSemanticsHolders: SortedSet<SemanticsHolder> = TreeSet(semanticsHolders)
  private val copyOfNextActionsHolders: SortedSet<ActionsHolder> = TreeSet(nextActionsHolders)
  
  override val formattedStartTime: CharSequence
    get() = StringBuilderTimeFormatter(startSemantic).format(startTime)
  
  override val currentTimeInMillis: Long
    get() = currentTime
  
  override val currentFormattedTime: CharSequence
    get() = timeFormatter.format(currentTime)
  
  override fun start() {
    if (state == RESUMED) return
    if (initialTime == TimeValues.NONE) {
      applyFormat(startSemantic)
      initialTime = SystemClock.elapsedRealtime() - startTime
    } else {
      initialTime = SystemClock.elapsedRealtime() - currentTime
    }
    handler!!.sendMessage(handler!!.obtainMessage())
    state = RESUMED
  }
  
  override fun stop() {
    state = PAUSED
    handler!!.removeCallbacksAndMessages(null)
  }
  
  override fun setTimeTo(time: Long, timeUnit: TimeUnit) {
    val newTime = timeUnit.toMillis(time)
    initialTime = SystemClock.elapsedRealtime() - newTime
    currentTime = newTime
  }
  
  override fun reset() {
    currentTime = startTime
    initialTime = TimeValues.NONE
    state = INACTIVE
    copyOfNextActionsHolders.clear()
    copyOfSemanticsHolders.clear()
    copyOfNextActionsHolders.addAll(nextActionsHolders)
    copyOfSemanticsHolders.addAll(semanticsHolders)
    handler!!.removeCallbacksAndMessages(null)
  }
  
  override fun release() {
    semanticsHolders.clear()
    copyOfSemanticsHolders.clear()
    nextActionsHolders.clear()
    copyOfNextActionsHolders.clear()
    tickListener = null
    handler!!.removeCallbacksAndMessages(null)
    handler = null
  }
  
  private fun applyFormat(semantic: Semantic) {
    timeFormatter = StringBuilderTimeFormatter(semantic)
    delay = timeFormatter.optimalDelay
  }
  
  @SuppressLint("HandlerLeak")
  private var handler: Handler? = object : Handler() {
    override fun handleMessage(msg: Message) {
      synchronized(this@StopwatchImpl) {
        val executionStartedTime = SystemClock.elapsedRealtime()
        currentTime = SystemClock.elapsedRealtime() - initialTime
        changeFormatIfNeeded()
        notifyActionIfNeeded()
        tickListener?.onTick(timeFormatter.format(currentTime))
        val executionDelay = SystemClock.elapsedRealtime() - executionStartedTime
        sendMessageDelayed(obtainMessage(), delay - executionDelay)
      }
    }
  }
  
  private fun notifyActionIfNeeded() {
    if (copyOfNextActionsHolders.size > 0
        && currentTime >= copyOfNextActionsHolders.first().millis) {
      copyOfNextActionsHolders.first().action.run()
      copyOfNextActionsHolders.remove(copyOfNextActionsHolders.first())
    }
  }
  
  private fun changeFormatIfNeeded() {
    if (copyOfSemanticsHolders.size > 0 && timeFormatter.format != copyOfSemanticsHolders.first().semantic.format
        && currentTime >= copyOfSemanticsHolders.first().millis) {
      applyFormat(copyOfSemanticsHolders.first().semantic)
      copyOfSemanticsHolders.remove(copyOfSemanticsHolders.first())
    }
  }
}