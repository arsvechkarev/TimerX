package timerx.formatting

/**
 * Analyzes string format and creates [Semantic] (meta information about the format)
 */
interface Analyzer {
  
  /**
   * Creates [Semantic]
   */
  fun analyze(format: String): Semantic
  
  companion object {
    
    /** Returns current implementation of the analyzer */
    fun get(): Analyzer = AnalyzerImpl
  }
}
