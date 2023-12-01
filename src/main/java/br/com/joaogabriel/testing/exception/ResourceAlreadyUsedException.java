package br.com.joaogabriel.testing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ResourceAlreadyUsedException extends RuntimeException {
    public ResourceAlreadyUsedException(String message) {
        super(message);
    }
    
}
