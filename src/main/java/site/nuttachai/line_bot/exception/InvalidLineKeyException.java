package site.nuttachai.line_bot.exception;

public class InvalidLineKeyException extends RuntimeException {

    public InvalidLineKeyException(String message) {
        super(message);
    }

    public InvalidLineKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
