import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { describe, it, expect, vi } from 'vitest'
import { CustomerFormModal } from '../components/CustomerFormModal'

describe('CustomerFormModal Component', () => {
  it('no debe renderizar nada cuando isOpen es false', () => {
    const { container } = render(
      <CustomerFormModal isOpen={false} onClose={vi.fn()} onSubmit={vi.fn()} />
    )
    expect(container).toBeEmptyDOMElement()
  })

  it('debe mostrar errores de validación si los campos obligatorios están vacíos al intentar enviar', async () => {
    const handleSubmit = vi.fn()
    render(
      <CustomerFormModal isOpen={true} onClose={vi.fn()} onSubmit={handleSubmit} />
    )

    const submitBtn = screen.getByRole('button', { name: /guardar cliente/i })
    fireEvent.click(submitBtn)

    expect(await screen.findByText(/el nombre es obligatorio/i)).toBeInTheDocument()
    expect(screen.getByText(/el apellido es obligatorio/i)).toBeInTheDocument()
    expect(screen.getByText(/el dni es obligatorio/i)).toBeInTheDocument()
    expect(screen.getByText(/el correo electrónico es obligatorio/i)).toBeInTheDocument()
    expect(screen.getByText(/la fecha de nacimiento es obligatoria/i)).toBeInTheDocument()
    expect(handleSubmit).not.toHaveBeenCalled()
  })

  it('debe validar que el DNI tenga exactamente 8 dígitos', async () => {
    render(
      <CustomerFormModal isOpen={true} onClose={vi.fn()} onSubmit={vi.fn()} />
    )

    const dniInput = screen.getByPlaceholderText('72849102')
    fireEvent.change(dniInput, { target: { value: '123' } })

    const submitBtn = screen.getByRole('button', { name: /guardar cliente/i })
    fireEvent.click(submitBtn)

    expect(
      await screen.findByText(/el dni debe contener exactamente 8 dígitos numéricos/i)
    ).toBeInTheDocument()
  })

  it('debe llamar a onSubmit con los datos correctos cuando el formulario es válido', async () => {
    const handleSubmit = vi.fn().mockResolvedValue(undefined)
    const handleClose = vi.fn()

    render(
      <CustomerFormModal isOpen={true} onClose={handleClose} onSubmit={handleSubmit} />
    )

    fireEvent.change(screen.getByPlaceholderText(/ej\. carlos/i), {
      target: { value: 'Carlos' },
    })
    fireEvent.change(screen.getByPlaceholderText(/ej\. santana/i), {
      target: { value: 'Santana' },
    })
    fireEvent.change(screen.getByPlaceholderText('72849102'), {
      target: { value: '72849102' },
    })
    fireEvent.change(screen.getByPlaceholderText(/carlos\.santana@example\.com/i), {
      target: { value: 'carlos.santana@example.com' },
    })

    const dateInput = screen.getByLabelText(/fecha de nacimiento/i)
    fireEvent.change(dateInput, { target: { value: '1994-07-15' } })

    const submitBtn = screen.getByRole('button', { name: /guardar cliente/i })
    fireEvent.click(submitBtn)

    await waitFor(() => {
      expect(handleSubmit).toHaveBeenCalledWith({
        nombre: 'Carlos',
        apellido: 'Santana',
        dni: '72849102',
        email: 'carlos.santana@example.com',
        fechaNacimiento: '1994-07-15',
      })
      expect(handleClose).toHaveBeenCalled()
    })
  })
})
