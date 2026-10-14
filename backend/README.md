# ☕ Backend • Customers Microservice

Hola, bienvenido al microservicio de clientes del proyecto. Esta API RESTful se encarga de la persistencia, validaciones de negocio, auditoría y cálculos demográficos de natalidad solicitados en el reto técnico.

Fue construida con **Java 21 (LTS)** y **Spring Boot 3.3.5**, utilizando **Gradle** como herramienta de construcción.

---

## 💡 Decisiones de Diseño y Enfoque

Al diseñar el backend busqué que fuera fácil de leer, mantenible y sin sorpresas para quien lo revise:

1. **Java Records para DTOs:** Toda la transferencia de datos (tanto entradas como salidas) usa `record`. Esto nos da inmutabilidad por defecto, reduce boilerplate y deja muy claras las propiedades que viajan en cada petición.
2. **Validación temprana con Jakarta Validation:** Se valida la estructura antes de tocar la base de datos (`@NotBlank`, `@Pattern` para 8 dígitos de DNI, email válido y fechas en pasado).
3. **Manejo de errores transparente (RFC 7807):** En lugar de devolver un `500` genérico o un stacktrace desordenado, implementé un `@RestControllerAdvice` que responde con un formato estándar (`status`, `error`, `message`, `validationErrors` si aplica). Los duplicados de DNI o email devuelven un `409 Conflict` claro.
4. **Cálculo dinámico de edad:** La edad no se guarda estática en la base de datos para no desactualizarse con el paso del tiempo. Se calcula al vuelo en el DTO usando `Period.between(fechaNacimiento, LocalDate.now())`.
5. **Base de datos H2 con datos semilla:** Para que puedas clonar y probar todo de inmediato sin configurar un motor externo, configuré H2 en memoria con 10 registros de prueba. Si se requiere producción con PostgreSQL, la entidad JPA y el repositorio están desacoplados y basta con cambiar las propiedades de conexión en `application.yml`.

---

## 🏛 Estructura del Proyecto

Organizado por capas siguiendo una arquitectura limpia y desacoplada:

```
src/main/java/com/indigital/customers/
├── config/             # Configuración de CORS dinámico, OpenAPI (Swagger) y carga inicial
├── controller/         # Endpoints REST (con anotaciones OpenAPI para documentación viva)
├── domain/             # Entidad JPA Customer con índices y timestamps automáticos
├── dto/
│   ├── request/        # Records de entrada con anotaciones de validación
│   └── response/       # Records de salida inmutables hacia el cliente
├── exception/          # Excepciones personalizadas y manejador global de errores
├── repository/         # Interfaces Spring Data JPA con queries personalizadas
└── service/            # Interfaz de servicio
    └── impl/           # Lógica de negocio y algoritmos matemáticos
```

---

## 📊 Algoritmos Demográficos de Natalidad

En `CustomerServiceImpl.java` se implementaron las siguientes métricas:

* **Nacimientos por Mes/Año:** Agrupa las fechas de nacimiento por `YearMonth` (ej: `1995-05`) y cuenta cuántos clientes corresponden a cada cohorte.
* **Picos y Valles:** Calcula el mes/año con mayor ($\max$) y menor ($\min$) cantidad de nacimientos, generando además etiquetas descriptivas (ej: `Mayo 1995 (3 clientes)`).
* **Tasa Mensual de Natalidad (Enero a Diciembre):** Calcula el porcentaje de nacimientos de cada mes sobre el total histórico:
  $$\text{Tasa} = \left(\frac{\text{Nacidos en el mes}}{\text{Total de Clientes}}\right) \times 100$$
  Se calcula con `BigDecimal` y redondeo `HALF_UP` a 2 decimales. Si la base de datos está vacía, devuelve `0.00%` en todos los meses de forma segura.

---

## 🔌 Catálogo de Endpoints

| Método | Ruta | Descripción | Respuestas típicas |
|:---|:---|:---|:---:|
| `POST` | `/api/v1/customers` | Registra un nuevo cliente | `201 Created`, `400`, `409` |
| `GET` | `/api/v1/customers` | Lista clientes (filtros opcionales `?dni=` y/o `?email=`) | `200 OK` |
| `GET` | `/api/v1/customers/indicators` | Consulta métricas demográficas y tasas de natalidad | `200 OK` |
| `GET` | `/api/v1/customers/{id}` | Consulta el detalle de un cliente por su ID | `200 OK`, `404` |
| `GET` | `/actuator/health` | Estado de salud del servicio | `200 OK` |

> *Nota:* Por comodidad también se habilitó la ruta sin prefijo `/api/v1` (por ejemplo `/customers`).

---

## 🚀 Cómo ejecutarlo en tu máquina

### Prerrequisitos
* Java 17 o 21 instalado (`java -version`).
* Puerto `8080` libre.

### Comandos frecuentes

```bash
# Iniciar la aplicación con recarga y datos semilla
./gradlew bootRun

# Correr toda la suite de pruebas automatizadas (22 tests)
./gradlew test

# Generar el reporte de cobertura con JaCoCo
./gradlew jacocoTestReport

# Compilar el JAR final ejecutable para producción
./gradlew bootJar
```

Una vez que la aplicación esté corriendo:
* **Swagger UI interactivo:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **Consola H2 (Base de datos):** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
  *(JDBC URL: `jdbc:h2:mem:customersdb`, Usuario: `sa`, Contraseña vacía)*

---

## 🧪 Pruebas Automatizadas

El proyecto cuenta con 22 pruebas automatizadas estructuradas con JUnit 5, Mockito y MockMvc:

* **`CustomerServiceTest`:** Pruebas unitarias que validan la matemática de los indicadores, control de división entre cero, detección de duplicados y flujo de creación.
* **`CustomerControllerTest`:** Pruebas de integración sobre los endpoints HTTP usando MockMvc para verificar códigos de estado (`201`, `400`, `404`, `409`), cabeceras `Location` y validación de campos.
* **`CustomerRepositoryTest`:** Pruebas de persistencia JPA para validar las restricciones de unicidad de DNI y Email.

Para revisar la cobertura de código en el navegador tras correr `./gradlew test jacocoTestReport`, abre el archivo:
`build/reports/jacoco/test/html/index.html` (>88% global, >96% en la lógica de negocio).
