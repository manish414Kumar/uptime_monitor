package com.manishKumar.uptimemonitor.uptime_monitor.Advices;

import com.manishKumar.uptimemonitor.uptime_monitor.Exceptions.DuplicateMonitorException;
import com.manishKumar.uptimemonitor.uptime_monitor.Exceptions.EmailAlreadyExistsException;
import com.manishKumar.uptimemonitor.uptime_monitor.Exceptions.MonitorNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MonitorNotFound.class)
    public ResponseEntity<ApiResponse<?>> handleMonitorNotFound(MonitorNotFound e) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .build();
        return buildErrorResponse(apiError);
    }
    private ResponseEntity<ApiResponse<?>> buildErrorResponse(ApiError apiError) {
        return new ResponseEntity<>(new ApiResponse<>(apiError), apiError.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneric(Exception e) {
        log.error("Unhandled exception", e);
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Something went wrong")
                .build();
        return buildErrorResponse(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.FORBIDDEN)
                .message(ex.getMessage())
                .build();
        return buildErrorResponse(apiError);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleEmailAlreadyExists(EmailAlreadyExistsException e) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .message(e.getMessage())
                .build();
        return buildErrorResponse(apiError);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleBadCredentials(BadCredentialsException e) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message("Invalid email or password")
                .build();
        return buildErrorResponse(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationErrors(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(java.util.stream.Collectors.joining(", "));

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(message)
                .build();
        return buildErrorResponse(apiError);
    }

    @ExceptionHandler(DuplicateMonitorException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicateMonitor(DuplicateMonitorException e) {

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .message(e.getMessage())
                .build();

        return buildErrorResponse(apiError);
    }
}
