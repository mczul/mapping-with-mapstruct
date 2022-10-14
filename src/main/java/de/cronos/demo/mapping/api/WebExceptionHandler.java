package de.cronos.demo.mapping.api;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.*;

@Slf4j
@ControllerAdvice
public class WebExceptionHandler {

    enum ErrorCode {
        UNKNOWN_PROPERTY_REFERENCE,
        CONSTRAINT_VIOLATION,
        MESSAGE_NOT_READABLE,
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

    /**
     * Handles exceptions indicating property access by name. An example: Sort a paged listing by unknown attribute path
     */
    @ExceptionHandler
    ResponseEntity<ExceptionReport> handle(PropertyReferenceException ex) {
        final var report = ExceptionReport.builder()
                .errorCode(ErrorCode.UNKNOWN_PROPERTY_REFERENCE)
                .message("Unknown property reference given: «%s»".formatted(ex.getPropertyName()))
                .param("base", Optional.ofNullable(ex.getBaseProperty())
                        .map(PropertyPath::toDotPath)
                        .orElse(null))
                .param("property", ex.getPropertyName())
                .build();
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(report);
    }

    /**
     * Handles bean validation exceptions that may refer to the invalid payload of a request body
     */
    @ExceptionHandler
    ResponseEntity<ExceptionReport> handle(MethodArgumentNotValidException ex) {
        final var violations = ex.getBindingResult().getFieldErrors().stream()
                .collect(groupingBy(
                        FieldError::getField,
                        mapping(FieldError::getDefaultMessage, joining("; "))
                ));

        final var report = ExceptionReport.builder()
                .errorCode(ErrorCode.CONSTRAINT_VIOLATION)
                .message("Invalid method argument: «%s»".formatted(ex.getMessage()))
                .params(violations)
                .build();

        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(report);
    }

    /**
     * Handles bean validation exceptions that refer to arbitrary constraint violations as e.g. invalid path variables
     */
    @ExceptionHandler
    ResponseEntity<ExceptionReport> handle(ConstraintViolationException ex) {
        final var violations = ex.getConstraintViolations().stream()
                .collect(groupingBy(
                        violation -> violation.getPropertyPath().toString(),
                        mapping(ConstraintViolation::getMessage, joining("; "))
                ));

        final var report = ExceptionReport.builder()
                .errorCode(ErrorCode.CONSTRAINT_VIOLATION)
                .message("Constraint violation(s): «%s»".formatted(ex.getMessage()))
                .params(violations)
                .build();

        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(report);
    }

    /**
     * Handles exceptions that indicate malformed source values like e.g. invalid UUID strings
     */
    @ExceptionHandler
    ResponseEntity<ExceptionReport> handle(HttpMessageNotReadableException ex) {
        final var report = ExceptionReport.builder()
                .errorCode(ErrorCode.MESSAGE_NOT_READABLE)
                .message("Message not readable: «%s»".formatted(ex.getMessage()))
                .build();
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(report);
    }

//    @ExceptionHandler
//    ResponseEntity<ExceptionReport> handle(RuntimeException ex) {
//        var reportBuilder = ExceptionReport.builder()
//                .errorCode(ErrorCode.UNKNOWN_RUNTIME_EXCEPTION)
//                .message(ex.getMessage());
//
//        if (ex.getCause() != null) {
//            reportBuilder = reportBuilder.param("cause", ex.getCause().getMessage());
//        }
//
//        return ResponseEntity.internalServerError()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(reportBuilder.build());
//    }

}
