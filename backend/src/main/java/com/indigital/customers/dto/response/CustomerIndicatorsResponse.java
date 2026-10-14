package com.indigital.customers.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Consolidado de indicadores demográficos de clientes")
public record CustomerIndicatorsResponse(
        @Schema(description = "Total de clientes registrados en el sistema", example = "16")
        long totalCustomers,

        @Schema(description = "Cantidad de períodos (mes/año) con nacimientos registrados", example = "12")
        int totalBirthPeriods,

        @Schema(description = "Lista detallada de clientes nacidos por mes/año")
        List<MonthYearStat> birthsByMonthYear,

        @Schema(description = "Mes/Año con mayor cantidad de clientes nacidos")
        PeriodSummary highestBirthPeriod,

        @Schema(description = "Mes/Año con menor cantidad de clientes nacidos")
        PeriodSummary lowestBirthPeriod,

        @Schema(description = "Tasa de natalidad para cada mes del año (Enero a Diciembre)")
        List<MonthlyBirthRate> monthlyBirthRates
) {
}
