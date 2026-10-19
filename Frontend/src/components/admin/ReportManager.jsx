import { useEffect, useState } from 'react';
import { reporteService } from '../../services/reporteService';

export default function ReportManager({ token }) {
  const [data, setData] = useState({ resumen: null, stock: [], vendidos: [] });
  const [error, setError] = useState('');

  useEffect(() => {
    Promise.all([reporteService.resumen(token), reporteService.stockCritico(token), reporteService.productosMasVendidos(token)])
      .then(([resumen, stock, vendidos]) => setData({ resumen, stock, vendidos }))
      .catch((requestError) => setError(requestError.message));
  }, [token]);

  if (error) return <section><h2 className="h4 text-success fw-bold">Reportes</h2><div className="alert alert-danger">{error}</div></section>;
  if (!data.resumen) return <section><h2 className="h4 text-success fw-bold">Reportes</h2><p className="text-muted">Cargando indicadores...</p></section>;

  return <section><h2 className="h4 text-success fw-bold">Reportes</h2><div className="row g-3 mb-4">{[['Usuarios', data.resumen.totalUsuarios], ['Productos', data.resumen.totalProductos], ['Pedidos', data.resumen.totalPedidos]].map(([label, value]) => <div className="col-md-4" key={label}><div className="card border-0 shadow-sm p-3"><span className="text-muted">{label}</span><strong className="display-6 text-success">{value}</strong></div></div>)}</div><div className="row g-4"><div className="col-md-6"><div className="card border-0 shadow-sm p-4"><h3 className="h5">Stock crítico</h3><ul className="mb-0">{data.stock.map((item) => <li key={item.id}>{item.nombre}: {item.stock} unidad(es)</li>)}</ul></div></div><div className="col-md-6"><div className="card border-0 shadow-sm p-4"><h3 className="h5">Más vendidos</h3><ol className="mb-0">{data.vendidos.map((item) => <li key={item.productoId}>{item.nombre}: {item.unidadesVendidas} unidad(es)</li>)}</ol></div></div></div></section>;
}
