package br.com.joaogabriel.testing.model;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_employee")
public class Employee implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String firstName; 

    @Column(nullable = false, length = 100)
    private String lastName; 

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false)
    private String cellphone;

    public Employee() {}

    public Employee(UUID id, String firstName, String lastName, String email, String cellphone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.cellphone = cellphone;
    }

    public Employee(String firstName, String lastName, String email, String cellphone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.cellphone = cellphone;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Employee other = (Employee) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public static class Builder {
        private Employee employee = new Employee();

        public Builder id(UUID id) {
            this.employee.setId(id);
            return this;
        }

        public Builder firstName(String firstName) {
            this.employee.setFirstName(firstName);
            return this;
        }

        public Builder lastName(String lastName) {
            this.employee.setLastName(lastName);
            return this;
        }

        public Builder email(String email) {
            this.employee.setEmail(email);
            return this;
        }

        public Builder cellphone(String cellphone) {
            this.employee.setCellphone(cellphone);
            return this;
        }

        public Employee build() {
            return this.employee;
        }
    }
}
