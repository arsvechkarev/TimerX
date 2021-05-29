package timerx

import timerx.Constants.Symbols

/**
 * Helper class for tests
 */
object TestHelper {
  /**
   * Replaces symbols (H, M, S, L, #) with actual symbols in case if they were changed.
   * This allows to avoid rewriting tests every time one of the special symbols is
   * changed
   */
  @JvmStatic
  fun updateFormatIfNeeded(inputFormat: String): String {
    return inputFormat
        .replace('H', Symbols.SYMBOL_HOURS)
        .replace('M', Symbols.SYMBOL_MINUTES)
        .replace('S', Symbols.SYMBOL_SECONDS)
        .replace('L', Symbols.SYMBOL_REM_MILLIS)
        .replace('#', Symbols.SYMBOL_ESCAPE)
  }
}