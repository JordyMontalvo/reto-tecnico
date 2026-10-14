# ⚛️ Frontend • Customers Portal

Hola, este es el cliente web del proyecto. Es una Single Page Application (SPA) construida con **React 19**, **TypeScript** y **Vite**, pensada para gestionar clientes, explorar las métricas demográficas de forma gráfica y exportar los datos cómodamente.

---

## 💡 Decisiones de Diseño y Experiencia de Usuario

Al armar la interfaz tuve en cuenta varios detalles prácticos para que fuera intuitiva y eficiente tanto para el usuario final como para quien revise la prueba:

1. **Sin sobre-ingeniería de estado:** Para este alcance, utilicé hooks nativos de React (`useState`, `useCallback`, `useMemo`) en lugar de añadir librerías pesadas como Redux o Zustand. El flujo de datos es unidireccional y fácil de seguir.
2. **CSS puro con variables:** En lugar de agregar frameworks que inflen el bundle, utilicé CSS moderno con variables para mantener un diseño limpio, profesional, adaptable a móviles y fácil de ajustar.
3. **Alternancia Gráficos vs. Tabla Analítica:** Chart.js permite ver visualmente la distribución de natalidad por mes y por período, pero a veces un evaluador o usuario necesita consultar la cifra exacta; por eso añadí un switch para alternar entre vista de barras y tabla resumen.
4. **Validaciones en vivo y preview de edad:** En el modal de registro, el usuario recibe retroalimentación inmediata sobre el formato del DNI (8 dígitos) y el correo. Además, al seleccionar la fecha de nacimiento se calcula y muestra la edad en tiempo real.
5. **Exportación a CSV compatible con Microsoft Excel:** Al exportar a CSV, se añade la cabecera `\uFEFF` (UTF-8 Byte Order Mark). Esto evita el problema clásico de caracteres con tildes o la 'ñ' corruptos cuando se abre el archivo en Excel. Además, la exportación respeta los filtros que tengas aplicados en la tabla.
6. **Acceso directo a Swagger:** En la barra superior agregué un botón que te lleva directamente a la documentación interactiva de la API en el backend.

---

## 🛠 Stack Tecnológico

* **React 19:** Functional components y hooks modernos.
* **TypeScript 5.9:** Tipado estricto en interfaces de datos y respuestas de API.
* **Vite 8:** Compilador rápido y servidor de desarrollo con Hot Module Replacement instantáneo.
* **Chart.js + react-chartjs-2:** Gráficos de barras interactivos con tooltips y responsive design.
* **Lucide React:** Iconografía moderna y ligera.
* **Vitest + React Testing Library:** Suite de pruebas unitarias para componentes y servicios.

---

## 📁 Estructura del Código

```
src/
├── components/          # Componentes de UI
│   ├── CustomerFormModal.tsx    # Modal de registro con validaciones y feedback
│   ├── CustomerTable.tsx        # Tabla de clientes, avatares y estados vacíos
│   ├── DemographicCharts.tsx    # Gráficos Chart.js y tabla comparativa
│   ├── IndicatorCards.tsx       # Tarjetas KPI de resumen
│   ├── Toast.tsx                # Notificaciones flotantes de éxito/error
│   └── ...
├── services/
│   └── customerService.ts       # Cliente HTTP centralizado y tipado con Fetch API
├── types/
│   └── customer.ts              # Interfaces TypeScript de dominio y API
├── index.css                    # Sistema de diseño con variables CSS
└── App.tsx                      # Componente raíz y coordinación de estado
```

---

## 🚀 Cómo ejecutarlo en tu máquina

### Prerrequisitos
* Node.js v18 o v20+ instalado (`node -v`).
* Backend de Spring Boot corriendo en `http://localhost:8080`.

### Comandos frecuentes

```bash
# 1. Instalar dependencias
npm install

# 2. Iniciar servidor de desarrollo
npm run dev

# 3. Correr las pruebas unitarias (Vitest)
npm test

# 4. Compilar paquete de producción
npm run build
```

Por defecto, Vite abrirá en [http://localhost:5173](http://localhost:5173) (o `http://localhost:5174` si el puerto 5173 está en uso).

---

## 🔌 Conexión con el Backend

Durante el desarrollo local, `vite.config.ts` incluye un proxy para que cualquier llamada a `/api/*` se redirija automáticamente al backend en `http://localhost:8080`:

```typescript
server: {
  port: 5173,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    },
  },
}
```

Esto evita problemas de CORS y permite que el frontend funcione de forma idéntica tanto en desarrollo como empaquetado tras un proxy inverso como Nginx.

---

## 🧪 Pruebas Unitarias

Se implementaron 11 pruebas con Vitest y React Testing Library cubriendo:

* **`customerService.test.ts`:** Verificación de peticiones GET, POST, normalización de parámetros de búsqueda y manejo de excepciones con `ApiError`.
* **`CustomerTable.test.tsx`:** Renderizado de columnas, cálculo de edad, formateo de fechas y estados de tabla vacía.
* **`CustomerFormModal.test.tsx`:** Validación de campos obligatorios, longitud exacta de DNI y bloqueo de fechas futuras.
