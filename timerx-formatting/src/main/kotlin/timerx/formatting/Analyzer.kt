package timerx.formatting

/**
 * Analyzes string format and creates [Semantic] (meta information about the format)
 */
interface Analyzer {
  
  /**
   * Validates [format] and creates [Semantic] from it. May throw [IllegalSymbolsCombinationException],
   * [NonContiguousFormatSymbolsException], [NoNecessarySymbolsException] if the format is not
   * valid. See documentation to this exceptions to find out what types of formats are invalid
   */
  fun analyze(format: String): Semantic
  
  companion object {
    
    /** Returns current implementation of the analyzer */
    fun get(): Analyzer = AnalyzerImpl
  }
}
