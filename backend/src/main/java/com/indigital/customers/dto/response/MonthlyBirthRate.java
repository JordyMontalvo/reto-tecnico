package com.indigital.customers.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tasa de natalidad distribuida por mes calendario")
public record MonthlyBirthRate(
        @Schema(description = "Mes del año (1 = Enero ... 12 = Diciembre)", example = "7")
        int month,

        @Schema(description = "Nombre del mes en español", example = "Julio")
        String monthName,

        @Schema(description = "Cantidad acumulada de clientes nacidos en este mes", example = "4")
        long birthsCount,

        @Schema(description = "Porcentaje relativo respecto al total de clientes", example = "25.0")
        double ratePercentage,

        @Schema(description = "Tasa formateada con símbolo porcentual", example = "25.00%")
        String formattedRate
) {
}
