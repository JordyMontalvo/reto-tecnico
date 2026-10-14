import React, { useState } from 'react'
import { BarChart3, Table as TableIcon } from 'lucide-react'
import { KpiCards } from './KpiCards'
import { BirthRateChart } from './BirthRateChart'
import type { CustomerIndicators } from '../types/customer.types'

interface IndicatorsDashboardProps {
  indicators: CustomerIndicators | null
  isLoading?: boolean
}

export const IndicatorsDashboard: React.FC<IndicatorsDashboardProps> = ({
  indicators,
  isLoading,
}) => {
  const [displayMode, setDisplayMode] = useState<'chart' | 'table'>('chart')

  return (
    <div className="section-wrapper">
      <div className="section-header">
        <div className="section-title-wrap">
          <div className="brand-badge-logo" style={{ width: '36px', height: '36px' }}>
            <BarChart3 size={18} />
          </div>
          <div>
            <h2 className="section-title">Indicadores Demográficos & Tasa de Natalidad</h2>
            <p className="section-desc">
              Distribución mensual y períodos pico de nacimientos de los clientes registrados
            </p>
          </div>
        </div>

        <div className="tab-pills">
          <button
            type="button"
            className={`tab-pill-btn ${displayMode === 'chart' ? 'active' : ''}`}
            onClick={() => setDisplayMode('chart')}
          >
            <BarChart3 size={14} style={{ display: 'inline', verticalAlign: 'middle', marginRight: '4px' }} />
            Gráficos
          </button>
          <button
            type="button"
            className={`tab-pill-btn ${displayMode === 'table' ? 'active' : ''}`}
            onClick={() => setDisplayMode('table')}
          >
            <TableIcon size={14} style={{ display: 'inline', verticalAlign: 'middle', marginRight: '4px' }} />
            Tabla Resumen
          </button>
        </div>
      </div>

      <KpiCards indicators={indicators} isLoading={isLoading} />

      {displayMode === 'chart' ? (
        <BirthRateChart indicators={indicators} />
      ) : (
        <div className="table-responsive">
          <table className="modern-table">
            <thead>
              <tr>
                <th>Mes</th>
                <th>Clientes Nacidos</th>
                <th>Tasa de Natalidad (%)</th>
                <th>Distribución Relativa</th>
              </tr>
            </thead>
            <tbody>
              {indicators?.monthlyBirthRates.map((item) => (
                <tr key={item.month}>
                  <td style={{ fontWeight: 600 }}>{item.monthName}</td>
                  <td>
                    <span className="badge badge-indigo">{item.birthsCount} cliente(s)</span>
                  </td>
                  <td style={{ fontWeight: 700, color: '#4f46e5' }}>
                    {item.formattedRate}
                  </td>
                  <td>
                    <div className="progress-bar-bg">
                      <div
                        className="progress-bar-fill"
                        style={{ width: `${Math.min(item.ratePercentage * 2.5, 100)}%` }}
                      />
                    </div>
                    <span style={{ fontSize: '12px', color: '#64748b' }}>
                      {item.formattedRate}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
