package com.indigital.customers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indigital.customers.dto.request.CustomerCreateRequest;
import com.indigital.customers.dto.response.CustomerIndicatorsResponse;
import com.indigital.customers.dto.response.CustomerResponse;
import com.indigital.customers.dto.response.MonthYearStat;
import com.indigital.customers.dto.response.MonthlyBirthRate;
import com.indigital.customers.dto.response.PeriodSummary;
import com.indigital.customers.exception.DuplicateResourceException;
import com.indigital.customers.exception.ResourceNotFoundException;
import com.indigital.customers.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @Test
    @DisplayName("POST /api/v1/customers - Debe retornar 201 Created con cabecera Location y cuerpo del cliente")
    void createCustomer_whenValidPayload_shouldReturn201Created() throws Exception {
        // Arrange
        CustomerCreateRequest request = new CustomerCreateRequest(
                "Carlos", "Santana", "carlos.santana@example.com", "72849102", LocalDate.of(1994, 7, 15)
        );
        CustomerResponse response = new CustomerResponse(
                1L, "Carlos", "Santana", "carlos.santana@example.com", "72849102",
                LocalDateTime.now(), LocalDate.of(1994, 7, 15), 32
        );
        when(customerService.createCustomer(any(CustomerCreateRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/customers/1")))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Carlos")))
                .andExpect(jsonPath("$.dni", is("72849102")))
                .andExpect(jsonPath("$.email", is("carlos.santana@example.com")));
    }

    @Test
    @DisplayName("POST /api/v1/customers - Debe retornar 400 Bad Request cuando el DNI no tiene 8 dígitos numéricos")
    void createCustomer_whenInvalidDni_shouldReturn400BadRequest() throws Exception {
        // Arrange
        CustomerCreateRequest request = new CustomerCreateRequest(
                "Carlos", "Santana", "carlos.santana@example.com", "123", LocalDate.of(1994, 7, 15)
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Error de Validación")))
                .andExpect(jsonPath("$.validationErrors[0].field", is("dni")))
                .andExpect(jsonPath("$.validationErrors[0].reason", containsString("8 dígitos")));
    }

    @Test
    @DisplayName("POST /api/v1/customers - Debe retornar 400 Bad Request cuando el Email es inválido")
    void createCustomer_whenInvalidEmail_shouldReturn400BadRequest() throws Exception {
        // Arrange
        CustomerCreateRequest request = new CustomerCreateRequest(
                "Carlos", "Santana", "correo-invalido", "72849102", LocalDate.of(1994, 7, 15)
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.validationErrors[0].field", is("email")));
    }

    @Test
    @DisplayName("POST /api/v1/customers - Debe retornar 409 Conflict cuando el DNI o Email ya existe")
    void createCustomer_whenDuplicateResource_shouldReturn409Conflict() throws Exception {
        // Arrange
        CustomerCreateRequest request = new CustomerCreateRequest(
                "Carlos", "Santana", "carlos.santana@example.com", "72849102", LocalDate.of(1994, 7, 15)
        );
        when(customerService.createCustomer(any(CustomerCreateRequest.class)))
                .thenThrow(new DuplicateResourceException("El DNI '72849102' ya se encuentra registrado"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Recurso Duplicado")))
                .andExpect(jsonPath("$.message", containsString("ya se encuentra registrado")));
    }

    @Test
    @DisplayName("GET /api/v1/customers - Debe retornar 200 OK con la lista de clientes")
    void getCustomers_shouldReturn200AndList() throws Exception {
        // Arrange
        CustomerResponse customer = new CustomerResponse(
                1L, "Carlos", "Santana", "carlos@example.com", "72849102",
                LocalDateTime.now(), LocalDate.of(1994, 7, 15), 32
        );
        when(customerService.getCustomers(null, null)).thenReturn(List.of(customer));

        // Act & Assert
        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].dni", is("72849102")));
    }

    @Test
    @DisplayName("GET /api/v1/customers?dni=72849102 - Debe retornar 200 OK aplicando el filtro")
    void getCustomers_withDniFilter_shouldReturn200AndFilteredList() throws Exception {
        // Arrange
        CustomerResponse customer = new CustomerResponse(
                1L, "Carlos", "Santana", "carlos@example.com", "72849102",
                LocalDateTime.now(), LocalDate.of(1994, 7, 15), 32
        );
        when(customerService.getCustomers(eq("72849102"), eq(null))).thenReturn(List.of(customer));

        // Act & Assert
        mockMvc.perform(get("/api/v1/customers").param("dni", "72849102"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].dni", is("72849102")));
    }

    @Test
    @DisplayName("GET /api/v1/customers/indicators - Debe retornar 200 OK con los indicadores calculados")
    void getIndicators_shouldReturn200AndMetrics() throws Exception {
        // Arrange
        CustomerIndicatorsResponse indicators = new CustomerIndicatorsResponse(
                10L,
                8,
                List.of(new MonthYearStat(1994, 7, "Julio", "1994-07", 3L)),
                new PeriodSummary(1994, 7, "Julio", "1994-07", 3L, "Julio 1994 (3 clientes)"),
                new PeriodSummary(1990, 1, "Enero", "1990-01", 1L, "Enero 1990 (1 cliente)"),
                List.of(new MonthlyBirthRate(7, "Julio", 3L, 30.0, "30.00%"))
        );
        when(customerService.getIndicators()).thenReturn(indicators);

        // Act & Assert
        mockMvc.perform(get("/api/v1/customers/indicators"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCustomers", is(10)))
                .andExpect(jsonPath("$.highestBirthPeriod.period", is("1994-07")))
                .andExpect(jsonPath("$.lowestBirthPeriod.period", is("1990-01")))
                .andExpect(jsonPath("$.birthsByMonthYear", hasSize(1)))
                .andExpect(jsonPath("$.monthlyBirthRates", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/v1/customers/{id} - Debe retornar 404 Not Found cuando el cliente no existe")
    void getCustomerById_whenNotFound_shouldReturn404NotFound() throws Exception {
        // Arrange
        when(customerService.getCustomerById(99L))
                .thenThrow(new ResourceNotFoundException("No se encontró el cliente con ID 99"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/customers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("No se encontró el cliente con ID 99")));
    }
}
