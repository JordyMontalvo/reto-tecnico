import React from 'react'
import { Users, Loader2 } from 'lucide-react'
import type { Customer } from '../types/customer.types'

interface CustomerTableProps {
  customers: Customer[]
  isLoading?: boolean
  onResetFilters?: () => void
}

export const CustomerTable: React.FC<CustomerTableProps> = ({
  customers,
  isLoading,
  onResetFilters,
}) => {
  if (isLoading) {
    return (
      <div className="empty-state">
        <Loader2 size={36} className="spinner" style={{ color: '#4f46e5', margin: '0 auto 12px auto' }} />
        <p>Cargando listado de clientes...</p>
      </div>
    )
  }

  if (customers.length === 0) {
    return (
      <div className="empty-state">
        <div className="empty-state-icon">
          <Users size={28} />
        </div>
        <h4>No se encontraron clientes</h4>
        <p>No existen registros que coincidan con los criterios de búsqueda actuales.</p>
        {onResetFilters && (
          <button type="button" className="btn btn-secondary" onClick={onResetFilters}>
            Restablecer Filtros
          </button>
        )}
      </div>
    )
  }

  const formatCreationDate = (dateStr: string) => {
    try {
      const d = new Date(dateStr)
      return d.toLocaleDateString('es-PE', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      })
    } catch {
      return dateStr
    }
  }

  const formatBirthDate = (dateStr: string) => {
    try {
      const [year, month, day] = dateStr.split('-')
      if (year && month && day) {
        return `${day}/${month}/${year}`
      }
      return dateStr
    } catch {
      return dateStr
    }
  }

  return (
    <div className="table-responsive">
      <table className="modern-table">
        <thead>
          <tr>
            <th>DNI</th>
            <th>Cliente</th>
            <th>Email</th>
            <th>Fecha Nacimiento</th>
            <th>Edad</th>
            <th>Fecha Registro</th>
          </tr>
        </thead>
        <tbody>
          {customers.map((c) => {
            const initials = `${c.nombre.charAt(0)}${c.apellido.charAt(0)}`.toUpperCase()

            return (
              <tr key={c.id}>
                <td>
                  <span className="badge badge-slate mono-cell">{c.dni}</span>
                </td>
                <td>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <div
                      style={{
                        width: '32px',
                        height: '32px',
                        borderRadius: '50%',
                        backgroundColor: '#e0e7ff',
                        color: '#4338ca',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontWeight: 700,
                        fontSize: '12px',
                      }}
                    >
                      {initials}
                    </div>
                    <div>
                      <div style={{ fontWeight: 600 }}>
                        {c.nombre} {c.apellido}
                      </div>
                    </div>
                  </div>
                </td>
                <td style={{ color: '#475569' }}>{c.email}</td>
                <td>{formatBirthDate(c.fechaNacimiento)}</td>
                <td>
                  {c.edad !== undefined && (
                    <span className="badge badge-indigo">{c.edad} años</span>
                  )}
                </td>
                <td style={{ fontSize: '13px', color: '#64748b' }}>
                  {formatCreationDate(c.fechaCreacion)}
                </td>
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}
