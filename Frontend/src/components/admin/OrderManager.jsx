import { useEffect, useState } from 'react';
import { pedidoService } from '../../services/pedidoService';

// Debe coincidir con el enum EstadoPedido del backend.
const statuses = ['PENDIENTE', 'EN_PREPARACION', 'ENVIADO', 'ENTREGADO', 'CANCELADO'];

export default function OrderManager({ token }) {
  const [pedidos, setPedidos] = useState([]);
  const [error, setError] = useState('');
  const [boletaId, setBoletaId] = useState(null);
  const boleta = pedidos.find((pedido) => pedido.id === boletaId);
  const dinero = (valor) => `$${Number(valor || 0).toLocaleString('es-CL', { maximumFractionDigits: 2 })}`;

  useEffect(() => {
    pedidoService.getAll(token).then(setPedidos).catch((requestError) => setError(requestError.message));
  }, [token]);

  const cambiarEstado = async (id, estado) => {
    try { const updated = await pedidoService.updateStatus(id, estado, token); setPedidos((current) => current.map((item) => item.id === id ? updated : item)); }
    catch (requestError) { setError(requestError.message); }
  };

  return <section><h2 className="h4 text-success fw-bold">Pedidos</h2>{error && <div className="alert alert-danger" role="alert">{error}</div>}<div className="table-responsive"><table className="table table-hover"><thead><tr><th>ID</th><th>Dirección</th><th>Total</th><th>Estado</th><th>Detalle</th></tr></thead><tbody>{pedidos.map((item) => <tr key={item.id}><td>#{item.id}</td><td>{item.direccionEntrega}</td><td>{dinero(item.total)}</td><td><select className="form-select form-select-sm" aria-label={`Estado del pedido #${item.id}`} value={item.estado} onChange={(event) => cambiarEstado(item.id, event.target.value)}>{statuses.map((status) => <option key={status}>{status}</option>)}</select></td><td><button type="button" className="btn btn-outline-success btn-sm" aria-label={`Ver boleta #${item.id}`} onClick={() => setBoletaId(item.id)}>Ver boleta</button></td></tr>)}</tbody></table></div>
    {boleta && <section className="card p-4 mt-3" aria-label={`Boleta #${boleta.id}`}>
      <h3 className="h5">Boleta #{boleta.id}</h3><p><strong>Fecha:</strong> {boleta.creadoEn ? new Date(boleta.creadoEn).toLocaleString('es-CL') : 'Sin fecha'}</p><p><strong>Estado:</strong> {boleta.estado}</p><p><strong>Dirección:</strong> {boleta.direccionEntrega}</p>
      <div className="table-responsive"><table className="table"><thead><tr><th>Producto</th><th>Cantidad</th><th>Precio unitario</th><th>Subtotal</th></tr></thead><tbody>{(boleta.detalles || []).map((detalle) => <tr key={detalle.productoId}><td>{detalle.productoNombre}</td><td>{detalle.cantidad}</td><td>{dinero(detalle.precioUnitario)}</td><td>{dinero(detalle.subtotal)}</td></tr>)}</tbody></table></div>
      <dl><dt>Subtotal</dt><dd>{dinero(boleta.subtotal)}</dd><dt>Descuento</dt><dd>{dinero(boleta.descuento)}</dd>{boleta.cuponAplicado && <><dt>Cupón aplicado</dt><dd>{boleta.cuponAplicado}</dd></>}<dt>Total</dt><dd className="fw-bold">{dinero(boleta.total)}</dd></dl>
      <button type="button" className="btn btn-outline-secondary align-self-start" onClick={() => setBoletaId(null)}>Cerrar boleta</button>
    </section>}
  </section>;
}
