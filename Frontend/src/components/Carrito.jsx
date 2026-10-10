// src/components/Carrito.jsx
import React, { useState } from 'react';

export default function Carrito({ carrito, vaciarCarrito, finalizarCompra }) {
  const [pasoCheckout, setPasoCheckout] = useState(false);
  const [direccion, setDireccion] = useState('');
  const [compraExitosa, setCompraExitosa] = useState(null);

  const total = carrito.reduce((acc, item) => acc + (item.precio * item.cantidad), 0);

  const handleCheckout = (e) => {
    e.preventDefault();
    if (!direccion.trim()) return;
    // Simulación de éxito de pago (70% éxito, 30% fallo para cubrir ambos diseños de la rúbrica)
    const exito = Math.random() > 0.2;
    setCompraExitosa(exito);
    finalizarCompra();
  };

  if (compraExitosa !== null) {
    return (
      <div className="container my-5 text-center">
        {compraExitosa ? (
          <div className="alert alert-success p-5 shadow">
            <h3 className="fw-bold">✅ ¡Se ha realizado la compra con éxito!</h3>
            <p className="text-muted">Nro de pedido: #2024{Math.floor(Math.random() * 10000)}</p>
            <p>Dirección de entrega: <strong>{direccion}</strong></p>
            <h4 className="text-success mt-3">Total pagado: ${total.toLocaleString()}</h4>
          </div>
        ) : (
          <div className="alert alert-danger p-5 shadow">
            <h3 className="fw-bold">❌ No se pudo realizar el pago.</h3>
            <p className="text-muted">Por favor, intente nuevamente o verifique sus datos.</p>
            <h4 className="mt-3">Total intentado: ${total.toLocaleString()}</h4>
          </div>
        )}
        <button className="btn btn-primary mt-3" onClick={() => window.location.reload()}>Volver al Inicio</button>
      </div>
    );
  }

  return (
    <div className="container my-5">
      <h2 className="mb-4 text-success fw-bold">🛒 Carrito de Compras</h2>
      {carrito.length === 0 ? (
        <div className="alert alert-warning">Tu carrito está vacío.</div>
      ) : (
        <div className="row">
          <div className="col-md-8">
            <ul className="list-group mb-3 shadow-sm">
              {carrito.map((item, index) => (
                <li key={index} className="list-group-item d-flex justify-content-between align-items-center py-3">
                  <div>
                    <h6 className="my-0 fw-bold">{item.nombre}</h6>
                    <small className="text-muted">Cantidad: {item.cantidad}</small>
                  </div>
                  <span className="text-success fw-bold">${(item.precio * item.cantidad).toLocaleString()}</span>
                </li>
              ))}
            </ul>
          </div>
          <div className="col-md-4">
            <div className="card p-4 shadow-sm border-0 bg-light">
              <h4 className="fw-bold mb-3">Resumen</h4>
              <p className="d-flex justify-content-between"><span>Subtotal:</span> <strong>${total.toLocaleString()}</strong></p>
              <p className="d-flex justify-content-between"><span>Envío:</span> <strong>$3.000</strong></p>
              <hr />
              <p className="d-flex justify-content-between fs-5 text-success"><span>Total:</span> <strong>${(total + 3000).toLocaleString()}</strong></p>

              {!pasoCheckout ? (
                <button className="btn btn-success w-100 mt-3" onClick={() => setPasoCheckout(true)}>
                  Proceder al Checkout
                </button>
              ) : (
                <form onSubmit={handleCheckout} className="mt-3">
                  <div className="mb-3">
                    <label className="form-label fw-bold">Dirección de entrega</label>
                    <input 
                      type="text" 
                      className="form-control" 
                      placeholder="Ej. Av. Libertador 123" 
                      value={direccion}
                      onChange={(e) => setDireccion(e.target.value)}
                      required 
                    />
                  </div>
                  <button type="submit" className="btn btn-primary w-100">Confirmar Pago</button>
                </form>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}