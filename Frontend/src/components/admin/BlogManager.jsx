import { useEffect, useState } from 'react';
import { blogService } from '../../services/blogService';

const initialForm = { titulo: '', descripcion: '', imagenUrl: '' };

export default function BlogManager({ token }) {
  const [blogs, setBlogs] = useState([]);
  const [form, setForm] = useState(initialForm);
  const [estado, setEstado] = useState(null);

  useEffect(() => { blogService.getAll().then(setBlogs).catch((error) => setEstado({ tipo: 'danger', texto: error.message })); }, []);

  const guardar = async (event) => {
    event.preventDefault();
    try { const creado = await blogService.create(form, token); setBlogs((current) => [...current, creado]); setForm(initialForm); setEstado({ tipo: 'success', texto: 'Noticia publicada.' }); }
    catch (error) { setEstado({ tipo: 'danger', texto: error.message }); }
  };

  const eliminar = async (id) => {
    if (!window.confirm('¿Desactivar esta noticia?')) return;
    try { await blogService.delete(id, token); setBlogs((current) => current.filter((item) => item.id !== id)); }
    catch (error) { setEstado({ tipo: 'danger', texto: error.message }); }
  };

  return (
    <section><h2 className="h4 text-success fw-bold">Noticias</h2>
      {estado && <div className={`alert alert-${estado.tipo}`}>{estado.texto}</div>}
      <form onSubmit={guardar} className="card border-0 shadow-sm p-4 mb-4"><div className="row g-3">
        <div className="col-md-5"><label className="form-label" htmlFor="blog-titulo">Título</label><input id="blog-titulo" className="form-control" value={form.titulo} onChange={(event) => setForm({ ...form, titulo: event.target.value })} maxLength="150" required /></div>
        <div className="col-md-5"><label className="form-label" htmlFor="blog-imagen">Imagen URL</label><input id="blog-imagen" type="url" className="form-control" value={form.imagenUrl} onChange={(event) => setForm({ ...form, imagenUrl: event.target.value })} /></div>
        <div className="col-md-2 d-flex align-items-end"><button className="btn btn-success w-100" type="submit">Publicar</button></div>
        <div className="col-12"><label className="form-label" htmlFor="blog-descripcion">Contenido</label><textarea id="blog-descripcion" className="form-control" rows="4" value={form.descripcion} onChange={(event) => setForm({ ...form, descripcion: event.target.value })} maxLength="20000" required /></div>
      </div></form>
      <div className="row g-3">{blogs.map((blog) => <div className="col-md-6" key={blog.id}><article className="card border-0 shadow-sm h-100"><div className="card-body"><h3 className="h5">{blog.titulo}</h3><p className="text-muted">{blog.descripcion}</p><button className="btn btn-sm btn-outline-danger" onClick={() => eliminar(blog.id)}>Desactivar</button></div></article></div>)}</div>
    </section>
  );
}
