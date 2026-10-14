import React, { useState } from 'react'
import { Search, RotateCcw, CreditCard, Mail } from 'lucide-react'

interface CustomerFiltersProps {
  onFilter: (filters: { dni?: string; email?: string }) => void
  onReset: () => void
  isLoading?: boolean
}

export const CustomerFilters: React.FC<CustomerFiltersProps> = ({
  onFilter,
  onReset,
  isLoading,
}) => {
  const [dni, setDni] = useState('')
  const [email, setEmail] = useState('')

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    onFilter({ dni: dni.trim() || undefined, email: email.trim() || undefined })
  }

  const handleClear = () => {
    setDni('')
    setEmail('')
    onReset()
  }

  const hasActiveFilters = Boolean(dni.trim() || email.trim())

  return (
    <form className="filters-bar" onSubmit={handleSearch}>
      {/* Filtro DNI */}
      <div className="filter-input-group">
        <CreditCard size={18} color="#94a3b8" />
        <input
          type="text"
          placeholder="Filtrar por DNI (ej. 70123456)..."
          value={dni}
          maxLength={8}
          onChange={(e) => setDni(e.target.value.replace(/\D/g, ''))}
          aria-label="Filtro por DNI"
        />
      </div>

      {/* Filtro Email */}
      <div className="filter-input-group">
        <Mail size={18} color="#94a3b8" />
        <input
          type="text"
          placeholder="Filtrar por correo electrónico..."
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          aria-label="Filtro por Email"
        />
      </div>

      {/* Botones de Acción */}
      <div style={{ display: 'flex', gap: '8px' }}>
        <button type="submit" className="btn btn-primary" disabled={isLoading}>
          <Search size={16} />
          {isLoading ? 'Buscando...' : 'Buscar'}
        </button>

        {hasActiveFilters && (
          <button
            type="button"
            className="btn btn-secondary"
            onClick={handleClear}
            disabled={isLoading}
          >
            <RotateCcw size={16} />
            Limpiar
          </button>
        )}
      </div>
    </form>
  )
}
