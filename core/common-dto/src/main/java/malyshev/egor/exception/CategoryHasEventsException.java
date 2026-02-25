package malyshev.egor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CategoryHasEventsException extends RuntimeException {
    public CategoryHasEventsException(String message) {
        super(message);
    }
}