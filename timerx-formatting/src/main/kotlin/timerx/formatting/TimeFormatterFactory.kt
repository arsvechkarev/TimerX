package timerx.formatting

/**
 * Factory for creating time formatters
 */
object TimeFormatterFactory {
  
  /**
   * Creates an instance of [TimeFormatter] for a particular [format]
   */
  fun create(format: String): TimeFormatter {
    return StringBuilderTimeFormatter(AnalyzerImpl.analyze(format))
  }
}
