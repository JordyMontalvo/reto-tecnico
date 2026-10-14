import React, { useState } from 'react'
import { X, UserPlus, AlertCircle, CheckCircle2 } from 'lucide-react'
import type { CustomerCreatePayload } from '../types/customer.types'

interface CustomerFormModalProps {
  isOpen: boolean
  onClose: () => void
  onSubmit: (payload: CustomerCreatePayload) => Promise<void>
}

interface FormErrors {
  nombre?: string
  apellido?: string
  email?: string
  dni?: string
  fechaNacimiento?: string
}

export const CustomerFormModal: React.FC<CustomerFormModalProps> = ({
  isOpen,
  onClose,
  onSubmit,
}) => {
  const [formData, setFormData] = useState<CustomerCreatePayload>({
    nombre: '',
    apellido: '',
    email: '',
    dni: '',
    fechaNacimiento: '',
  })

  const [errors, setErrors] = useState<FormErrors>({})
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [serverError, setServerError] = useState<string | null>(null)

  if (!isOpen) return null

  const today = new Date().toISOString().split('T')[0]

  const validate = (): boolean => {
    const newErrors: FormErrors = {}

    // Nombre
    if (!formData.nombre.trim()) {
      newErrors.nombre = 'El nombre es obligatorio'
    } else if (formData.nombre.trim().length < 2) {
      newErrors.nombre = 'El nombre debe tener al menos 2 caracteres'
    }

    // Apellido
    if (!formData.apellido.trim()) {
      newErrors.apellido = 'El apellido es obligatorio'
    } else if (formData.apellido.trim().length < 2) {
      newErrors.apellido = 'El apellido debe tener al menos 2 caracteres'
    }

    // Email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!formData.email.trim()) {
      newErrors.email = 'El correo electrónico es obligatorio'
    } else if (!emailRegex.test(formData.email.trim())) {
      newErrors.email = 'Ingrese un formato de correo electrónico válido'
    }

    // DNI (exactamente 8 dígitos numéricos peruanos)
    if (!formData.dni.trim()) {
      newErrors.dni = 'El DNI es obligatorio'
    } else if (!/^\d{8}$/.test(formData.dni.trim())) {
      newErrors.dni = 'El DNI debe contener exactamente 8 dígitos numéricos'
    }

    // Fecha de Nacimiento
    if (!formData.fechaNacimiento) {
      newErrors.fechaNacimiento = 'La fecha de nacimiento es obligatoria'
    } else if (formData.fechaNacimiento >= today) {
      newErrors.fechaNacimiento = 'La fecha de nacimiento debe ser anterior a la fecha actual'
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setServerError(null)

    if (!validate()) {
      return
    }

    setIsSubmitting(true)
    try {
      await onSubmit({
        nombre: formData.nombre.trim(),
        apellido: formData.apellido.trim(),
        email: formData.email.trim().toLowerCase(),
        dni: formData.dni.trim(),
        fechaNacimiento: formData.fechaNacimiento,
      })
      // Reset form
      setFormData({
        nombre: '',
        apellido: '',
        email: '',
        dni: '',
        fechaNacimiento: '',
      })
      onClose()
    } catch (err: unknown) {
      if (err instanceof Error) {
        setServerError(err.message)
      } else {
        setServerError('Ocurrió un error inesperado al registrar el cliente')
      }
    } finally {
      setIsSubmitting(false)
    }
  }

  const calculateAgePreview = () => {
    if (!formData.fechaNacimiento) return null
    const birth = new Date(formData.fechaNacimiento)
    const now = new Date()
    let age = now.getFullYear() - birth.getFullYear()
    const m = now.getMonth() - birth.getMonth()
    if (m < 0 || (m === 0 && now.getDate() < birth.getDate())) {
      age--
    }
    return age >= 0 ? `${age} años` : null
  }

  const agePreview = calculateAgePreview()

  return (
    <div className="modal-backdrop" onClick={onClose} role="dialog" aria-modal="true">
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div className="brand-badge-logo" style={{ width: '36px', height: '36px' }}>
              <UserPlus size={18} />
            </div>
            <h3>Registrar Nuevo Cliente</h3>
          </div>
          <button
            type="button"
            className="btn btn-ghost"
            style={{ padding: '6px' }}
            onClick={onClose}
            aria-label="Cerrar modal"
          >
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} noValidate>
          <div className="modal-body">
            {serverError && (
              <div
                style={{
                  padding: '12px 16px',
                  backgroundColor: '#fff1f2',
                  border: '1px solid #fecdd3',
                  borderRadius: '8px',
                  marginBottom: '18px',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '10px',
                  color: '#e11d48',
                  fontSize: '13px',
                }}
              >
                <AlertCircle size={18} />
                <span>{serverError}</span>
              </div>
            )}

            <div className="form-grid">
              {/* Nombre */}
              <div className="form-group">
                <label htmlFor="customer-nombre">Nombre *</label>
                <input
                  id="customer-nombre"
                  type="text"
                  className={`form-control ${errors.nombre ? 'has-error' : ''}`}
                  placeholder="Ej. Carlos"
                  value={formData.nombre}
                  onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
                />
                {errors.nombre && <span className="form-error-msg">{errors.nombre}</span>}
              </div>

              {/* Apellido */}
              <div className="form-group">
                <label htmlFor="customer-apellido">Apellido *</label>
                <input
                  id="customer-apellido"
                  type="text"
                  className={`form-control ${errors.apellido ? 'has-error' : ''}`}
                  placeholder="Ej. Santana"
                  value={formData.apellido}
                  onChange={(e) => setFormData({ ...formData, apellido: e.target.value })}
                />
                {errors.apellido && <span className="form-error-msg">{errors.apellido}</span>}
              </div>

              {/* DNI */}
              <div className="form-group">
                <label htmlFor="customer-dni">
                  DNI (8 dígitos) *
                  {formData.dni.length === 8 && (
                    <span style={{ color: '#059669', marginLeft: '6px', fontSize: '11px' }}>
                      <CheckCircle2 size={12} style={{ display: 'inline', verticalAlign: 'middle' }} /> Válido
                    </span>
                  )}
                </label>
                <input
                  id="customer-dni"
                  type="text"
                  maxLength={8}
                  className={`form-control ${errors.dni ? 'has-error' : ''}`}
                  placeholder="72849102"
                  value={formData.dni}
                  onChange={(e) =>
                    setFormData({ ...formData, dni: e.target.value.replace(/\D/g, '') })
                  }
                />
                {errors.dni && <span className="form-error-msg">{errors.dni}</span>}
              </div>

              {/* Fecha de Nacimiento */}
              <div className="form-group">
                <label htmlFor="customer-fechaNacimiento">
                  Fecha de Nacimiento *
                  {agePreview && (
                    <span style={{ color: '#4f46e5', marginLeft: '6px', fontSize: '11px' }}>
                      ({agePreview})
                    </span>
                  )}
                </label>
                <input
                  id="customer-fechaNacimiento"
                  type="date"
                  max={today}
                  className={`form-control ${errors.fechaNacimiento ? 'has-error' : ''}`}
                  value={formData.fechaNacimiento}
                  onChange={(e) =>
                    setFormData({ ...formData, fechaNacimiento: e.target.value })
                  }
                />
                {errors.fechaNacimiento && (
                  <span className="form-error-msg">{errors.fechaNacimiento}</span>
                )}
              </div>

              {/* Email */}
              <div className="form-group col-full">
                <label htmlFor="customer-email">Correo Electrónico *</label>
                <input
                  id="customer-email"
                  type="email"
                  className={`form-control ${errors.email ? 'has-error' : ''}`}
                  placeholder="carlos.santana@example.com"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                />
                {errors.email && <span className="form-error-msg">{errors.email}</span>}
              </div>
            </div>
          </div>

          <div className="modal-footer">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={onClose}
              disabled={isSubmitting}
            >
              Cancelar
            </button>
            <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
              {isSubmitting ? 'Guardando...' : 'Guardar Cliente'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
