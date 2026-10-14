import React from 'react'
import { CheckCircle2, AlertTriangle, Info, X } from 'lucide-react'

export interface ToastMessage {
  id: string
  type: 'success' | 'error' | 'info'
  text: string
}

interface ToastProps {
  toasts: ToastMessage[]
  onDismiss: (id: string) => void
}

export const Toast: React.FC<ToastProps> = ({ toasts, onDismiss }) => {
  if (toasts.length === 0) return null

  return (
    <div className="toast-container">
      {toasts.map((t) => (
        <div key={t.id} className={`toast ${t.type}`}>
          {t.type === 'success' && <CheckCircle2 size={20} color="#059669" />}
          {t.type === 'error' && <AlertTriangle size={20} color="#e11d48" />}
          {t.type === 'info' && <Info size={20} color="#4f46e5" />}

          <span className="toast-msg">{t.text}</span>

          <button
            type="button"
            className="btn btn-ghost"
            style={{ padding: '2px' }}
            onClick={() => onDismiss(t.id)}
            aria-label="Cerrar notificación"
          >
            <X size={16} />
          </button>
        </div>
      ))}
    </div>
  )
}
