import { describe, it, expect, vi, beforeEach } from 'vitest'
import { customerService, ApiError } from '../services/customerService'
import type { Customer, CustomerIndicators } from '../types/customer.types'

describe('customerService', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
  })

  it('getCustomers debe retornar la lista de clientes devuelta por el backend', async () => {
    const mockData: Customer[] = [
      {
        id: 1,
        nombre: 'Carlos',
        apellido: 'Santana',
        dni: '72849102',
        email: 'carlos@example.com',
        fechaCreacion: '2026-10-13T10:00:00',
        fechaNacimiento: '1994-07-15',
        edad: 32,
      },
    ]

    vi.spyOn(globalThis, 'fetch').mockResolvedValueOnce({
      ok: true,
      json: async () => mockData,
    } as Response)

    const result = await customerService.getCustomers()
    expect(result).toEqual(mockData)
    expect(globalThis.fetch).toHaveBeenCalled()
  })

  it('createCustomer debe enviar el payload y retornar el cliente creado', async () => {
    const payload = {
      nombre: 'Carlos',
      apellido: 'Santana',
      dni: '72849102',
      email: 'carlos@example.com',
      fechaNacimiento: '1994-07-15',
    }

    const mockResponse: Customer = {
      id: 1,
      ...payload,
      fechaCreacion: '2026-10-13T10:00:00',
      edad: 32,
    }

    vi.spyOn(globalThis, 'fetch').mockResolvedValueOnce({
      ok: true,
      json: async () => mockResponse,
    } as Response)

    const result = await customerService.createCustomer(payload)
    expect(result).toEqual(mockResponse)
  })

  it('getIndicators debe retornar el consolidado de indicadores demográficos', async () => {
    const mockIndicators: CustomerIndicators = {
      totalCustomers: 10,
      totalBirthPeriods: 6,
      birthsByMonthYear: [],
      highestBirthPeriod: null,
      lowestBirthPeriod: null,
      monthlyBirthRates: [],
    }

    vi.spyOn(globalThis, 'fetch').mockResolvedValueOnce({
      ok: true,
      json: async () => mockIndicators,
    } as Response)

    const result = await customerService.getIndicators()
    expect(result).toEqual(mockIndicators)
  })

  it('debe lanzar ApiError cuando la respuesta no es exitosa (HTTP 409)', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValueOnce({
      ok: false,
      status: 409,
      json: async () => ({
        message: 'El DNI ya se encuentra registrado',
        status: 409,
      }),
    } as Response)

    await expect(
      customerService.createCustomer({
        nombre: 'Pedro',
        apellido: 'Pascal',
        dni: '12345678',
        email: 'pedro@example.com',
        fechaNacimiento: '1980-04-02',
      })
    ).rejects.toThrow(ApiError)
  })
})
