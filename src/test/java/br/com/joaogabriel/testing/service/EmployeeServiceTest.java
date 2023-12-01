package br.com.joaogabriel.testing.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import br.com.joaogabriel.testing.exception.ResourceAlreadyUsedException;
import br.com.joaogabriel.testing.exception.ResourceNotFoundException;
import br.com.joaogabriel.testing.model.Employee;
import br.com.joaogabriel.testing.repository.EmployeeRepository;
import br.com.joaogabriel.testing.service.impl.EmployeeServiceImpl;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService; //Must be implementation, not a interface.

    private Employee employee;
    private Employee saved;
    private UUID id;

    @BeforeEach
    public void setup() {
        this.id = UUID.randomUUID();

        this.employee = new Employee.Builder()
            .firstName("João Gabriel")
            .lastName("Carvalho")
            .email("27.joaogabriel@gmail.com")
            .cellphone("14 999999999")
            .build();

        this.saved = new Employee.Builder().id(id)
            .firstName("João Gabriel")
            .lastName("Carvalho")
            .email("27.joaogabriel@gmail.com")
            .cellphone("14 999999999")
            .build();
    }

    @DisplayName("Given employee object, when save employee, then return employee object persisted from database.")
    @Test
    public void givenEmployeeObject_whenSaveEmployee_thenReturnEmployeeObjectPersisted() {
        BDDMockito.given(employeeRepository.findByEmail(this.employee.getEmail()))
            .willReturn(Optional.empty());

        BDDMockito.given(employeeRepository.save(Mockito.any())).willReturn(this.saved);

        Employee saved = this.employeeService.save(employee);
        Assertions.assertThat(saved).isNotNull();
        Assertions.assertThat(saved.getId()).isNotNull();
        Assertions.assertThat(saved.getFirstName()).isEqualTo(employee.getFirstName());
    }

    @DisplayName("Given employee object, when save employee with unavailable email, then throw ResourceAlreadyUsedException")
    @Test
    public void givenEmployeeObject_whenSaveEmployeeWithUnavailableEmail_thenThrowException() {
        BDDMockito.given(employeeRepository.findByEmail(this.employee.getEmail()))
            .willThrow(new ResourceAlreadyUsedException("Email Unavaible"));
        
        Throwable throwable = Assertions.catchThrowable(() -> {
            employeeService.save(this.employee);
        });

        org.junit.jupiter.api.Assertions.assertThrows(ResourceAlreadyUsedException.class, () -> employeeService.save(employee)); //Another way
        Mockito.verify(employeeRepository, never()).save(Mockito.any());

        Assertions.assertThat(throwable).isInstanceOf(ResourceAlreadyUsedException.class);
        
    }

    @DisplayName("Given employee list, when retrieve all employees, then return list of employees")
    @Test
    public void givenEmployeeList_whenRetrieveAllEmployess_thenReturnListOfEmployeers() {
        BDDMockito.given(employeeRepository.findAll(ArgumentMatchers.any(PageRequest.class)))
        .willReturn(new PageImpl<>(Collections.emptyList()));

        List<Employee> employees = this.employeeService.findAll(PageRequest.of(0, 10, Sort.Direction.ASC, "firstName"));
        Assertions.assertThat(employees).isNotNull();
        Assertions.assertThat(employees).isEmpty();
    }

    @DisplayName("Given existing id from employeer, when retrieve employeer by id, then return employee object persisted from database.")
    @Test
    public void givenExistingIdFromEmployeer_whenRetrieveEmployeerById_thenReturnEmployeeObject() {
        BDDMockito.given(employeeRepository.findById(ArgumentMatchers.any(UUID.class)))
            .willReturn(Optional.of(this.saved));

        Employee employee = this.employeeService.findById(id);
        Assertions.assertThat(employee.getId()).isEqualTo(id);
        Assertions.assertThat(employee).isEqualTo(this.saved);
    }

    @DisplayName("Given invalid id from employee, when retrieve employeer by id, must be throw ResourceNotFoundException")
    @Test
    public void givenInvalidIdFromEmployeer_whenRetrieveEmployeerById_thenThrowResourceNotFoundExpcetion() {
        BDDMockito.given(employeeRepository.findById(ArgumentMatchers.any(UUID.class)))
            .willThrow(new ResourceNotFoundException("Employeer not found into database."));

        Throwable throwable = Assertions.catchThrowable(() -> { 
            employeeService.findById(id);
        });

        Assertions.assertThat(throwable.getMessage()).isEqualTo("Employeer not found into database.");
        Assertions.assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("Given employee object, when update employee, then return employee object updated.")
    @Test
    public void givenEmployeeObject_whenUpdateEmployee_thenReturnEmployeeObjectUpdated() {
        BDDMockito.given(this.employeeRepository.saveAndFlush(ArgumentMatchers.any(Employee.class)))
            .willReturn(this.saved);
        employee.setId(UUID.randomUUID());
        Employee updated = this.employeeService.update(employee);
        Assertions.assertThat(updated).isEqualTo(saved);
    }

    @DisplayName("Given existing id from employeer, when delete delete employee, then nothing")
    @Test
    public void givenExistingId_whenDeleteEmployee_thenNothing() {
        BDDMockito.given(this.employeeRepository.findById(ArgumentMatchers.any(UUID.class)))
            .willReturn(Optional.of(this.saved));
        BDDMockito.willDoNothing().given(employeeRepository).delete(this.saved);
        this.employeeService.delete(id);
        Mockito.verify(employeeRepository, times(1)).delete(saved);;
    }


    @DisplayName("Given invalid id from employeer, when delete delete employee, must be throw ResourceNotFoundException")
    @Test
    public void givenInvalidId_whenDeleteEmployee_thenThrowResourceNotFoundException() {
        BDDMockito.given(employeeRepository.findById(ArgumentMatchers.any(UUID.class)))
            .willThrow(new ResourceNotFoundException("Employee not found."));
        
        Throwable throwable = Assertions.catchThrowable(() -> {
            this.employeeService.delete(id);
        });
        Assertions.assertThat(throwable).isInstanceOf(ResourceNotFoundException.class);
        Assertions.assertThat(throwable.getMessage()).isEqualTo("Employee not found.");
        Mockito.verify(employeeRepository, never()).delete(ArgumentMatchers.any(Employee.class));
        
    }
}
