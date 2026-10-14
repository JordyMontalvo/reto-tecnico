package com.indigital.customers.service;

import com.indigital.customers.domain.Customer;
import com.indigital.customers.dto.request.CustomerCreateRequest;
import com.indigital.customers.dto.response.CustomerIndicatorsResponse;
import com.indigital.customers.dto.response.CustomerResponse;
import com.indigital.customers.dto.response.MonthlyBirthRate;
import com.indigital.customers.exception.DuplicateResourceException;
import com.indigital.customers.exception.ResourceNotFoundException;
import com.indigital.customers.repository.CustomerRepository;
import com.indigital.customers.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer sampleCustomer1;
    private Customer sampleCustomer2;
    private Customer sampleCustomer3;

    @BeforeEach
    void setUp() {
        sampleCustomer1 = Customer.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan.perez@example.com")
                .dni("12345678")
                .fechaCreacion(LocalDateTime.now())
                .fechaNacimiento(LocalDate.of(1995, 5, 10))
                .build();

        sampleCustomer2 = Customer.builder()
                .id(2L)
                .nombre("María")
                .apellido("López")
                .email("maria.lopez@example.com")
                .dni("87654321")
                .fechaCreacion(LocalDateTime.now())
                .fechaNacimiento(LocalDate.of(1995, 5, 25))
                .build();

        sampleCustomer3 = Customer.builder()
                .id(3L)
                .nombre("Carlos")
                .apellido("Gómez")
                .email("carlos.gomez@example.com")
                .dni("45678901")
                .fechaCreacion(LocalDateTime.now())
                .fechaNacimiento(LocalDate.of(1998, 11, 15))
                .build();
    }

    @Test
    @DisplayName("Debe crear un cliente satisfactoriamente cuando los datos son válidos")
    void createCustomer_whenValidRequest_shouldSaveAndReturnResponse() {
        // Arrange
        CustomerCreateRequest request = new CustomerCreateRequest(
                "Juan", "Pérez", "juan.perez@example.com", "12345678", LocalDate.of(1995, 5, 10)
        );
        when(customerRepository.existsByDni("12345678")).thenReturn(false);
        when(customerRepository.existsByEmailIgnoreCase("juan.perez@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(sampleCustomer1);

        // Act
        CustomerResponse response = customerService.createCustomer(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nombre()).isEqualTo("Juan");
        assertThat(response.apellido()).isEqualTo("Pérez");
        assertThat(response.email()).isEqualTo("juan.perez@example.com");
        assertThat(response.dni()).isEqualTo("12345678");
        assertThat(response.fechaNacimiento()).isEqualTo(LocalDate.of(1995, 5, 10));
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Debe lanzar DuplicateResourceException cuando el DNI ya existe")
    void createCustomer_whenDuplicateDni_shouldThrowDuplicateResourceException() {
        // Arrange
        CustomerCreateRequest request = new CustomerCreateRequest(
                "Juan", "Pérez", "nuevo@example.com", "12345678", LocalDate.of(1995, 5, 10)
        );
        when(customerRepository.existsByDni("12345678")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("El DNI '12345678' ya se encuentra registrado");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Debe lanzar DuplicateResourceException cuando el Email ya existe")
    void createCustomer_whenDuplicateEmail_shouldThrowDuplicateResourceException() {
        // Arrange
        CustomerCreateRequest request = new CustomerCreateRequest(
                "Juan", "Pérez", "juan.perez@example.com", "99999999", LocalDate.of(1995, 5, 10)
        );
        when(customerRepository.existsByDni("99999999")).thenReturn(false);
        when(customerRepository.existsByEmailIgnoreCase("juan.perez@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("El correo electrónico 'juan.perez@example.com' ya se encuentra registrado");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando la fecha de nacimiento es en el futuro")
    void createCustomer_whenBirthDateInFuture_shouldThrowIllegalArgumentException() {
        // Arrange
        CustomerCreateRequest request = new CustomerCreateRequest(
                "Futuro", "Cliente", "futuro@example.com", "99999999", LocalDate.now().plusDays(1)
        );
        when(customerRepository.existsByDni("99999999")).thenReturn(false);
        when(customerRepository.existsByEmailIgnoreCase("futuro@example.com")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La fecha de nacimiento no puede ser posterior a la fecha actual");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Debe retornar lista de clientes consultados por filtros")
    void getCustomers_withFilters_shouldReturnMatchingCustomers() {
        // Arrange
        when(customerRepository.searchByFilters("1234", "perez")).thenReturn(List.of(sampleCustomer1));

        // Act
        List<CustomerResponse> results = customerService.getCustomers("1234", "perez");

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).dni()).isEqualTo("12345678");
    }

    @Test
    @DisplayName("Debe retornar cliente por ID cuando existe")
    void getCustomerById_whenExists_shouldReturnCustomer() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer1));

        // Act
        CustomerResponse response = customerService.getCustomerById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.dni()).isEqualTo("12345678");
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el ID no existe")
    void getCustomerById_whenNotFound_shouldThrowResourceNotFoundException() {
        // Arrange
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> customerService.getCustomerById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el cliente con ID 999");
    }

    @Test
    @DisplayName("Debe retornar indicadores con valores en cero de forma segura cuando no hay clientes")
    void getIndicators_whenNoCustomers_shouldReturnEmptyMetricsSafely() {
        // Arrange
        when(customerRepository.findAll()).thenReturn(List.of());

        // Act
        CustomerIndicatorsResponse response = customerService.getIndicators();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.totalCustomers()).isZero();
        assertThat(response.totalBirthPeriods()).isZero();
        assertThat(response.birthsByMonthYear()).isEmpty();
        assertThat(response.highestBirthPeriod()).isNull();
        assertThat(response.lowestBirthPeriod()).isNull();
        assertThat(response.monthlyBirthRates()).hasSize(12);
        assertThat(response.monthlyBirthRates().get(0).ratePercentage()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Debe calcular indicadores, picos, valles y tasas de natalidad con precisión matemática")
    void getIndicators_whenCustomersExist_shouldCalculateRatesAndPeaksAccurately() {
        // Arrange: 3 clientes (2 en Mayo 1995, 1 en Noviembre 1998)
        when(customerRepository.findAll()).thenReturn(List.of(sampleCustomer1, sampleCustomer2, sampleCustomer3));

        // Act
        CustomerIndicatorsResponse response = customerService.getIndicators();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.totalCustomers()).isEqualTo(3L);
        assertThat(response.totalBirthPeriods()).isEqualTo(2);

        // Validar lista de nacimientos por mes/año
        assertThat(response.birthsByMonthYear()).hasSize(2);
        assertThat(response.birthsByMonthYear().get(0).period()).isEqualTo("1995-05");
        assertThat(response.birthsByMonthYear().get(0).count()).isEqualTo(2L);
        assertThat(response.birthsByMonthYear().get(1).period()).isEqualTo("1998-11");
        assertThat(response.birthsByMonthYear().get(1).count()).isEqualTo(1L);

        // Validar pico máximo (Mayo 1995 con 2 clientes)
        assertThat(response.highestBirthPeriod()).isNotNull();
        assertThat(response.highestBirthPeriod().period()).isEqualTo("1995-05");
        assertThat(response.highestBirthPeriod().count()).isEqualTo(2L);

        // Validar valle mínimo (Noviembre 1998 con 1 cliente)
        assertThat(response.lowestBirthPeriod()).isNotNull();
        assertThat(response.lowestBirthPeriod().period()).isEqualTo("1998-11");
        assertThat(response.lowestBirthPeriod().count()).isEqualTo(1L);

        // Validar tasas mensuales (12 meses)
        assertThat(response.monthlyBirthRates()).hasSize(12);
        // Mayo (mes 5): 2 nacimientos de 3 = 66.67%
        MonthlyBirthRate mayoRate = response.monthlyBirthRates().stream()
                .filter(r -> r.month() == 5)
                .findFirst()
                .orElseThrow();
        assertThat(mayoRate.birthsCount()).isEqualTo(2L);
        assertThat(mayoRate.ratePercentage()).isEqualTo(66.67);
        assertThat(mayoRate.formattedRate()).isEqualTo("66.67%");

        // Noviembre (mes 11): 1 nacimiento de 3 = 33.33%
        MonthlyBirthRate novRate = response.monthlyBirthRates().stream()
                .filter(r -> r.month() == 11)
                .findFirst()
                .orElseThrow();
        assertThat(novRate.birthsCount()).isEqualTo(1L);
        assertThat(novRate.ratePercentage()).isEqualTo(33.33);

        // Mes sin nacimientos (ej: Enero = 0%)
        MonthlyBirthRate eneRate = response.monthlyBirthRates().stream()
                .filter(r -> r.month() == 1)
                .findFirst()
                .orElseThrow();
        assertThat(eneRate.birthsCount()).isZero();
        assertThat(eneRate.ratePercentage()).isEqualTo(0.0);
    }
}
