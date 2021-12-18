package timerx.formatting

object Constants {
  
  const val NONE: Int = -1
  
  object Symbols {
    
    const val SYMBOL_HOURS = 'H'
    const val SYMBOL_MINUTES = 'M'
    const val SYMBOL_SECONDS = 'S'
    const val SYMBOL_REM_MILLIS = 'L'
    const val SYMBOL_ESCAPE = '#'
    
    fun Char.isOneOfSpecialSymbols(): Boolean {
      return this == SYMBOL_HOURS || this == SYMBOL_MINUTES || this == SYMBOL_SECONDS || this == SYMBOL_REM_MILLIS
    }
  }
  
  object TimeValues {
    
    const val SECONDS_IN_MINUTE: Long = 60
    const val MINUTES_IN_HOUR: Long = 60
    const val MILLIS_IN_SECOND: Long = 1000
    const val NONE: Long = -1
  }
}
