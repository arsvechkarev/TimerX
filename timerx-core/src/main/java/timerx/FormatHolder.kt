package timerx

internal class FormatHolder(val millis: Long, val format: String) :
  Comparable<FormatHolder> {
  
  override fun compareTo(other: FormatHolder) = millis.compareTo(other.millis)
}
