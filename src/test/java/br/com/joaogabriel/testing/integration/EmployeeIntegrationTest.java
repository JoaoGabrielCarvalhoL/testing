package br.com.joaogabriel.testing.integration;

import java.util.UUID;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import br.com.joaogabriel.testing.model.Employee;
import br.com.joaogabriel.testing.repository.EmployeeRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class EmployeeIntegrationTest {
    
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee employee;
    private String id;

    private final String baseUri = "/api/v1/employees";

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        this.employee = new Employee.Builder()
            .firstName("João Gabriel")
            .lastName("Carvalho")
            .email("27.joaogabriel@gmail.com")
            .cellphone("14 999999999")
            .build();
    }

    @AfterAll
    public void cleanup() {
        this.employeeRepository.deleteAll();
    }

    @DisplayName("Given employee object, when save employee into database, then return employee object persisted.")
    @Test
    @Order(1)
    public void givenEmployeeObject_whenSaveEmployee_thenReturnEmployeeObjectPersisted() throws JsonProcessingException, Exception {
        
        ResultActions responseActions = mockMvc.perform(MockMvcRequestBuilders.post(baseUri)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(employee)));

        responseActions.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(employee.getFirstName())));

        MvcResult result = responseActions.andReturn();
        String response = result.getResponse().getContentAsString();
        this.id = JsonPath.parse(response).read("$.id");
        System.out.println("ID: " + id);
    }

    @DisplayName("Given employee object with invalid email, when save employee into database, then throw ResourceAlreadyUsedException.")
    @Test
    @Order(2)
    public void givenEmployeeObjectWithInvalidEmail_whenSaveEmployee_thenThrowResourceAlreadyUsedException() throws JsonProcessingException, Exception {
        
        ResultActions responseActions = mockMvc.perform(MockMvcRequestBuilders.post(baseUri)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(employee)));

        responseActions.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Email unavailable!")));
    }

    @DisplayName("Given employee object, when retrieve all employees, then return list of employeers from database.")
    @Test
    @Order(3)
    public void givenEmployeeList_whenRetrieveAllEmployees_thenReturnListOfEmployeers() throws Exception {
        ResultActions responseActions = mockMvc.perform(MockMvcRequestBuilders
            .get(baseUri, PageRequest.of(0, 10, Sort.Direction.ASC, "firstName"))
            .contentType(MediaType.APPLICATION_JSON));
        
            responseActions.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Given existing id from database, when retrieve employeer by id, then return employeer object.")
    @Test
    @Order(4)
    public void givenExistingId_whenRetrieveEmployeerById_thenReturnEmployeeObject() throws Exception {
    
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees/{id}", id)
            .contentType(MediaType.APPLICATION_JSON));
        
        resultActions.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(id)));
    }

    @DisplayName("Given existing id, when delete employeer by id, then nothing.")
    @Test
    @Order(5)
    public void givenExistingId_whenDeleteEmployeerById_thenNothing() throws Exception {
    
        ResultActions resultActions = 
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/employees/{id}", id));

        resultActions.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @DisplayName("Given invalid id, when retrieve employeer by id, must be throw ResourceNotFoundException")
    @Test
    @Order(6)
    public void givenInvalidgId_whenRetrieveEmployeerById_thenThrowResourceNotFoundException() throws Exception {

        String id = UUID.randomUUID().toString();
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees/{id}", id)
            .contentType(MediaType.APPLICATION_JSON));

        resultActions.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Employee not found into database. Id: " + id)));
    }

    @DisplayName("Given invalid id, when delete employeer by id, must be throw ResourceNotFoundException")
    @Test
    @Order(7)
    public void givenInvalidId_whenDeleteEmployeerById_thenThrowResourceNotFoundException() throws Exception {
        
        String id = UUID.randomUUID().toString();

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/employees/{id}", id));

        resultActions.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Employee not found into database. Id: " + id)));
    }
}
