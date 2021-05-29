package timerx

/**
 * Exception thrown when input format does not contain any special symbols like "H", "M",
 * "S" or "L"
 */
public class NoNecessarySymbolsException(message: String?) : RuntimeException(message)