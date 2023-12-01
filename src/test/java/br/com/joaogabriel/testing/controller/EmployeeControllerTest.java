package br.com.joaogabriel.testing.controller;

import java.util.Collections;
import java.util.UUID;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.joaogabriel.testing.exception.ResourceAlreadyUsedException;
import br.com.joaogabriel.testing.exception.ResourceNotFoundException;
import br.com.joaogabriel.testing.model.Employee;
import br.com.joaogabriel.testing.service.EmployeeService;

@WebMvcTest
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    private Employee employee;

    private Employee saved;

    private Employee updated;

    private UUID id;

    private final String baseUri = "/api/v1/employees";

    @BeforeEach
    public void setup() {
        this.employee = new Employee.Builder()
            .firstName("João Gabriel")
            .lastName("Carvalho")
            .email("27.joaogabriel@gmail.com")
            .cellphone("14 999999999")
            .build();

        this.id = UUID.randomUUID();

        this.saved = new Employee.Builder()
            .id(id)
            .firstName("João Gabriel")
            .lastName("Carvalho")
            .email("27.joaogabriel@gmail.com")
            .cellphone("14 999999999")
            .build();

        this.updated = new Employee.Builder()
            .id(id)
            .firstName("João Gabriel")
            .lastName("Carvalho Lopes da Cruz")
            .email("27.joaogabriel@gmail.com")
            .cellphone("14 999999999")
            .build();
    }

    @DisplayName("Given employee object, when save employee into database, then return employee object persisted.")
    @Test
    public void givenEmployeeObject_whenSaveEmployee_thenReturnEmployeeObjectPersisted() throws JsonProcessingException, Exception {
        BDDMockito.given(employeeService.save(ArgumentMatchers.any(Employee.class)))
            .willReturn(saved);

        ResultActions responseActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(employee)));

        responseActions.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(employee.getFirstName())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(saved.getId().toString())));
    }

    @DisplayName("Given employee object with invalid email, when save employee into database, then throw ResourceAlreadyUsedException.")
    @Test
    public void givenEmployeeObjectWithInvalidEmail_whenSaveEmployee_thenThrowResourceAlreadyUsedException() throws JsonProcessingException, Exception {
        BDDMockito.given(employeeService.save(ArgumentMatchers.any(Employee.class)))
            .willThrow(new ResourceAlreadyUsedException("Email unavailable."));

        ResultActions responseActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(employee)));

        responseActions.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Email unavailable.")));
    }

    @DisplayName("Given employee object, when retrieve all employees, then return list of employeers from database.")
    @Test
    public void givenEmployeeList_whenRetrieveAllEmployees_thenReturnListOfEmployeers() throws Exception {
        BDDMockito.given(employeeService.findAll(ArgumentMatchers.any(PageRequest.class)))
            .willReturn(new PageImpl<Employee>(Collections.emptyList()).stream().toList());

        ResultActions responseActions = mockMvc.perform(MockMvcRequestBuilders
            .get(baseUri, PageRequest.of(0, 10, Sort.Direction.ASC, "firstName"))
            .contentType(MediaType.APPLICATION_JSON));
        
            responseActions.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Given existing id from database, when retrieve employeer by id, then return employeer object.")
    @Test
    public void givenExistingId_whenRetrieveEmployeerById_thenReturnEmployeeObject() throws Exception {
        BDDMockito.given(employeeService.findById(ArgumentMatchers.any(UUID.class)))
            .willReturn(saved);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees/{id}", id)
            .contentType(MediaType.APPLICATION_JSON));
        
        resultActions.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(id.toString())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(saved.getFirstName())));
    }

    @DisplayName("Given invalid id, when retrieve employeer by id, must be throw ResourceNotFoundException")
    @Test
    public void givenInvalidgId_whenRetrieveEmployeerById_thenThrowResourceNotFoundException() throws Exception {
        BDDMockito.given(employeeService.findById(ArgumentMatchers.any(UUID.class)))
            .willThrow(new ResourceNotFoundException("Employeer not found."));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees/{id}", id)
            .contentType(MediaType.APPLICATION_JSON));

        resultActions.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Employeer not found.")));
    }

    @DisplayName("Given employee object, when update employeer, then nothing")
    @Test
    public void givenEmployeeObject_whenUpdateEmployee_thenNothing() throws JsonProcessingException, Exception {
        BDDMockito.given(employeeService.update(ArgumentMatchers.any(Employee.class)))
            .willReturn(updated);
        
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put(baseUri)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(saved)));
        
        resultActions.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNoContent());
    }


    @DisplayName("Given existing id, when delete employeer by id, then nothing.")
    @Test
    public void givenExistingId_whenDeleteEmployeerById_thenNothing() throws Exception {
        BDDMockito.willDoNothing().given(employeeService).delete(ArgumentMatchers.any(UUID.class));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/employees/{id}", id));

        resultActions.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @DisplayName("Given invalid id, when delete employeer by id, must be throw ResourceNotFoundException")
    @Test
    public void givenInvalidId_whenDeleteEmployeerById_thenThrowResourceNotFoundException() throws Exception {
        
        BDDMockito.doThrow(new ResourceNotFoundException("Employeer not found."))
            .when(employeeService).delete(ArgumentMatchers.any(UUID.class));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/employees/{id}", id));

        resultActions.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Employeer not found.")));
    }
}
