package com.indigital.customers.dto.response;

import com.indigital.customers.domain.Customer;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Schema(description = "Datos de respuesta de un cliente")
public record CustomerResponse(
        @Schema(description = "Identificador único", example = "1")
        Long id,

        @Schema(description = "Nombre", example = "Carlos")
        String nombre,

        @Schema(description = "Apellido", example = "Santana")
        String apellido,

        @Schema(description = "Correo electrónico", example = "carlos.santana@example.com")
        String email,

        @Schema(description = "DNI", example = "72849102")
        String dni,

        @Schema(description = "Fecha y hora de creación del registro", example = "2026-10-13T18:40:00")
        LocalDateTime fechaCreacion,

        @Schema(description = "Fecha de nacimiento", example = "1994-07-15")
        LocalDate fechaNacimiento,

        @Schema(description = "Edad en años", example = "32")
        Integer edad
) {
    public static CustomerResponse fromEntity(Customer customer) {
        if (customer == null) {
            return null;
        }
        Integer calculatedAge = customer.getFechaNacimiento() != null
                ? Period.between(customer.getFechaNacimiento(), LocalDate.now()).getYears()
                : null;

        return new CustomerResponse(
                customer.getId(),
                customer.getNombre(),
                customer.getApellido(),
                customer.getEmail(),
                customer.getDni(),
                customer.getFechaCreacion(),
                customer.getFechaNacimiento(),
                calculatedAge
        );
    }
}
