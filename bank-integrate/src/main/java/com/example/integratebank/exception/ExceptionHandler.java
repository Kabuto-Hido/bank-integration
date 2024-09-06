package com.example.integratebank.exception;

import com.example.integratebank.constants.MessageConstants;
import com.example.integratebank.exception.dto.ExceptionResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                     MessageConstants.SYSTEM_ERROR));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(DuplicateException.class)
    public ResponseEntity<String> handleException(DuplicateException e) {
        log.info("", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        List<String> msgErrors = new ArrayList<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            msgErrors.add(fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), msgErrors.get(0)));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintValidationException(
            ConstraintViolationException ex) {
        List<String> msgErrors = new ArrayList<>();
        for (ConstraintViolation violation : ex.getConstraintViolations()) {
            msgErrors.add(violation.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), msgErrors.get(0)));
    }
}
