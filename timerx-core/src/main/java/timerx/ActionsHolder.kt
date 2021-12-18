package timerx

internal class ActionsHolder(
  val millis: Long,
  val action: Runnable
) : Comparable<ActionsHolder> {
  
  override fun compareTo(other: ActionsHolder) = millis.compareTo(other.millis)
}
