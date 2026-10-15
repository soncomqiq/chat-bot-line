package site.nuttachai.line_bot.exception;

public class LineJwtGenerationException extends RuntimeException {

    public LineJwtGenerationException(String message) {
        super(message);
    }

    public LineJwtGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
