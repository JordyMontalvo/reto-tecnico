package com.indigital.customers.controller;

import com.indigital.customers.dto.request.CustomerCreateRequest;
import com.indigital.customers.dto.response.CustomerIndicatorsResponse;
import com.indigital.customers.dto.response.CustomerResponse;
import com.indigital.customers.exception.ErrorResponse;
import com.indigital.customers.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/customers", "/customers"})
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Operaciones para la administración de clientes e indicadores")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    @PostMapping
    @Operation(
            summary = "Crear nuevo cliente",
            description = "Registra un nuevo cliente con sus datos personales y fecha de nacimiento. " +
                    "La fecha de creación se genera automáticamente en el servidor."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Cliente creado exitosamente",
                    headers = @Header(name = "Location", description = "URI del nuevo cliente creado"),
                    content = @Content(schema = @Schema(implementation = CustomerResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos o faltantes",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "DNI o Email ya se encuentran registrados",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerCreateRequest request) {
        log.info("Recibida solicitud para registrar cliente: DNI={}, Email={}, Nombre={} {}", 
                request.dni(), request.email(), request.nombre(), request.apellido());
        CustomerResponse created = customerService.createCustomer(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/customers/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @Operation(
            summary = "Consultar clientes con o sin filtros",
            description = "Obtiene la lista de clientes. Se puede filtrar opcionalmente por DNI y/o correo electrónico. " +
                    "Si no se envían parámetros, retorna la totalidad de los clientes."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de clientes obtenido correctamente"
            )
    })
    public ResponseEntity<List<CustomerResponse>> getCustomers(
            @Parameter(description = "Filtro opcional por DNI (búsqueda parcial o exacta)", example = "72849102")
            @RequestParam(required = false) String dni,
            @Parameter(description = "Filtro opcional por correo electrónico", example = "carlos@example.com")
            @RequestParam(required = false) String email
    ) {
        List<CustomerResponse> list = customerService.getCustomers(dni, email);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/indicators")
    @Operation(
            summary = "Consultar indicadores demográficos",
            description = "Calcula y retorna estadísticas de natalidad: distribución por mes/año, período pico con mayor " +
                    "nacimientos, período con menor nacimientos y tasa de natalidad mensual porcentual."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Indicadores calculados exitosamente",
                    content = @Content(schema = @Schema(implementation = CustomerIndicatorsResponse.class))
            )
    })
    public ResponseEntity<CustomerIndicatorsResponse> getIndicators() {
        CustomerIndicatorsResponse indicators = customerService.getIndicators();
        return ResponseEntity.ok(indicators);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar cliente por ID",
            description = "Retorna el detalle completo de un cliente a partir de su ID único."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cliente encontrado",
                    content = @Content(schema = @Schema(implementation = CustomerResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente no encontrado con el ID especificado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<CustomerResponse> getCustomerById(
            @Parameter(description = "ID único del cliente", example = "1")
            @PathVariable Long id
    ) {
        CustomerResponse customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }
}
