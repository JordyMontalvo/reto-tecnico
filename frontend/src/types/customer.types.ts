export interface Customer {
  id: number
  nombre: string
  apellido: string
  email: string
  dni: string
  fechaCreacion: string
  fechaNacimiento: string
  edad?: number
}

export interface CustomerCreatePayload {
  nombre: string
  apellido: string
  email: string
  dni: string
  fechaNacimiento: string
}

export interface MonthYearStat {
  year: number
  month: number
  monthName: string
  period: string
  count: number
}

export interface PeriodSummary {
  year: number
  month: number
  monthName: string
  period: string
  count: number
  label: string
}

export interface MonthlyBirthRate {
  month: number
  monthName: string
  birthsCount: number
  ratePercentage: number
  formattedRate: string
}

export interface CustomerIndicators {
  totalCustomers: number
  totalBirthPeriods: number
  birthsByMonthYear: MonthYearStat[]
  highestBirthPeriod: PeriodSummary | null
  lowestBirthPeriod: PeriodSummary | null
  monthlyBirthRates: MonthlyBirthRate[]
}

export interface ValidationErrorDetail {
  field: string
  rejectedValue?: unknown
  reason: string
}

export interface ApiErrorResponse {
  timestamp?: string
  status?: number
  error?: string
  message: string
  path?: string
  validationErrors?: ValidationErrorDetail[]
}
