package calendar.persistence;

/**
 * Exception thrown when parsing a text line into a domain object fails.
 * This exception indicates that the line format is invalid or contains
 * data that cannot be parsed correctly.
 */
public class ParseException extends Exception {
    
    /**
     * Constructs a new ParseException with the specified detail message.
     * 
     * @param message the detail message explaining why parsing failed
     */
    public ParseException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new ParseException with the specified detail message and cause.
     * 
     * @param message the detail message explaining why parsing failed
     * @param cause the underlying cause of the parsing failure
     */
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
