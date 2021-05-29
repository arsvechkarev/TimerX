package timerx

internal class SemanticsHolder(val millis: Long, val semantic: Semantic) :
  Comparable<SemanticsHolder> {
  
  override fun compareTo(other: SemanticsHolder) = millis.compareTo(other.millis)
}