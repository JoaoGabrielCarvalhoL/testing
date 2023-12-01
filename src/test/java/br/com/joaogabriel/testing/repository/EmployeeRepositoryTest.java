package br.com.joaogabriel.testing.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import br.com.joaogabriel.testing.model.Employee;

@DataJpaTest
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    public void setup() {
        this.employee = new Employee.Builder()
            .firstName("João Gabriel")
            .lastName("Carvalho")
            .email("27.joaogabriel@gmail.com")
            .cellphone("14 999999999")
            .build();
    }
    
    @DisplayName("Given employee object, when save employee into database, must be returned employee object persisted.")
    @Test
    public void givenEmployeeObject_whenSaveEmployee_thenReturnEmployeePersisted() {

        Employee saved = employeeRepository.save(employee);

        Assertions.assertThat(saved).isNotNull();
        Assertions.assertThat(saved.getId()).isNotNull();
        Assertions.assertThat(saved.getFirstName()).isEqualTo(employee.getFirstName());
    }

    @DisplayName("Given employee list, when retrieve all employes, then return all employees persisted into database.")
    @Test
    public void givenEmployeeList_whenRetrieveAllEmployess_thenReturnAllEmployeesPersisted() {

        Employee firstEmployee = new Employee.Builder()
            .firstName("João Gabriel")
            .lastName("Carvalho")
            .email("27.joaogabriel@gmail.com")
            .cellphone("14 999999999")
            .build();

        Employee secondEmployee = new Employee.Builder()
            .firstName("Laís Mansano")
            .lastName("Pereira")
            .email("lais@gmail.com")
            .cellphone("14 999999999")
            .build();
        
        List<Employee> employees = new ArrayList<Employee>();
        employees.add(firstEmployee);
        employees.add(secondEmployee);
        
        employeeRepository.saveAll(employees);

        List<Employee> result = employeeRepository.findAll();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.get(0)).isEqualTo(employees.get(0));
        Assertions.assertThat(result.get(1)).isEqualTo(employees.get(1));
    }

    @DisplayName("Given existing id into database, when find employee by id, must be return employee object.")
    @Test
    public void givenExistingValidId_whenFindEmployeeById_thenReturnEmployee() {
        
        Employee saved = employeeRepository.save(employee);
        UUID id = saved.getId();

        Optional<Employee> result = employeeRepository.findById(id);
        
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.get().getId()).isEqualTo(id);
    }

    @DisplayName("Given existing valid email into database, when find employee by email, must be return employee object.")
    @Test
    public void givenExistingValidEmail_whenFindEmployeeByEmail_thenReturnEmployee() {
    
        Employee saved = employeeRepository.save(employee);
        String email = saved.getEmail();

        Optional<Employee> result = employeeRepository.findByEmail(email);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isPresent();
        
    }

    @DisplayName("Given employeed persisted into database, when update object, then return updated object.")
    @Test
    public void givenEmployeePersisted_whenUpdateObject_thenReturnEmployeedUpdated() {
       
        Employee saved = employeeRepository.save(employee);

        String cellphone = "14 98989898988";
        String email = "27.joaogabrielcarvalho@gmail.com"; 
        String firstName = "João Gabriel Carvalho"; 
        String lastName = "Cruz";

        saved.setCellphone(cellphone);
        saved.setEmail(email);
        saved.setFirstName(firstName);
        saved.setLastName(lastName);

        Employee updated = employeeRepository.save(saved);

        Assertions.assertThat(saved.getId()).isEqualTo(updated.getId());
        Assertions.assertThat(updated).isNotNull();
        Assertions.assertThat(updated.getCellphone()).isEqualTo(cellphone);
        Assertions.assertThat(updated.getEmail()).isEqualTo(email);
        Assertions.assertThat(updated.getFirstName()).isEqualTo(firstName);
        Assertions.assertThat(updated.getLastName()).isEqualTo(lastName);
    }

    @DisplayName("Given existing employee, when delete employee, then return")
    @Test
    public void givenExistingEmployee_whenDeleteEmployee_thenReturn() {
        
        Employee saved = employeeRepository.save(employee);

        employeeRepository.delete(saved);
    }

    @DisplayName("Given existing employee persisted into database, when search by first name and last name, must return employee object.")
    @Test
    public void givenExistingEmployee_whenValidFirstNameAndLastName_thenReturnEmployeeObject() {
        String firstName = "João Gabriel"; 
        String lastName = "Carvalho";

        Employee employee = new Employee.Builder()
            .firstName(firstName)
            .lastName(lastName)
            .email("27.joaogabriel@gmail.com")
            .cellphone("14 999999999")
            .build();

        Employee saved = employeeRepository.save(employee);

        Employee result = employeeRepository.findByFullname(firstName, lastName);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(saved.getFirstName()).isEqualTo(result.getFirstName());
        Assertions.assertThat(saved.getLastName()).isEqualTo(result.getLastName());

    }

}
