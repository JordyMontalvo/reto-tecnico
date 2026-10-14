package com.indigital.customers.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumen de un período destacado (mayor o menor nacimientos)")
public record PeriodSummary(
        @Schema(description = "Año", example = "1994")
        int year,

        @Schema(description = "Mes (1 al 12)", example = "7")
        int month,

        @Schema(description = "Nombre del mes", example = "Julio")
        String monthName,

        @Schema(description = "Período YYYY-MM", example = "1994-07")
        String period,

        @Schema(description = "Cantidad de clientes nacidos", example = "5")
        long count,

        @Schema(description = "Etiqueta descriptiva amigable", example = "Julio 1994 (5 clientes)")
        String label
) {
}
