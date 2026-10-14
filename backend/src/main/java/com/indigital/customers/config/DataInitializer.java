package com.indigital.customers.config;

import com.indigital.customers.domain.Customer;
import com.indigital.customers.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) {
        if (customerRepository.count() == 0) {
            log.info("Inicializando datos semilla de clientes de demostración...");

            List<Customer> seedCustomers = List.of(
                    Customer.builder()
                            .nombre("Ana")
                            .apellido("García Mendoza")
                            .email("ana.garcia@gmail.com")
                            .dni("45678901")
                            .fechaNacimiento(LocalDate.of(1992, 5, 14))
                            .fechaCreacion(LocalDateTime.now().minusDays(30))
                            .build(),
                    Customer.builder()
                            .nombre("Carlos")
                            .apellido("Flores Ramos")
                            .email("carlos.flores@hotmail.com")
                            .dni("70123456")
                            .fechaNacimiento(LocalDate.of(1995, 5, 22))
                            .fechaCreacion(LocalDateTime.now().minusDays(25))
                            .build(),
                    Customer.builder()
                            .nombre("Lucía")
                            .apellido("Torres Quispe")
                            .email("lucia.torres@outlook.com")
                            .dni("71234567")
                            .fechaNacimiento(LocalDate.of(1995, 5, 30))
                            .fechaCreacion(LocalDateTime.now().minusDays(20))
                            .build(),
                    Customer.builder()
                            .nombre("Miguel")
                            .apellido("Reyes Benítez")
                            .email("miguel.reyes@gmail.com")
                            .dni("42345678")
                            .fechaNacimiento(LocalDate.of(1988, 11, 8))
                            .fechaCreacion(LocalDateTime.now().minusDays(18))
                            .build(),
                    Customer.builder()
                            .nombre("Sofía")
                            .apellido("Pérez Morales")
                            .email("sofia.perez@yahoo.com")
                            .dni("73456789")
                            .fechaNacimiento(LocalDate.of(1999, 11, 19))
                            .fechaCreacion(LocalDateTime.now().minusDays(15))
                            .build(),
                    Customer.builder()
                            .nombre("Jorge")
                            .apellido("Vargas Castillo")
                            .email("jorge.vargas@gmail.com")
                            .dni("44567890")
                            .fechaNacimiento(LocalDate.of(1990, 8, 3))
                            .fechaCreacion(LocalDateTime.now().minusDays(12))
                            .build(),
                    Customer.builder()
                            .nombre("Elena")
                            .apellido("Chávez Romero")
                            .email("elena.chavez@gmail.com")
                            .dni("75678901")
                            .fechaNacimiento(LocalDate.of(2001, 8, 27))
                            .fechaCreacion(LocalDateTime.now().minusDays(8))
                            .build(),
                    Customer.builder()
                            .nombre("Diego")
                            .apellido("Salazar Huamán")
                            .email("diego.salazar@outlook.com")
                            .dni("46789012")
                            .fechaNacimiento(LocalDate.of(1993, 1, 15))
                            .fechaCreacion(LocalDateTime.now().minusDays(5))
                            .build(),
                    Customer.builder()
                            .nombre("Valeria")
                            .apellido("Castro Ruiz")
                            .email("valeria.castro@gmail.com")
                            .dni("77890123")
                            .fechaNacimiento(LocalDate.of(1997, 3, 29))
                            .fechaCreacion(LocalDateTime.now().minusDays(2))
                            .build(),
                    Customer.builder()
                            .nombre("Rodrigo")
                            .apellido("Navarro Silva")
                            .email("rodrigo.navarro@gmail.com")
                            .dni("48901234")
                            .fechaNacimiento(LocalDate.of(1985, 12, 10))
                            .fechaCreacion(LocalDateTime.now().minusDays(1))
                            .build()
            );

            customerRepository.saveAll(seedCustomers);
            log.info("Se registraron {} clientes semilla exitosamente.", seedCustomers.size());
        }
    }
}
