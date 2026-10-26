export default function BlogCard({ blog, onVerDetalle }) {
  return (
    <article className="tarjeta-blog h-100">
      {blog.imagenUrl && <img src={blog.imagenUrl} alt="" className="w-100" style={{ height: 180, objectFit: 'cover' }} />}
      <div className="p-4">
        <h2 className="h5 fw-bold">{blog.titulo}</h2>
        <p className="mb-0 text-muted">{onVerDetalle && blog.descripcion?.length > 180 ? `${blog.descripcion.slice(0, 180)}…` : blog.descripcion}</p>
        {onVerDetalle && <button type="button" className="btn btn-outline-success mt-3" aria-label={`Leer ${blog.titulo}`} onClick={() => onVerDetalle(blog)}>Leer noticia</button>}
      </div>
    </article>
  );
}
