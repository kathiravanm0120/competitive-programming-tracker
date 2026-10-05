package Competitive.Programming.Tracker.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException exception) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Validation failed");
        body.put("fields", fieldErrors);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(
            IllegalArgumentException exception) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", safeMessage(exception.getMessage(), "Invalid request")));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(
            RuntimeException exception) {

        String message = safeMessage(exception.getMessage(), "Request failed");

        HttpStatus status;

        switch (message) {
            case "Username already exists":
            case "Email already exists":
            case "Email is already in use":
                status = HttpStatus.CONFLICT;
                break;

            case "Invalid username or password":
            case "Account is disabled":
                status = HttpStatus.UNAUTHORIZED;
                break;

            case "User not found":
            case "Problem not found":
            case "Goal not found":
            case "Planner task not found":
            case "Notification not found":
                status = HttpStatus.NOT_FOUND;
                break;

            case "You cannot modify this problem":
            case "You cannot delete this problem":
            case "You cannot delete your own admin account":
            case "You cannot disable your own admin account":
            case "You cannot remove your own admin role":
                status = HttpStatus.FORBIDDEN;
                break;

            default:
                status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status)
                .body(Map.of("error", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpectedException(
            Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
    }

    private String safeMessage(String message, String fallback) {
        return message == null || message.isBlank() ? fallback : message;
    }
}
