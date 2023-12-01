package br.com.joaogabriel.testing.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

import br.com.joaogabriel.testing.model.Employee;

public interface EmployeeService {
    
    boolean isValidEmail(final String email);

    Employee save(final Employee employee);

    Employee update(final Employee employee);

    Employee findById(final UUID id); 

    List<Employee> findAll(Pageable pageable);

    void delete(final UUID id);
    
}
