import { useEffect, useState } from 'react';
import { categoriaService } from '../../services/categoriaService';

const initialForm = { nombre: '', descripcion: '' };

export default function CategoryManager({ token }) {
  const [categorias, setCategorias] = useState([]);
  const [form, setForm] = useState(initialForm);
  const [estado, setEstado] = useState(null);

  const cargar = () => categoriaService.getAll().then(setCategorias).catch((error) => setEstado({ tipo: 'danger', texto: error.message }));
  useEffect(() => { cargar(); }, []);

  const guardar = async (event) => {
    event.preventDefault();
    try {
      const creada = await categoriaService.create(form, token);
      setCategorias((current) => [...current, creada]);
      setForm(initialForm);
      setEstado({ tipo: 'success', texto: 'Categoría creada correctamente.' });
    } catch (error) { setEstado({ tipo: 'danger', texto: error.message }); }
  };

  const eliminar = async (id) => {
    if (!window.confirm('¿Eliminar esta categoría?')) return;
    try {
      await categoriaService.delete(id, token);
      setCategorias((current) => current.filter((item) => item.id !== id));
    } catch (error) { setEstado({ tipo: 'danger', texto: error.message }); }
  };

  return (
    <section>
      <h2 className="h4 text-success fw-bold">Categorías</h2>
      {estado && <div className={`alert alert-${estado.tipo}`}>{estado.texto}</div>}
      <form onSubmit={guardar} className="card border-0 shadow-sm p-4 mb-4">
        <div className="row g-3">
          <div className="col-md-4"><label className="form-label" htmlFor="categoria-nombre">Nombre</label><input id="categoria-nombre" className="form-control" value={form.nombre} onChange={(event) => setForm({ ...form, nombre: event.target.value })} required /></div>
          <div className="col-md-6"><label className="form-label" htmlFor="categoria-descripcion">Descripción</label><input id="categoria-descripcion" className="form-control" value={form.descripcion} onChange={(event) => setForm({ ...form, descripcion: event.target.value })} /></div>
          <div className="col-md-2 d-flex align-items-end"><button className="btn btn-success w-100" type="submit">Crear</button></div>
        </div>
      </form>
      <div className="table-responsive"><table className="table table-hover"><thead><tr><th>Nombre</th><th>Descripción</th><th /></tr></thead><tbody>{categorias.map((item) => <tr key={item.id}><td>{item.nombre}</td><td>{item.descripcion}</td><td><button className="btn btn-sm btn-outline-danger" onClick={() => eliminar(item.id)}>Eliminar</button></td></tr>)}</tbody></table></div>
    </section>
  );
}
