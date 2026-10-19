import { useEffect, useState } from 'react';
import { pedidoService } from '../../services/pedidoService';

const statuses = ['PENDIENTE', 'CONFIRMADO', 'EN_PREPARACION', 'ENVIADO', 'ENTREGADO', 'CANCELADO'];

export default function OrderManager({ token }) {
  const [pedidos, setPedidos] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    pedidoService.getAll(token).then(setPedidos).catch((requestError) => setError(requestError.message));
  }, [token]);

  const cambiarEstado = async (id, estado) => {
    try { const updated = await pedidoService.updateStatus(id, estado, token); setPedidos((current) => current.map((item) => item.id === id ? updated : item)); }
    catch (requestError) { setError(requestError.message); }
  };

  return <section><h2 className="h4 text-success fw-bold">Pedidos</h2>{error && <div className="alert alert-danger">{error}</div>}<div className="table-responsive"><table className="table table-hover"><thead><tr><th>ID</th><th>Dirección</th><th>Total</th><th>Estado</th></tr></thead><tbody>{pedidos.map((item) => <tr key={item.id}><td>#{item.id}</td><td>{item.direccionEntrega}</td><td>${Number(item.total || 0).toLocaleString()}</td><td><select className="form-select form-select-sm" value={item.estado} onChange={(event) => cambiarEstado(item.id, event.target.value)}>{statuses.map((status) => <option key={status}>{status}</option>)}</select></td></tr>)}</tbody></table></div></section>;
}
