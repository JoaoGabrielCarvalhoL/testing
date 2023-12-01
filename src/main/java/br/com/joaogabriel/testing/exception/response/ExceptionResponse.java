package br.com.joaogabriel.testing.exception.response;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ExceptionResponse(
    String title, 
    String message, 
    Integer status,
    LocalDateTime occurredAt
) implements Serializable {
    
}
