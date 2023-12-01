package br.com.joaogabriel.testing.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.joaogabriel.testing.model.Employee;
import java.util.Optional;


public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    Optional<Employee> findByEmail(String email);

    @Query("SELECT e FROM Employee e where e.firstName =:firstName and e.lastName =:lastName")
    Employee findByFullname(@Param("firstName") String firstName, @Param("lastName") String lastName);
    
}
