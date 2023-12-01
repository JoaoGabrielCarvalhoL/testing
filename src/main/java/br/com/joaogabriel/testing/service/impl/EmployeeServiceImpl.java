package br.com.joaogabriel.testing.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.joaogabriel.testing.exception.ResourceAlreadyUsedException;
import br.com.joaogabriel.testing.exception.ResourceNotFoundException;
import br.com.joaogabriel.testing.model.Employee;
import br.com.joaogabriel.testing.repository.EmployeeRepository;
import br.com.joaogabriel.testing.service.EmployeeService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final Logger logger = Logger.getLogger(EmployeeServiceImpl.class.getName());
    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public boolean isValidEmail(String email) {
        logger.info("Checking email availability: " + email);
        Optional<Employee> employee = this.employeeRepository.findByEmail(email);
        if (employee.isEmpty()) {
            return true;
        }
        throw new ResourceAlreadyUsedException("Email unavailable!");
    }

    @Override
    public Employee save(Employee employee) {
        isValidEmail(employee.getEmail());
        logger.info("Saving employee into database.");
        return this.employeeRepository.save(employee);
    }

    @Override
    public Employee update(Employee employee) {
        logger.info("Updating employee into database.");
        return this.employeeRepository.saveAndFlush(employee);
    }

    @Override
    public Employee findById(UUID id) {
        logger.info("Getting employee by id: " + id);
        return this.employeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found into database. Id: " + id));
    }

    @Override
    public List<Employee> findAll(Pageable pageable) {
        logger.info("Find all employers");
        return this.employeeRepository.findAll(pageable)
            .stream().toList();
    }

    @Override
    public void delete(UUID id) {
        logger.info("Deleting employeer by id: " + id);
        this.employeeRepository.delete(findById(id));
    }
    
    
}
