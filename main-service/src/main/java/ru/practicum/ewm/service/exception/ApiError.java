package ru.practicum.ewm.service.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    private List<String> errors;
    private String message;
    private String reason;
    private String status;     // ВНИМАНИЕ: используем status.name() → "BAD_REQUEST", "CONFLICT", ...
    private String timestamp;  // "yyyy-MM-dd HH:mm:ss"

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static String now() {
        return LocalDateTime.now().format(F);
    }

    public static ApiError of(HttpStatus status, String reason, String message, List<String> errors) {
        return ApiError.builder()
                .errors(errors == null ? List.of() : errors)
                .message(message)
                .reason(reason)
                .status(status.name())
                .timestamp(now())
                .build();
    }

    public static ApiError of(HttpStatus status, String reason, String message) {
        return of(status, reason, message, List.of());
    }

    public static ApiError badRequest(String message) {
        return of(HttpStatus.BAD_REQUEST, "Incorrectly made request.", message);
    }

    public static ApiError conflict(String message) {
        return of(HttpStatus.CONFLICT, "Integrity constraint has been violated.", message);
    }

    public static ApiError notFound(String message) {
        return of(HttpStatus.NOT_FOUND, "The required object was not found.", message);
    }
}
