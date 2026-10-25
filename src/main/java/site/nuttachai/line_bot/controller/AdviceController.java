package site.nuttachai.line_bot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import site.nuttachai.line_bot.exception.InvalidLineKeyException;
import site.nuttachai.line_bot.exception.LineJwtGenerationException;
import site.nuttachai.line_bot.exception.LineKeyLoadingException;

@RestControllerAdvice
public class AdviceController extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(AdviceController.class);

    @ExceptionHandler({LineKeyLoadingException.class, InvalidLineKeyException.class})
    public ProblemDetail handleLineKeyException(Exception exception) {
        log.error("LINE signing key failure", exception);
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "The LINE signing key is unavailable."
        );
    }

    @ExceptionHandler(LineJwtGenerationException.class)
    public ProblemDetail handleLineJwtGenerationException(LineJwtGenerationException exception) {
        log.error("LINE JWT generation failure", exception);
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unable to generate the LINE JWT."
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedException(Exception exception) {
        log.error("Unexpected request failure", exception);
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred."
        );
    }
}
