package com.indigital.customers.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estadística de nacimientos agrupados por mes y año específico")
public record MonthYearStat(
        @Schema(description = "Año de nacimiento", example = "1994")
        int year,

        @Schema(description = "Mes de nacimiento (1 al 12)", example = "7")
        int month,

        @Schema(description = "Nombre del mes en español", example = "Julio")
        String monthName,

        @Schema(description = "Período en formato ISO YYYY-MM", example = "1994-07")
        String period,

        @Schema(description = "Cantidad de clientes nacidos en este período", example = "3")
        long count
) {
}
