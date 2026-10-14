import React from 'react'
import { Users, TrendingUp, TrendingDown, Calendar } from 'lucide-react'
import type { CustomerIndicators } from '../types/customer.types'

interface KpiCardsProps {
  indicators: CustomerIndicators | null
  isLoading?: boolean
}

export const KpiCards: React.FC<KpiCardsProps> = ({ indicators, isLoading }) => {
  const total = indicators?.totalCustomers ?? 0
  const highest = indicators?.highestBirthPeriod
  const lowest = indicators?.lowestBirthPeriod
  const totalPeriods = indicators?.totalBirthPeriods ?? 0

  return (
    <div className="kpi-grid">
      {/* Total Clientes */}
      <div className="kpi-card indigo">
        <div className="kpi-top">
          <span className="kpi-label">Total Clientes</span>
          <div className="kpi-icon-wrap indigo">
            <Users size={20} />
          </div>
        </div>
        <div className="kpi-value">{isLoading ? '...' : total}</div>
        <div className="kpi-subtext">Clientes activos registrados en la base de datos</div>
      </div>

      {/* Mes/Año Mayor Natalidad */}
      <div className="kpi-card emerald">
        <div className="kpi-top">
          <span className="kpi-label">Pico de Natalidad</span>
          <div className="kpi-icon-wrap emerald">
            <TrendingUp size={20} />
          </div>
        </div>
        <div className="kpi-value">
          {isLoading ? '...' : highest ? `${highest.count} nacidos` : 'N/A'}
        </div>
        <div className="kpi-subtext">
          {highest ? (
            <span className="badge badge-emerald">
              {highest.monthName} {highest.year}
            </span>
          ) : (
            'Sin registros suficientes'
          )}
        </div>
      </div>

      {/* Mes/Año Menor Natalidad */}
      <div className="kpi-card amber">
        <div className="kpi-top">
          <span className="kpi-label">Valle de Natalidad</span>
          <div className="kpi-icon-wrap amber">
            <TrendingDown size={20} />
          </div>
        </div>
        <div className="kpi-value">
          {isLoading ? '...' : lowest ? `${lowest.count} nacidos` : 'N/A'}
        </div>
        <div className="kpi-subtext">
          {lowest ? (
            <span className="badge badge-amber">
              {lowest.monthName} {lowest.year}
            </span>
          ) : (
            'Sin registros suficientes'
          )}
        </div>
      </div>

      {/* Períodos Registrados */}
      <div className="kpi-card violet">
        <div className="kpi-top">
          <span className="kpi-label">Períodos Evaluados</span>
          <div className="kpi-icon-wrap violet">
            <Calendar size={20} />
          </div>
        </div>
        <div className="kpi-value">{isLoading ? '...' : `${totalPeriods} períodos`}</div>
        <div className="kpi-subtext">Cohortes únicas de nacimiento identificadas</div>
      </div>
    </div>
  )
}
