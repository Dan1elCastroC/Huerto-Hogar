import { useEffect, useState } from 'react';
import { cuponService } from '../../services/cuponService';

const initialForm = { codigo: '', porcentaje: '', fechaExpiracion: '' };

export default function CouponManager({ token }) {
  const [cupones, setCupones] = useState([]);
  const [form, setForm] = useState(initialForm);
  const [estado, setEstado] = useState(null);

  useEffect(() => { cuponService.getAll(token).then(setCupones).catch((error) => setEstado({ tipo: 'danger', texto: error.message })); }, [token]);

  const guardar = async (event) => {
    event.preventDefault();
    try {
      const creado = await cuponService.create({ ...form, porcentaje: Number(form.porcentaje), fechaExpiracion: form.fechaExpiracion || null }, token);
      setCupones((current) => [...current, creado]);
      setForm(initialForm);
      setEstado({ tipo: 'success', texto: 'Cupón creado correctamente.' });
    } catch (error) { setEstado({ tipo: 'danger', texto: error.message }); }
  };

  const eliminar = async (id) => {
    if (!window.confirm('¿Desactivar este cupón?')) return;
    try { await cuponService.delete(id, token); setCupones((current) => current.filter((item) => item.id !== id)); }
    catch (error) { setEstado({ tipo: 'danger', texto: error.message }); }
  };

  return (
    <section>
      <h2 className="h4 text-success fw-bold">Cupones</h2>
      {estado && <div className={`alert alert-${estado.tipo}`}>{estado.texto}</div>}
      <form onSubmit={guardar} className="card border-0 shadow-sm p-4 mb-4"><div className="row g-3">
        <div className="col-md-3"><label className="form-label" htmlFor="cupon-codigo">Código</label><input id="cupon-codigo" className="form-control" value={form.codigo} onChange={(event) => setForm({ ...form, codigo: event.target.value.toUpperCase() })} required /></div>
        <div className="col-md-3"><label className="form-label" htmlFor="cupon-porcentaje">Descuento (%)</label><input id="cupon-porcentaje" type="number" min="1" max="100" className="form-control" value={form.porcentaje} onChange={(event) => setForm({ ...form, porcentaje: event.target.value })} required /></div>
        <div className="col-md-4"><label className="form-label" htmlFor="cupon-fecha">Expiración</label><input id="cupon-fecha" type="date" className="form-control" value={form.fechaExpiracion} onChange={(event) => setForm({ ...form, fechaExpiracion: event.target.value })} /></div>
        <div className="col-md-2 d-flex align-items-end"><button className="btn btn-success w-100" type="submit">Crear</button></div>
      </div></form>
      <div className="table-responsive"><table className="table table-hover"><thead><tr><th>Código</th><th>Descuento</th><th>Expiración</th><th /></tr></thead><tbody>{cupones.map((item) => <tr key={item.id}><td>{item.codigo}</td><td>{item.porcentaje}%</td><td>{item.fechaExpiracion || 'Sin vencimiento'}</td><td><button className="btn btn-sm btn-outline-danger" onClick={() => eliminar(item.id)}>Desactivar</button></td></tr>)}</tbody></table></div>
    </section>
  );
}
