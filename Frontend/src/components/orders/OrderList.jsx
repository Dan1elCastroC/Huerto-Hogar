import { useState } from 'react';

const statusLabels = {
  PENDIENTE: 'Pendiente',
  CONFIRMADO: 'Confirmado',
  EN_PREPARACION: 'En preparación',
  ENVIADO: 'Enviado',
  ENTREGADO: 'Entregado',
  CANCELADO: 'Cancelado',
};

export default function OrderList({ pedidos, loading, error }) {
  const [pedidoAbierto, setPedidoAbierto] = useState(null);

  if (loading) return <p className="text-center text-muted">Cargando pedidos...</p>;
  if (error) return <div className="alert alert-warning" role="alert">{error}</div>;
  if (!pedidos.length) return <div className="alert alert-info" role="status">Todavía no tienes pedidos registrados.</div>;

  return (
    <div className="accordion" id="historial-pedidos">
      {pedidos.map((pedido) => (
        <div className="accordion-item" key={pedido.id}>
          <h2 className="accordion-header" id={`pedido-heading-${pedido.id}`}>
            <button
              className={`accordion-button ${pedidoAbierto === pedido.id ? '' : 'collapsed'}`}
              type="button"
              aria-expanded={pedidoAbierto === pedido.id}
              aria-controls={`pedido-${pedido.id}`}
              onClick={() => setPedidoAbierto((current) => (current === pedido.id ? null : pedido.id))}
            >
              <span className="me-3">Pedido #{pedido.id}</span>
              <span className="badge bg-secondary me-3">{statusLabels[pedido.estado] || pedido.estado}</span>
              <strong>${Number(pedido.total || 0).toLocaleString()}</strong>
            </button>
          </h2>
          <div id={`pedido-${pedido.id}`} className={`accordion-collapse collapse ${pedidoAbierto === pedido.id ? 'show' : ''}`} aria-labelledby={`pedido-heading-${pedido.id}`}>
            <div className="accordion-body">
              <p className="mb-2"><strong>Dirección:</strong> {pedido.direccionEntrega}</p>
              <ul className="mb-0">
                {(pedido.detalles || []).map((detalle) => (
                  <li key={`${pedido.id}-${detalle.productoId}`}>
                    {detalle.productoNombre} · {detalle.cantidad} unidad(es)
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}
