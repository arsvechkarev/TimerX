package timerx.formatting

/**
 * Factory for creating time formatters
 */
object TimeFormatterFactory {
  
  /**
   * Creates an instance of [TimeFormatter] from a particular [format]
   */
  fun create(format: String): TimeFormatter {
    return StringBuilderTimeFormatter(Analyzer.get().analyze(format))
  }
  
  /**
   * Creates an instance of [TimeFormatter] from a particular [semantic]
   */
  fun create(semantic: Semantic): TimeFormatter {
    return StringBuilderTimeFormatter(semantic)
  }
}
