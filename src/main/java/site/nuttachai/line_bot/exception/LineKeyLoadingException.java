package site.nuttachai.line_bot.exception;

public class LineKeyLoadingException extends RuntimeException {

    public LineKeyLoadingException(String message) {
        super(message);
    }

    public LineKeyLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
