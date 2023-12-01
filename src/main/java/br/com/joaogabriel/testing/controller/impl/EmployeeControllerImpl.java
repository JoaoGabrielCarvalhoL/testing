package br.com.joaogabriel.testing.controller.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.joaogabriel.testing.controller.EmployeeController;
import br.com.joaogabriel.testing.model.Employee;
import br.com.joaogabriel.testing.service.EmployeeService;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeControllerImpl implements EmployeeController{
    private final EmployeeService employeeService;

    public EmployeeControllerImpl(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public ResponseEntity<Employee> save(Employee employee) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(this.employeeService.save(employee));
    }

    @Override
    public ResponseEntity<List<Employee>> findAll(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(this.employeeService.findAll(pageable));
    }

    @Override
    public ResponseEntity<Employee> findById(UUID id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(this.employeeService.findById(id));
    }

    @Override
    public ResponseEntity<Void> update(Employee employee) {
        this.employeeService.update(employee);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> delete(UUID id) {
        this.employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
