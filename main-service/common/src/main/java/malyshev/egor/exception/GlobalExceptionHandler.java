package malyshev.egor.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import malyshev.egor.ewm.service.compilation.exception.CompilationNotFoundException;
import malyshev.egor.ewm.service.compilation.exception.TitleAlreadyExistsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 — ошибки валидации тела/параметров
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiError handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> String.format("Field: %s. Error: %s. Value: %s",
                        err.getField(), err.getDefaultMessage(), err.getRejectedValue()))
                .collect(Collectors.joining("; "));
        return ApiError.of(HttpStatus.BAD_REQUEST, "Incorrectly made request.", msg);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class,
    })
    public ApiError handleBadRequest(Exception ex) {
        return ApiError.of(HttpStatus.BAD_REQUEST, "Incorrectly made request.", ex.getMessage());
    }

    // 404 — не найдено
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            NotFoundException.class,
            CompilationNotFoundException.class,
            NoSuchElementException.class
    })
    public ApiError handleNotFound(Exception ex) {
        return ApiError.of(HttpStatus.NOT_FOUND, "The required object was not found.", ex.getMessage());
    }

    // 409 — конфликт: ограничения БД, бизнес-состояние «нельзя»
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({
            DataIntegrityViolationException.class,
            TitleAlreadyExistsException.class
    })
    public ApiError handleConflict(Exception ex) {
        return ApiError.of(HttpStatus.CONFLICT, "Integrity constraint has been violated.", ex.getMessage());
    }

    // 409 — условия операции не соблюдены (ваш бизнес-кейс)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(IllegalStateException.class)
    public ApiError handleIllegalState(IllegalStateException ex) {
        return ApiError.of(HttpStatus.CONFLICT, "For the requested operation the conditions are not met.", ex.getMessage());
    }

    // 400 — ваша кастомная ValidationException
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ApiError handleCustomValidation(ValidationException ex) {
        return ApiError.badRequest(ex.getMessage());
    }

    // 500 — запасной парашют
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiError handleOther(Exception ex) {
        return ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error.", ex.getMessage());
    }
}
