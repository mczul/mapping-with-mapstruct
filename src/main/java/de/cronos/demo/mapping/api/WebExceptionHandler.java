package de.cronos.demo.mapping.api;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class WebExceptionHandler {

    enum ErrorCode {
        UNKNOWN_PROPERTY_REFERENCE,
        CONSTRAINT_VIOLATION,
        UNKNOWN_RUNTIME_EXCEPTION
    }

    @Value
    @Builder
    static class ExceptionReport {

        Instant occured = Instant.now();

        String message;

        ErrorCode errorCode;

        @Singular
        Map<String, String> params;

    }

    @PostConstruct
    protected void init() {
        log.info("Global exception handler initialized");
    }

    @ExceptionHandler
    ResponseEntity<ExceptionReport> handle(PropertyReferenceException ex) {
        final var report = ExceptionReport.builder()
                .errorCode(ErrorCode.UNKNOWN_PROPERTY_REFERENCE)
                .message("Unknown property reference given: \"%s\"".formatted(ex.getPropertyName()))
                .param("base", Optional.ofNullable(ex.getBaseProperty())
                        .map(PropertyPath::toDotPath)
                        .orElse(null))
                .param("property", ex.getPropertyName())
                .build();
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(report);
    }

    @ExceptionHandler
    ResponseEntity<ExceptionReport> handle(MethodArgumentNotValidException ex) {
        final var report = ExceptionReport.builder()
                .errorCode(ErrorCode.CONSTRAINT_VIOLATION)
                .message("Invalid method argument: \"%s\"".formatted(ex.getMessage()))
                .param("objectName", ex.getObjectName())
                .param("nestedPath", ex.getNestedPath())
                .param("errorCodeCSV", ex.getAllErrors().stream()
                        .map(ObjectError::getCode)
                        .collect(Collectors.joining(", "))
                )
                .build();
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(report);
    }

    @ExceptionHandler
    ResponseEntity<ExceptionReport> handle(ConstraintViolationException ex) {
        final var report = ExceptionReport.builder()
                .errorCode(ErrorCode.CONSTRAINT_VIOLATION)
                .message("Constraint violation: \"%s\"".formatted(ex.getMessage()))
                .param("base", ex.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining("; ")))
                .build();
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(report);
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ExceptionReport> handle(RuntimeException ex) {
        final var report = ExceptionReport.builder()
                .errorCode(ErrorCode.UNKNOWN_RUNTIME_EXCEPTION)
                .message(ex.getMessage())
                .param("cause", ex.getCause().getMessage())
                .build();
        return ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(report);
    }

}
