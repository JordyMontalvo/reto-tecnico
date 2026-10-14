import { useState, useEffect, useCallback } from 'react'
import {
  Users,
  UserPlus,
  RefreshCw,
  Activity,
  Layers,
  Download,
  BookOpen,
} from 'lucide-react'
import { customerService } from './services/customerService'
import type {
  Customer,
  CustomerIndicators,
  CustomerCreatePayload,
} from './types/customer.types'
import { IndicatorsDashboard } from './components/IndicatorsDashboard'
import { CustomerFilters } from './components/CustomerFilters'
import { CustomerTable } from './components/CustomerTable'
import { CustomerFormModal } from './components/CustomerFormModal'
import { Toast, type ToastMessage } from './components/Toast'

export function App() {
  const [customers, setCustomers] = useState<Customer[]>([])
  const [indicators, setIndicators] = useState<CustomerIndicators | null>(null)
  const [isLoadingCustomers, setIsLoadingCustomers] = useState(true)
  const [isLoadingIndicators, setIsLoadingIndicators] = useState(true)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [toasts, setToasts] = useState<ToastMessage[]>([])
  const [currentFilters, setCurrentFilters] = useState<{ dni?: string; email?: string }>({})

  const showToast = useCallback((type: 'success' | 'error' | 'info', text: string) => {
    const id = Math.random().toString(36).substring(2, 9)
    setToasts((prev) => [...prev, { id, type, text }])
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id))
    }, 4500)
  }, [])

  const dismissToast = (id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id))
  }

  const fetchCustomers = useCallback(
    async (filters?: { dni?: string; email?: string }) => {
      setIsLoadingCustomers(true)
      try {
        const data = await customerService.getCustomers(filters)
        setCustomers(data)
      } catch (err: unknown) {
        const msg = err instanceof Error ? err.message : 'Error al obtener clientes'
        showToast('error', msg)
      } finally {
        setIsLoadingCustomers(false)
      }
    },
    [showToast]
  )

  const fetchIndicators = useCallback(async () => {
    setIsLoadingIndicators(true)
    try {
      const data = await customerService.getIndicators()
      setIndicators(data)
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Error al calcular indicadores'
      showToast('error', msg)
    } finally {
      setIsLoadingIndicators(false)
    }
  }, [showToast])

  const refreshAll = useCallback(() => {
    fetchCustomers(currentFilters)
    fetchIndicators()
  }, [fetchCustomers, fetchIndicators, currentFilters])

  useEffect(() => {
    fetchCustomers()
    fetchIndicators()
  }, [fetchCustomers, fetchIndicators])

  const handleFilter = (filters: { dni?: string; email?: string }) => {
    setCurrentFilters(filters)
    fetchCustomers(filters)
  }

  const handleResetFilters = () => {
    setCurrentFilters({})
    fetchCustomers({})
  }

  const handleCreateCustomer = async (payload: CustomerCreatePayload) => {
    const created = await customerService.createCustomer(payload)
    showToast('success', `Cliente ${created.nombre} ${created.apellido} creado exitosamente`)
    // Refresh table and indicators
    fetchCustomers(currentFilters)
    fetchIndicators()
  }

  const handleExportCsv = () => {
    if (customers.length === 0) {
      showToast('info', 'No hay clientes disponibles para exportar')
      return
    }

    const headers = [
      'ID',
      'DNI',
      'Nombre',
      'Apellido',
      'Correo Electrónico',
      'Fecha de Nacimiento',
      'Edad',
      'Fecha de Registro',
    ]

    const rows = customers.map((c) => [
      c.id,
      `"${c.dni}"`,
      `"${c.nombre.replace(/"/g, '""')}"`,
      `"${c.apellido.replace(/"/g, '""')}"`,
      `"${c.email.replace(/"/g, '""')}"`,
      `"${c.fechaNacimiento}"`,
      c.edad ?? '',
      `"${c.fechaCreacion}"`,
    ])

    const csvContent =
      '\uFEFF' + [headers.join(','), ...rows.map((r) => r.join(','))].join('\r\n')
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute(
      'download',
      `clientes_indigital_${new Date().toISOString().slice(0, 10)}.csv`
    )
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)

    showToast('success', `Se exportaron ${customers.length} clientes a CSV exitosamente`)
  }

  return (
    <div className="app-container">
      {/* App Header */}
      <header className="app-header">
        <div className="brand-wrapper">
          <div className="brand-badge-logo">
            <Layers size={26} />
          </div>
          <div className="brand-info">
            <h1>InDigital XP • Customers Portal</h1>
            <p>Módulo de Gestión de Clientes, Auditoría y Análisis Demográfico de Natalidad</p>
          </div>
        </div>

        <div className="header-meta">
          <span className="badge badge-emerald">
            <Activity size={13} />
            API Conectada
          </span>

          <a
            href="http://localhost:8080/swagger-ui.html"
            target="_blank"
            rel="noopener noreferrer"
            className="btn btn-secondary"
            title="Abrir documentación Swagger UI / OpenAPI"
          >
            <BookOpen size={15} />
            Swagger API
          </a>

          <button
            type="button"
            className="btn btn-secondary"
            onClick={refreshAll}
            title="Refrescar datos"
            disabled={isLoadingCustomers || isLoadingIndicators}
          >
            <RefreshCw
              size={15}
              className={isLoadingCustomers || isLoadingIndicators ? 'spinner' : ''}
            />
            Actualizar
          </button>

          <button
            type="button"
            className="btn btn-primary"
            onClick={() => setIsModalOpen(true)}
          >
            <UserPlus size={16} />
            Nuevo Cliente
          </button>
        </div>
      </header>

      {/* Main Content */}
      <main>
        {/* Sección de Indicadores */}
        <IndicatorsDashboard indicators={indicators} isLoading={isLoadingIndicators} />

        {/* Sección de Gestión de Clientes */}
        <section className="section-wrapper">
          <div className="section-header">
            <div className="section-title-wrap">
              <div className="brand-badge-logo" style={{ width: '36px', height: '36px' }}>
                <Users size={18} />
              </div>
              <div>
                <h2 className="section-title">Directorio de Clientes</h2>
                <p className="section-desc">
                  Consulta de clientes registrados con filtros dinámicos por DNI y correo
                </p>
              </div>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <span
                className="badge badge-slate"
                style={{ fontSize: '13px', padding: '6px 12px', fontWeight: 600 }}
              >
                {customers.length} {customers.length === 1 ? 'cliente' : 'clientes'}
              </span>
              <button
                type="button"
                className="btn btn-secondary"
                onClick={handleExportCsv}
                disabled={customers.length === 0}
                title="Descargar lista de clientes en formato CSV para Excel"
              >
                <Download size={15} />
                Exportar CSV
              </button>
            </div>
          </div>

          {/* Filtros */}
          <CustomerFilters
            onFilter={handleFilter}
            onReset={handleResetFilters}
            isLoading={isLoadingCustomers}
          />

          {/* Tabla de Resultados */}
          <CustomerTable
            customers={customers}
            isLoading={isLoadingCustomers}
            onResetFilters={handleResetFilters}
          />
        </section>
      </main>

      {/* Modal de Creación */}
      <CustomerFormModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSubmit={handleCreateCustomer}
      />

      {/* Sistema de Notificaciones Toast */}
      <Toast toasts={toasts} onDismiss={dismissToast} />
    </div>
  )
}

export default App
