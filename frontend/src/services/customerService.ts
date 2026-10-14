import type {
  Customer,
  CustomerCreatePayload,
  CustomerIndicators,
  ApiErrorResponse,
} from '../types/customer.types'

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1/customers'

export class ApiError extends Error {
  status: number
  errorDetails?: ApiErrorResponse

  constructor(message: string, status: number, errorDetails?: ApiErrorResponse) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.errorDetails = errorDetails
  }
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    let errorData: ApiErrorResponse | undefined
    try {
      errorData = (await response.json()) as ApiErrorResponse
    } catch {
      // response wasn't JSON
    }

    const message =
      errorData?.message ||
      (errorData?.validationErrors && errorData.validationErrors.length > 0
        ? errorData.validationErrors.map((v) => `${v.field}: ${v.reason}`).join(', ')
        : `Error en la solicitud HTTP (${response.status})`)

    throw new ApiError(message, response.status, errorData)
  }

  return response.json() as Promise<T>
}

export const customerService = {
  async getCustomers(filters?: { dni?: string; email?: string }): Promise<Customer[]> {
    const url = new URL(API_BASE_URL)
    if (filters?.dni && filters.dni.trim() !== '') {
      url.searchParams.append('dni', filters.dni.trim())
    }
    if (filters?.email && filters.email.trim() !== '') {
      url.searchParams.append('email', filters.email.trim())
    }

    const res = await fetch(url.toString(), {
      method: 'GET',
      headers: {
        Accept: 'application/json',
      },
    })
    return handleResponse<Customer[]>(res)
  },

  async createCustomer(payload: CustomerCreatePayload): Promise<Customer> {
    const res = await fetch(API_BASE_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      body: JSON.stringify(payload),
    })
    return handleResponse<Customer>(res)
  },

  async getIndicators(): Promise<CustomerIndicators> {
    const res = await fetch(`${API_BASE_URL}/indicators`, {
      method: 'GET',
      headers: {
        Accept: 'application/json',
      },
    })
    return handleResponse<CustomerIndicators>(res)
  },
}
