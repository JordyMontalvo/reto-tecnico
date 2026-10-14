package com.indigital.customers.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Payload para la creación de un nuevo cliente")
public record CustomerCreateRequest(

        @Schema(description = "Nombre del cliente", example = "Carlos")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @Schema(description = "Apellido del cliente", example = "Santana")
        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
        String apellido,

        @Schema(description = "Correo electrónico único", example = "carlos.santana@example.com")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe ser una dirección de correo válida")
        @Size(max = 150, message = "El email no debe exceder los 150 caracteres")
        String email,

        @Schema(description = "Documento Nacional de Identidad (8 dígitos numéricos)", example = "72849102")
        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = "^\\d{8}$", message = "El DNI debe contener exactamente 8 dígitos numéricos")
        String dni,

        @Schema(description = "Fecha de nacimiento en formato YYYY-MM-DD", example = "1994-07-15")
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser una fecha anterior al día de hoy")
        LocalDate fechaNacimiento
) {
}
