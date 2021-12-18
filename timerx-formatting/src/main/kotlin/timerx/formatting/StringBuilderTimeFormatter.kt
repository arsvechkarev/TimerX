package timerx.formatting

import timerx.formatting.Constants.TimeValues
import java.util.concurrent.TimeUnit
import kotlin.math.pow

/**
 * Optimized time formatter based on string builder
 */
internal class StringBuilderTimeFormatter(private val semantic: Semantic) : TimeFormatter {
  
  private val timeContainer = TimeContainer()
  
  private var formatString = getStrippedFormatString()
  
  private var currentLargestAvailableUnitLength = semantic.largestAvailableUnitLength
  private var currentMillis = 0L
  
  override fun getWaitingDelay(useExactDelay: Boolean) = if (useExactDelay) {
    calculateExactDelay()
  } else {
    calculateOptimalDelay()
  }
  
  override val format: String
    get() = semantic.format
  
  override fun format(millis: Long): CharSequence {
    if (millis < currentMillis) {
      formatString = getStrippedFormatString()
    }
    currentMillis = millis
    val units = timeUnitsOf(millis)
    var millisToShow = TimeValues.NONE
    var secondsToShow = TimeValues.NONE
    var minutesToShow = TimeValues.NONE
    var hoursToShow = TimeValues.NONE
    if (semantic.has(TimeUnitType.R_MILLISECONDS)) {
      millisToShow = if (semantic.has(TimeUnitType.SECONDS)) units.remMillis else units.millis
    }
    if (semantic.has(TimeUnitType.SECONDS)) {
      secondsToShow = if (semantic.has(TimeUnitType.MINUTES)) units.remSeconds else units.seconds
    }
    if (semantic.has(TimeUnitType.MINUTES)) {
      minutesToShow = if (semantic.has(TimeUnitType.HOURS)) units.remMinutes else units.minutes
    }
    if (semantic.has(TimeUnitType.HOURS)) {
      hoursToShow = units.hours
    }
    applyFormat(millisToShow, secondsToShow, minutesToShow, hoursToShow)
    return formatString
  }
  
  private fun calculateOptimalDelay(): Long {
    var delay: Long = 100
    if (semantic.has(TimeUnitType.R_MILLISECONDS)) {
      if (semantic.rMillisPosition.length == 2) {
        delay = 10
      } else if (semantic.rMillisPosition.length > 2) {
        delay = 1
      }
    }
    return delay
  }
  
  private fun calculateExactDelay(): Long {
    var delay: Long = TimeUnit.HOURS.toMinutes(1)
    if (semantic.has(TimeUnitType.MINUTES)) {
      delay = TimeUnit.MINUTES.toMillis(1)
    }
    if (semantic.has(TimeUnitType.SECONDS)) {
      delay = TimeUnit.SECONDS.toMillis(1)
    }
    if (semantic.has(TimeUnitType.R_MILLISECONDS)) {
      delay = when (semantic.rMillisPosition.length) {
        1 -> 100
        2 -> 10
        else -> 1
      }
    }
    return delay
  }
  
  private fun timeUnitsOf(millis: Long): TimeContainer {
    val seconds = millis / TimeValues.MILLIS_IN_SECOND
    val minutes = seconds / TimeValues.SECONDS_IN_MINUTE
    val hours = minutes / TimeValues.MINUTES_IN_HOUR
    val remMillis = millis % TimeValues.MILLIS_IN_SECOND
    val remSeconds = seconds - minutes * TimeValues.SECONDS_IN_MINUTE
    val remMinutes = minutes - hours * TimeValues.MINUTES_IN_HOUR
    return timeContainer.apply {
      this.millis = millis
      this.seconds = seconds
      this.minutes = minutes
      this.hours = hours
      this.remMillis = remMillis
      this.remSeconds = remSeconds
      this.remMinutes = remMinutes
    }
  }
  
  private fun applyFormat(millisToShow: Long, secondsToShow: Long,
                          minutesToShow: Long, hoursToShow: Long) {
    if (millisToShow != TimeValues.NONE) {
      updateString(millisToShow, TimeUnitType.R_MILLISECONDS)
    }
    if (secondsToShow != TimeValues.NONE) {
      updateString(secondsToShow, TimeUnitType.SECONDS)
    }
    if (minutesToShow != TimeValues.NONE) {
      updateString(minutesToShow, TimeUnitType.MINUTES)
    }
    if (hoursToShow != TimeValues.NONE) {
      updateString(hoursToShow, TimeUnitType.HOURS)
    }
  }
  
  private fun updateString(time: Long, timeUnitType: TimeUnitType) {
    var timeVar = time
    val timeLength = lengthOf(timeVar)
    val position = if (semantic.largestAvailableUnit == timeUnitType) {
      val position = semantic.getPositionOf(timeUnitType)
      val howMuchZerosToAdd = (timeLength - currentLargestAvailableUnitLength).coerceAtLeast(0)
      if (currentLargestAvailableUnitLength < timeLength) {
        currentLargestAvailableUnitLength = timeLength
        formatString = StringBuilder(formatString).apply {
          repeat(howMuchZerosToAdd) { insert(0, ' ') }
        }
      }
      val length = semantic.getPositionOf(semantic.largestAvailableUnit).length
      val diff = (timeLength - length).coerceAtLeast(0)
      position.copy(end = position.end + diff)
    } else {
      semantic.getPositionOf(timeUnitType)
          .offsetBy(formatString.length - semantic.strippedFormat.length)
    }
    if (!semantic.hasOnlyRMillis() && timeUnitType === TimeUnitType.R_MILLISECONDS) {
      if (semantic.rMillisPosition.length < 3 && timeLength < 3) {
        val difference = 3 - semantic.rMillisPosition.length
        timeVar /= 10.0.pow(difference.toDouble()).toLong()
      }
      if (semantic.rMillisPosition.length < timeLength) {
        val difference = timeLength - semantic.rMillisPosition.length
        timeVar /= 10.0.pow(difference.toDouble()).toLong()
      }
    }
    val updatedTimeLength = lengthOf(timeVar)
    val range = (position.end - position.start).coerceAtLeast(updatedTimeLength - 1)
    for (i in position.end downTo position.end - range) {
      val ch = ('0'.code.toLong() + timeVar % 10).toInt().toChar()
      require(i >= position.start)
      formatString.setCharAt(i, ch)
      timeVar /= 10
    }
  }
  
  private fun lengthOf(number: Long): Int {
    var length = 0
    var temp: Long = 1
    while (temp <= number) {
      length++
      temp *= 10
    }
    return length
  }
  
  private fun getStrippedFormatString() = StringBuilder(semantic.strippedFormat.length).apply {
    append(semantic.strippedFormat)
  }
}
