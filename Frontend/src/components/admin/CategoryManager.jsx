import { useEffect, useState } from 'react';
import { categoriaService } from '../../services/categoriaService';

const initialForm = { nombre: '', descripcion: '' };

export default function CategoryManager({ token, categorias, setCategorias }) {
  const [form, setForm] = useState(initialForm);
  const [estado, setEstado] = useState(null);
  const [editando, setEditando] = useState(null);
  const [guardando, setGuardando] = useState(false);

  useEffect(() => {
    categoriaService.getAll().then(setCategorias).catch((error) => setEstado({ tipo: 'danger', texto: error.message }));
  }, [setCategorias]);

  const guardar = async (event) => {
    event.preventDefault();
    setGuardando(true);
    try {
      const guardada = editando === null ? await categoriaService.create(form, token) : await categoriaService.update(editando, form, token);
      setCategorias((current) => editando === null ? [...current, guardada] : current.map((item) => item.id === editando ? guardada : item));
      setForm(initialForm);
      setEditando(null);
      setEstado({ tipo: 'success', texto: editando === null ? 'Categoría creada correctamente.' : 'Categoría actualizada correctamente.' });
    } catch (error) { setEstado({ tipo: 'danger', texto: error.message }); }
    finally { setGuardando(false); }
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
      {estado && <div className={`alert alert-${estado.tipo}`} role="alert">{estado.texto}</div>}
      <form onSubmit={guardar} className="card border-0 shadow-sm p-4 mb-4">
        <div className="row g-3">
          <div className="col-md-4"><label className="form-label" htmlFor="categoria-nombre">Nombre</label><input id="categoria-nombre" className="form-control" value={form.nombre} onChange={(event) => setForm({ ...form, nombre: event.target.value })} required /></div>
          <div className="col-md-6"><label className="form-label" htmlFor="categoria-descripcion">Descripción</label><input id="categoria-descripcion" className="form-control" value={form.descripcion} onChange={(event) => setForm({ ...form, descripcion: event.target.value })} /></div>
          <div className="col-md-2 d-flex align-items-end"><button className="btn btn-success w-100" type="submit" disabled={guardando}>{guardando ? 'Guardando...' : editando === null ? 'Crear' : 'Guardar cambios'}</button></div>
          {editando !== null && <div className="col-12"><button type="button" className="btn btn-outline-secondary" onClick={() => { setEditando(null); setForm(initialForm); }}>Cancelar edición</button></div>}
        </div>
      </form>
      <div className="table-responsive"><table className="table table-hover"><thead><tr><th>Nombre</th><th>Descripción</th><th>Acciones</th></tr></thead><tbody>{categorias.map((item) => <tr key={item.id}><td>{item.nombre}</td><td>{item.descripcion}</td><td><button type="button" aria-label={`Editar ${item.nombre}`} className="btn btn-sm btn-outline-success me-2" onClick={() => { setEditando(item.id); setForm({ nombre: item.nombre, descripcion: item.descripcion || '' }); setEstado(null); }}>Editar</button><button type="button" className="btn btn-sm btn-outline-danger" onClick={() => eliminar(item.id)}>Eliminar</button></td></tr>)}</tbody></table></div>
    </section>
  );
}
