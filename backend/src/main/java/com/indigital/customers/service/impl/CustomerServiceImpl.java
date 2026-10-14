package com.indigital.customers.service.impl;

import com.indigital.customers.domain.Customer;
import com.indigital.customers.dto.request.CustomerCreateRequest;
import com.indigital.customers.dto.response.CustomerIndicatorsResponse;
import com.indigital.customers.dto.response.CustomerResponse;
import com.indigital.customers.dto.response.MonthYearStat;
import com.indigital.customers.dto.response.MonthlyBirthRate;
import com.indigital.customers.dto.response.PeriodSummary;
import com.indigital.customers.exception.DuplicateResourceException;
import com.indigital.customers.exception.ResourceNotFoundException;
import com.indigital.customers.repository.CustomerRepository;
import com.indigital.customers.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private static final Locale SPANISH_LOCALE = Locale.forLanguageTag("es-ES");

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de creación no puede ser nula");
        }

        String normalizedDni = request.dni().trim();
        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);
        String normalizedNombre = request.nombre().trim();
        String normalizedApellido = request.apellido().trim();

        if (customerRepository.existsByDni(normalizedDni)) {
            throw new DuplicateResourceException(
                    String.format("El DNI '%s' ya se encuentra registrado en el sistema", normalizedDni)
            );
        }

        if (customerRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new DuplicateResourceException(
                    String.format("El correo electrónico '%s' ya se encuentra registrado en el sistema", normalizedEmail)
            );
        }

        if (request.fechaNacimiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser posterior a la fecha actual");
        }

        Customer customer = Customer.builder()
                .nombre(normalizedNombre)
                .apellido(normalizedApellido)
                .email(normalizedEmail)
                .dni(normalizedDni)
                .fechaNacimiento(request.fechaNacimiento())
                .fechaCreacion(LocalDateTime.now())
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Cliente creado exitosamente con ID: {} y DNI: {}", savedCustomer.getId(), savedCustomer.getDni());

        return CustomerResponse.fromEntity(savedCustomer);
    }

    @Override
    public List<CustomerResponse> getCustomers(String dni, String email) {
        String cleanDni = (dni != null && !dni.trim().isEmpty()) ? dni.trim() : null;
        String cleanEmail = (email != null && !email.trim().isEmpty()) ? email.trim() : null;

        List<Customer> customers = customerRepository.searchByFilters(cleanDni, cleanEmail);

        return customers.stream()
                .map(CustomerResponse::fromEntity)
                .toList();
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del cliente no puede ser nulo");
        }
        return customerRepository.findById(id)
                .map(CustomerResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("No se encontró el cliente con ID %d", id)
                ));
    }

    @Override
    public CustomerIndicatorsResponse getIndicators() {
        List<Customer> allCustomers = customerRepository.findAll();
        long totalCustomers = allCustomers.size();

        if (totalCustomers == 0) {
            List<MonthlyBirthRate> emptyRates = generateDefaultMonthlyBirthRates(0);
            return new CustomerIndicatorsResponse(
                    0L,
                    0,
                    List.of(),
                    null,
                    null,
                    emptyRates
            );
        }

        // 1. Cantidad de clientes nacidos por mes/año
        Map<YearMonth, Long> countsByYearMonth = allCustomers.stream()
                .map(customer -> YearMonth.from(customer.getFechaNacimiento()))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<MonthYearStat> birthsByMonthYear = countsByYearMonth.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    YearMonth ym = entry.getKey();
                    long count = entry.getValue();
                    String monthName = getCapitalizedMonthName(ym.getMonthValue());
                    return new MonthYearStat(
                            ym.getYear(),
                            ym.getMonthValue(),
                            monthName,
                            ym.toString(),
                            count
                    );
                })
                .toList();

        // 2. Mes/Año con mayor cantidad de clientes nacidos
        Map.Entry<YearMonth, Long> highestEntry = countsByYearMonth.entrySet().stream()
                .max(Map.Entry.<YearMonth, Long>comparingByValue()
                        .thenComparing(Map.Entry.comparingByKey()))
                .orElse(null);

        PeriodSummary highestPeriod = null;
        if (highestEntry != null) {
            YearMonth ym = highestEntry.getKey();
            long count = highestEntry.getValue();
            String monthName = getCapitalizedMonthName(ym.getMonthValue());
            String label = String.format("%s %d (%d cliente%s)",
                    monthName, ym.getYear(), count, count == 1 ? "" : "s");
            highestPeriod = new PeriodSummary(
                    ym.getYear(),
                    ym.getMonthValue(),
                    monthName,
                    ym.toString(),
                    count,
                    label
            );
        }

        // 3. Mes/Año con menor cantidad de clientes nacidos
        Map.Entry<YearMonth, Long> lowestEntry = countsByYearMonth.entrySet().stream()
                .min(Map.Entry.<YearMonth, Long>comparingByValue()
                        .thenComparing(Comparator.comparing(Map.Entry<YearMonth, Long>::getKey).reversed()))
                .orElse(null);

        PeriodSummary lowestPeriod = null;
        if (lowestEntry != null) {
            YearMonth ym = lowestEntry.getKey();
            long count = lowestEntry.getValue();
            String monthName = getCapitalizedMonthName(ym.getMonthValue());
            String label = String.format("%s %d (%d cliente%s)",
                    monthName, ym.getYear(), count, count == 1 ? "" : "s");
            lowestPeriod = new PeriodSummary(
                    ym.getYear(),
                    ym.getMonthValue(),
                    monthName,
                    ym.toString(),
                    count,
                    label
            );
        }

        // 4. Tasa de Natalidad de cada mes (Enero a Diciembre)
        Map<Integer, Long> countsByCalendarMonth = allCustomers.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getFechaNacimiento().getMonthValue(),
                        Collectors.counting()
                ));

        List<MonthlyBirthRate> monthlyRates = new ArrayList<>(12);
        for (int m = 1; m <= 12; m++) {
            long birthsInMonth = countsByCalendarMonth.getOrDefault(m, 0L);
            double rate = ((double) birthsInMonth / totalCustomers) * 100.0;
            BigDecimal roundedRate = BigDecimal.valueOf(rate).setScale(2, RoundingMode.HALF_UP);
            String formatted = String.format(Locale.US, "%.2f%%", roundedRate.doubleValue());

            monthlyRates.add(new MonthlyBirthRate(
                    m,
                    getCapitalizedMonthName(m),
                    birthsInMonth,
                    roundedRate.doubleValue(),
                    formatted
            ));
        }

        return new CustomerIndicatorsResponse(
                totalCustomers,
                birthsByMonthYear.size(),
                birthsByMonthYear,
                highestPeriod,
                lowestPeriod,
                monthlyRates
        );
    }

    private List<MonthlyBirthRate> generateDefaultMonthlyBirthRates(long total) {
        List<MonthlyBirthRate> list = new ArrayList<>(12);
        for (int m = 1; m <= 12; m++) {
            list.add(new MonthlyBirthRate(
                    m,
                    getCapitalizedMonthName(m),
                    0L,
                    0.0,
                    "0.00%"
            ));
        }
        return list;
    }

    private String getCapitalizedMonthName(int monthValue) {
        String name = Month.of(monthValue).getDisplayName(TextStyle.FULL, SPANISH_LOCALE);
        if (name == null || name.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
