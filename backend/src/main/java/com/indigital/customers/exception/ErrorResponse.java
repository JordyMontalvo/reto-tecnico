package com.indigital.customers.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Estructura estandarizada de errores de la API")
public record ErrorResponse(
        @Schema(description = "Marca de tiempo del error", example = "2026-10-13T18:40:00")
        LocalDateTime timestamp,

        @Schema(description = "Código de estado HTTP", example = "400")
        int status,

        @Schema(description = "Descripción del estado HTTP", example = "Bad Request")
        String error,

        @Schema(description = "Mensaje descriptivo del error", example = "Error de validación en los datos enviados")
        String message,

        @Schema(description = "Ruta del recurso solicitado", example = "/api/v1/customers")
        String path,

        @Schema(description = "Detalle de errores específicos de validación por campo")
        List<ValidationErrorDetail> validationErrors
) {
    public record ValidationErrorDetail(
            @Schema(description = "Nombre del campo con error", example = "dni")
            String field,

            @Schema(description = "Valor rechazado", example = "123")
            Object rejectedValue,

            @Schema(description = "Mensaje explicativo del fallo", example = "El DNI debe contener exactamente 8 dígitos numéricos")
            String reason
    ) {}

    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, List.of());
    }

    public static ErrorResponse ofValidation(int status, String error, String message, String path, List<ValidationErrorDetail> errors) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, errors);
    }
}
