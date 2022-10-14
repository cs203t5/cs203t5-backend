package com.example.Vox.Viridis.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Component
public class RestExceptionHandler {
    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handle(ConstraintViolationException exception) {

        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        StringBuilder error = new StringBuilder();
        for (ConstraintViolation<?> violation : violations) {
            error.append(violation.getMessage());
            break;
        }
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", error.toString());
        return body;
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handle(BindException ex) {
        StringBuilder error = new StringBuilder();
        for (ObjectError objectError : ex.getBindingResult().getAllErrors()){
            error.append(objectError.getDefaultMessage());
        }
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", error.toString());
        return body;
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handle(ValidationException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", ex.getMessage());
        return body;
    }

    /**
     * Handle the case in which arguments for controller's methods did not match the type.
     * E.g., bookId passed in is not a number
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        
    }
}
