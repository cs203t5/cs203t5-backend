package com.example.Vox.Viridis.exception;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        List<String> error = new ArrayList<>();
        for (ConstraintViolation<?> violation : violations) {
            error.add(violation.getMessage());
        }
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", error.toString());
        return body;
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handle(BindException ex) {
        List<String> error = new ArrayList<>();
        for (ObjectError objectError : ex.getBindingResult().getAllErrors()){
            error.add(objectError.getDefaultMessage());
        }
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", error.toString());
        return body;
    }

    @ExceptionHandler
    @ResponseBody
    public ResponseEntity<Map<String,Object>> handle(ValidationException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, ex.getHttpStatus());
    }

    /**
     * Handle the case in which arguments for controller's methods did not match the type.
     * E.g., bookId passed in is not a number
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Invalid method argument");
        return body;
    }

}
