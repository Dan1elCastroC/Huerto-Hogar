import React, { useEffect, useState } from 'react';
import { getCartTotal } from '../services/cartUtils';
import { cuponService } from '../services/cuponService';

export default function Carrito({
  carrito,
  user,
  cambiarVista,
  vaciarCarrito,
  cambiarCantidad,
  eliminarDelCarrito,
  finalizarCompra,
  sincronizandoCarrito,
}) {
  const [pasoCheckout, setPasoCheckout] = useState(false);
  const [direccion, setDireccion] = useState(user?.direccion || '');
  const [modalidadEntrega, setModalidadEntrega] = useState('proxima');
  const [fechaEntrega, setFechaEntrega] = useState('');
  const [codigoCupon, setCodigoCupon] = useState('');
  const [cuponAplicado, setCuponAplicado] = useState(null);
  const [errorCupon, setErrorCupon] = useState('');
  const [validandoCupon, setValidandoCupon] = useState(false);
  const [resultado, setResultado] = useState(null);
  const [error, setError] = useState('');
  const [procesando, setProcesando] = useState(false);
  const subtotal = getCartTotal(carrito);
  const subtotalCentavos = Math.round(subtotal * 100);
  const porcentajeCentesimas = Math.round(Number(cuponAplicado?.porcentaje || 0) * 100);
  const descuento = Math.round(subtotalCentavos * porcentajeCentesimas / 10000) / 100;
  const total = (subtotalCentavos - Math.round(descuento * 100)) / 100;
  const hoy = new Date();
  const fechaMinima = `${hoy.getFullYear()}-${String(hoy.getMonth() + 1).padStart(2, '0')}-${String(hoy.getDate()).padStart(2, '0')}`;

  useEffect(() => {
    if (user?.direccion) setDireccion(user.direccion);
  }, [user]);

  const aplicarCupon = async () => {
    setErrorCupon('');
    setCuponAplicado(null);
    setValidandoCupon(true);
    try {
      const cupon = await cuponService.validate(codigoCupon.trim());
      setCuponAplicado(cupon);
      setCodigoCupon(cupon.codigo);
    } catch (requestError) {
      setErrorCupon(requestError.message || 'No se pudo validar el cupón.');
    } finally {
      setValidandoCupon(false);
    }
  };

  const quitarCupon = () => {
    setCodigoCupon('');
    setCuponAplicado(null);
    setErrorCupon('');
    setError('');
    setResultado(null);
  };

  const handleCheckout = async (event) => {
    event.preventDefault();
    setError('');

    if (!user) {
      setError('Debes iniciar sesión para confirmar la compra. Tu carrito se conservará.');
      return;
    }

    if (modalidadEntrega === 'programada' && (!fechaEntrega || fechaEntrega < fechaMinima)) {
      setError('Selecciona una fecha de entrega desde hoy.');
      return;
    }

    setProcesando(true);
    try {
      const pedido = await finalizarCompra({
        direccionEntrega: direccion.trim(),
        fechaEntregaDeseada: modalidadEntrega === 'programada' ? fechaEntrega : undefined,
        codigoCupon: cuponAplicado?.codigo || undefined,
      });
      setResultado({ exito: true, pedido });
    } catch (requestError) {
      setResultado({ exito: false });
      setError(requestError.message || 'No se pudo procesar la compra.');
    } finally {
      setProcesando(false);
    }
  };

  const handleVaciarCarrito = () => {
    if (window.confirm('¿Seguro que deseas vaciar todo el carrito?')) vaciarCarrito();
  };

  if (resultado?.exito) {
    const { pedido } = resultado;
    return (
      <div className="container my-5">
        <div className="alert alert-success p-5 shadow-sm text-center">
          <div className="display-5 mb-3">✅</div>
          <h2 className="fw-bold">¡Compra realizada con éxito!</h2>
          <p className="mb-1">Número de pedido: <strong>#{pedido.id}</strong></p>
          <p className="mb-1">Dirección de entrega: <strong>{pedido.direccionEntrega}</strong></p>
          <p className="fs-4 text-success fw-bold mt-3 mb-0">Total pagado: ${Number(pedido.total).toLocaleString()}</p>
        </div>
        <div className="text-center">
          <button type="button" className="btn btn-success" onClick={() => cambiarVista('catalogo')}>Seguir comprando</button>
        </div>
      </div>
    );
  }

  if (carrito.length === 0) {
    return (
      <div className="container my-5 text-center">
        <div className="alert alert-warning shadow-sm">
          <h2 className="h4 fw-bold">Tu carrito está vacío</h2>
          <p>Agrega productos del catálogo para comenzar tu compra.</p>
          <button type="button" className="btn btn-success" onClick={() => cambiarVista('catalogo')}>Ver productos</button>
        </div>
      </div>
    );
  }

  return (
    <div className="container my-5 carrito-page">
      <div className="d-flex justify-content-between align-items-center mb-4 gap-3 flex-wrap">
        <h2 className="text-success fw-bold mb-0">🛒 Carrito de compras</h2>
        <button type="button" className="btn btn-outline-danger btn-sm" onClick={handleVaciarCarrito}>Vaciar carrito</button>
      </div>

      <div className="row g-4">
        <div className="col-lg-8">
          <div className="list-group shadow-sm">
            {carrito.map((item) => (
              <div key={item.id} className="list-group-item py-3">
                <div className="d-flex align-items-center gap-3 carrito-item">
                  <img src={item.imagenUrl || item.imagen || 'https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=120'} alt={item.nombre} className="rounded" style={{ width: 72, height: 72, objectFit: 'cover' }} />
                  <div className="flex-grow-1 carrito-item-info">
                    <h3 className="h6 fw-bold mb-1">{item.nombre}</h3>
                    <small className="text-muted">${Number(item.precio).toLocaleString()} por unidad</small>
                  </div>
                  <div className="d-flex align-items-center gap-2 carrito-item-cantidad">
                    <button type="button" className="btn btn-outline-secondary btn-sm" aria-label={`Disminuir cantidad de ${item.nombre}`} onClick={() => cambiarCantidad(item.id, item.cantidad - 1)}>−</button>
                    <span className="fw-bold" aria-label="Cantidad">{item.cantidad}</span>
                    <button type="button" className="btn btn-outline-secondary btn-sm" aria-label={`Aumentar cantidad de ${item.nombre}`} disabled={item.cantidad >= item.stock} onClick={() => cambiarCantidad(item.id, item.cantidad + 1)}>+</button>
                  </div>
                  <strong className="text-success text-nowrap">${(Number(item.precio) * item.cantidad).toLocaleString()}</strong>
                  <button type="button" className="btn btn-outline-danger btn-sm" aria-label={`Eliminar ${item.nombre}`} onClick={() => eliminarDelCarrito(item.id)}>✕</button>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="col-lg-4">
          <div className="card p-4 shadow-sm border-0 bg-light carrito-checkout">
            <h3 className="h4 fw-bold mb-3">Resumen de compra</h3>
            <p className="d-flex justify-content-between"><span>Subtotal</span><strong>${subtotal.toLocaleString()}</strong></p>
            {cuponAplicado && <p className="d-flex justify-content-between text-success"><span>Descuento ({cuponAplicado.codigo})</span><strong>-${descuento.toLocaleString()}</strong></p>}
            <p className="d-flex justify-content-between"><span>Envío</span><strong>Gratis</strong></p>
            <hr />
            <p className="d-flex justify-content-between fs-5 text-success"><span>Total</span><strong>${total.toLocaleString()}</strong></p>

            {!pasoCheckout ? (
              <button type="button" className="btn btn-success w-100 mt-3" onClick={() => setPasoCheckout(true)}>Proceder al checkout</button>
            ) : (
              <form onSubmit={handleCheckout} className="mt-3">
                {resultado && !resultado.exito && <div className="alert alert-danger small" role="alert"><strong>No se pudo realizar el pago.</strong> Revisa los datos e inténtalo nuevamente.</div>}
                {!user && <div className="alert alert-info small">Inicia sesión para usar tus datos y confirmar el pedido.</div>}
                {error && <div className="alert alert-danger small" role="alert">{error}</div>}
                {user && <div className="small mb-3">
                  <p className="mb-1">Cliente: <strong>{[user.nombre, user.apellidos].filter(Boolean).join(' ').trim() || user.correo}</strong></p>
                  {user.correo && <p className="mb-0"><span>Correo: </span><span>{user.correo}</span></p>}
                </div>}
                <div className="mb-3">
                  <label htmlFor="direccion-entrega" className="form-label fw-bold">Dirección de entrega</label>
                  <input id="direccion-entrega" type="text" className="form-control" placeholder="Ej. Av. Libertador 123" value={direccion} onChange={(event) => setDireccion(event.target.value)} required />
                </div>
                <div className="mb-3">
                  <label htmlFor="modalidad-entrega" className="form-label fw-bold">Modalidad de entrega</label>
                  <select id="modalidad-entrega" className="form-select" value={modalidadEntrega} onChange={(event) => { setModalidadEntrega(event.target.value); if (event.target.value === 'proxima') setFechaEntrega(''); }}>
                    <option value="proxima">Próxima disponibilidad</option>
                    <option value="programada">Programar entrega</option>
                  </select>
                </div>
                {modalidadEntrega === 'programada' && <div className="mb-3">
                  <label htmlFor="fecha-entrega" className="form-label fw-bold">Fecha deseada</label>
                  <input id="fecha-entrega" type="date" className="form-control" value={fechaEntrega} onChange={(event) => setFechaEntrega(event.target.value)} min={fechaMinima} required />
                </div>}
                <div className="mb-3">
                  <label htmlFor="codigo-cupon" className="form-label fw-bold">Cupón <span className="text-muted fw-normal">(opcional)</span></label>
                  <input id="codigo-cupon" type="text" className="form-control" value={codigoCupon} onChange={(event) => { setCodigoCupon(event.target.value); setCuponAplicado(null); setErrorCupon(''); }} placeholder="Código de descuento" disabled={validandoCupon || procesando} aria-invalid={Boolean(errorCupon)} aria-describedby={errorCupon ? 'error-cupon' : undefined} />
                  <div className="d-flex gap-2 flex-wrap mt-2">
                    <button type="button" className="btn btn-outline-success btn-sm" onClick={aplicarCupon} disabled={!codigoCupon.trim() || validandoCupon || procesando}>{validandoCupon ? 'Validando cupón...' : 'Aplicar cupón'}</button>
                    {(cuponAplicado || codigoCupon) && <button type="button" className="btn btn-outline-secondary btn-sm" onClick={quitarCupon} disabled={validandoCupon || procesando}>Quitar cupón</button>}
                  </div>
                  {errorCupon && <div id="error-cupon" className="alert alert-danger small mt-2" role="alert">{errorCupon}</div>}
                </div>
                {user ? (
                  <button type="submit" className="btn btn-primary w-100" disabled={procesando || sincronizandoCarrito || validandoCupon}>{sincronizandoCarrito ? 'Sincronizando carrito...' : procesando ? 'Procesando...' : 'Confirmar compra'}</button>
                ) : (
                  <button type="button" className="btn btn-primary w-100" onClick={() => cambiarVista('login')}>Iniciar sesión</button>
                )}
              </form>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
