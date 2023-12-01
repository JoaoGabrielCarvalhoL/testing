package br.com.joaogabriel.testing.handler;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.joaogabriel.testing.exception.ResourceAlreadyUsedException;
import br.com.joaogabriel.testing.exception.ResourceNotFoundException;
import br.com.joaogabriel.testing.exception.response.ExceptionResponse;

@ControllerAdvice
public class TestingGlobalHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception exception) {
        ExceptionResponse exceptionResponse = new ExceptionResponse("Internal Server Error",
         exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
         return new ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceAlreadyUsedException.class)
    public ResponseEntity<ExceptionResponse> handleResourceAlreadyUsedException(ResourceAlreadyUsedException exception) {
        ExceptionResponse exceptionResponse = new ExceptionResponse("Bad Request",
         exception.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
         return new ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
        ExceptionResponse exceptionResponse = new ExceptionResponse("Bad Request",
         exception.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
         return new ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.BAD_REQUEST);

    }
    
}
