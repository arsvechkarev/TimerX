package timerx.formatting

import timerx.formatting.Constants.Symbols
import java.util.concurrent.TimeUnit

/**
 * Helper class for tests
 */
object TestHelper {
  
  /**
   * Replaces symbols (H, M, S, L, #) with actual symbols in case if they were changed.
   * This allows to avoid rewriting tests every time one of the special symbols is
   * changed
   */
  fun updateFormatIfNeeded(inputFormat: String): String = inputFormat
      .replace('H', Symbols.SYMBOL_HOURS)
      .replace('M', Symbols.SYMBOL_MINUTES)
      .replace('S', Symbols.SYMBOL_SECONDS)
      .replace('L', Symbols.SYMBOL_REM_MILLIS)
      .replace('#', Symbols.SYMBOL_ESCAPE)
}

val Int.millis get() = TimeUnit.MILLISECONDS.toMillis(this.toLong())

val Int.seconds get() = TimeUnit.SECONDS.toMillis(this.toLong())

val Int.minutes get() = TimeUnit.MINUTES.toMillis(this.toLong())

val Int.hours get() = TimeUnit.HOURS.toMillis(this.toLong())
