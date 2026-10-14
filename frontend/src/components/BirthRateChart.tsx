import React, { useState } from 'react'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler,
} from 'chart.js'
import { Bar } from 'react-chartjs-2'
import type { CustomerIndicators } from '../types/customer.types'

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
)

interface BirthRateChartProps {
  indicators: CustomerIndicators | null
}

export const BirthRateChart: React.FC<BirthRateChartProps> = ({ indicators }) => {
  const [activeView, setActiveView] = useState<'monthlyRate' | 'periodCounts'>('monthlyRate')

  if (!indicators || indicators.totalCustomers === 0) {
    return (
      <div className="empty-state">
        <p>No hay suficientes clientes registrados para generar los gráficos demográficos.</p>
      </div>
    )
  }

  // Data for View 1: Tasa de Natalidad por Mes (Enero a Diciembre)
  const monthlyRates = indicators.monthlyBirthRates || []
  const monthlyLabels = monthlyRates.map((r) => r.monthName)
  const monthlyPercentages = monthlyRates.map((r) => r.ratePercentage)
  const monthlyCounts = monthlyRates.map((r) => r.birthsCount)

  const monthlyChartData = {
    labels: monthlyLabels,
    datasets: [
      {
        label: 'Tasa de Natalidad (%)',
        data: monthlyPercentages,
        backgroundColor: 'rgba(79, 70, 229, 0.75)',
        hoverBackgroundColor: 'rgba(79, 70, 229, 0.95)',
        borderRadius: 8,
        borderSkipped: false,
      },
    ],
  }

  // Data for View 2: Nacimientos por Mes/Año
  const periodStats = indicators.birthsByMonthYear || []
  const periodLabels = periodStats.map((p) => `${p.monthName.substring(0, 3)} ${p.year}`)
  const periodCounts = periodStats.map((p) => p.count)

  const periodChartData = {
    labels: periodLabels,
    datasets: [
      {
        label: 'Cantidad de Clientes Nacidos',
        data: periodCounts,
        backgroundColor: 'rgba(16, 185, 129, 0.75)',
        hoverBackgroundColor: 'rgba(16, 185, 129, 0.95)',
        borderRadius: 8,
        borderSkipped: false,
      },
    ],
  }

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false,
      },
      tooltip: {
        backgroundColor: '#0f172a',
        titleFont: { family: "'Plus Jakarta Sans', sans-serif", size: 13 },
        bodyFont: { family: "'Plus Jakarta Sans', sans-serif", size: 12 },
        padding: 12,
        cornerRadius: 8,
        callbacks: {
          label: (context: { raw: unknown; dataIndex: number }) => {
            if (activeView === 'monthlyRate') {
              const count = monthlyCounts[context.dataIndex]
              return `Tasa: ${context.raw}% (${count} nacidos)`
            }
            return `Nacidos: ${context.raw} cliente(s)`
          },
        },
      },
    },
    scales: {
      x: {
        grid: {
          display: false,
        },
        ticks: {
          font: { family: "'Plus Jakarta Sans', sans-serif", size: 12 },
          color: '#64748b',
        },
      },
      y: {
        beginAtZero: true,
        grid: {
          color: '#f1f5f9',
        },
        ticks: {
          font: { family: "'Plus Jakarta Sans', sans-serif", size: 12 },
          color: '#64748b',
          callback: (value: string | number) =>
            activeView === 'monthlyRate' ? `${value}%` : value,
        },
      },
    },
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: '16px' }}>
        <div className="tab-pills">
          <button
            type="button"
            className={`tab-pill-btn ${activeView === 'monthlyRate' ? 'active' : ''}`}
            onClick={() => setActiveView('monthlyRate')}
          >
            Tasa de Natalidad Mensual (%)
          </button>
          <button
            type="button"
            className={`tab-pill-btn ${activeView === 'periodCounts' ? 'active' : ''}`}
            onClick={() => setActiveView('periodCounts')}
          >
            Nacimientos por Mes / Año
          </button>
        </div>
      </div>

      <div className="chart-box">
        <Bar
          data={activeView === 'monthlyRate' ? monthlyChartData : periodChartData}
          options={options}
        />
      </div>
    </div>
  )
}
