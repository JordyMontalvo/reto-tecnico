import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import { CustomerTable } from '../components/CustomerTable'
import type { Customer } from '../types/customer.types'

describe('CustomerTable Component', () => {
  const mockCustomers: Customer[] = [
    {
      id: 1,
      nombre: 'Juan',
      apellido: 'Pérez',
      dni: '12345678',
      email: 'juan@example.com',
      fechaNacimiento: '1995-05-10',
      fechaCreacion: '2026-10-10T10:00:00',
      edad: 31,
    },
    {
      id: 2,
      nombre: 'María',
      apellido: 'López',
      dni: '87654321',
      email: 'maria@example.com',
      fechaNacimiento: '1998-11-20',
      fechaCreacion: '2026-10-11T12:00:00',
      edad: 27,
    },
  ]

  it('debe renderizar el spinner cuando isLoading es true', () => {
    render(<CustomerTable customers={[]} isLoading={true} />)
    expect(screen.getByText(/cargando listado de clientes/i)).toBeInTheDocument()
  })

  it('debe mostrar el estado vacío si la lista no contiene clientes', () => {
    render(<CustomerTable customers={[]} isLoading={false} />)
    expect(screen.getByText(/no se encontraron clientes/i)).toBeInTheDocument()
  })

  it('debe renderizar las filas de clientes con sus datos correctamente', () => {
    render(<CustomerTable customers={mockCustomers} isLoading={false} />)

    expect(screen.getByText('12345678')).toBeInTheDocument()
    expect(screen.getByText('Juan Pérez')).toBeInTheDocument()
    expect(screen.getByText('juan@example.com')).toBeInTheDocument()
    expect(screen.getByText('31 años')).toBeInTheDocument()

    expect(screen.getByText('87654321')).toBeInTheDocument()
    expect(screen.getByText('María López')).toBeInTheDocument()
    expect(screen.getByText('27 años')).toBeInTheDocument()
  })
})
