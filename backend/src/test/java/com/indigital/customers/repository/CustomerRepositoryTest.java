package com.indigital.customers.repository;

import com.indigital.customers.domain.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();

        Customer c1 = Customer.builder()
                .nombre("Pedro")
                .apellido("Suárez")
                .email("pedro.suarez@example.com")
                .dni("11223344")
                .fechaNacimiento(LocalDate.of(1991, 3, 14))
                .fechaCreacion(LocalDateTime.now().minusDays(2))
                .build();

        Customer c2 = Customer.builder()
                .nombre("Laura")
                .apellido("Mendoza")
                .email("laura.mendoza@example.com")
                .dni("55667788")
                .fechaNacimiento(LocalDate.of(1996, 9, 21))
                .fechaCreacion(LocalDateTime.now().minusDays(1))
                .build();

        customerRepository.save(c1);
        customerRepository.save(c2);
    }

    @Test
    @DisplayName("Debe verificar existencia por DNI correctamente")
    void existsByDni_shouldReturnTrueWhenExists() {
        assertThat(customerRepository.existsByDni("11223344")).isTrue();
        assertThat(customerRepository.existsByDni("00000000")).isFalse();
    }

    @Test
    @DisplayName("Debe verificar existencia por Email ignorando mayúsculas y minúsculas")
    void existsByEmailIgnoreCase_shouldReturnTrue() {
        assertThat(customerRepository.existsByEmailIgnoreCase("PEDRO.SUAREZ@EXAMPLE.COM")).isTrue();
        assertThat(customerRepository.existsByEmailIgnoreCase("otro@example.com")).isFalse();
    }

    @Test
    @DisplayName("Debe filtrar clientes por coincidencia parcial de DNI")
    void searchByFilters_withDni_shouldReturnMatchingCustomer() {
        List<Customer> result = customerRepository.searchByFilters("1122", null);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Pedro");
    }

    @Test
    @DisplayName("Debe retornar todos los clientes ordenados por fechaCreacion DESC cuando no hay filtros")
    void searchByFilters_withoutFilters_shouldReturnAll() {
        List<Customer> result = customerRepository.searchByFilters(null, null);
        assertThat(result).hasSize(2);
        // Laura fue creada más recientemente (minusDays(1) vs minusDays(2))
        assertThat(result.get(0).getNombre()).isEqualTo("Laura");
        assertThat(result.get(1).getNombre()).isEqualTo("Pedro");
    }
}
