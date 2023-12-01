package br.com.joaogabriel.testing.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.joaogabriel.testing.model.Employee;

public interface EmployeeController {
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<Employee> save(@RequestBody Employee employee);

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<List<Employee>> findAll(Pageable pageable);

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<Employee> findById(@PathVariable("id") UUID id);

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<Void> update(@RequestBody Employee employee);

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<Void> delete(@PathVariable("id") UUID id);
}
