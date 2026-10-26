export default function BlogDetailPage({ blog, volver }) {
  return (
    <main className="container my-5">
      <button type="button" className="btn btn-outline-success mb-4" onClick={volver}>Volver a noticias</button>
      {blog ? <article className="mx-auto" style={{ maxWidth: 840 }}>
        <h1 className="text-success fw-bold mb-4">{blog.titulo}</h1>
        {blog.imagenUrl && <img src={blog.imagenUrl} alt="" className="img-fluid rounded mb-4" />}
        <p className="lead" style={{ whiteSpace: 'pre-line' }}>{blog.descripcion}</p>
      </article> : <p role="status">Noticia no disponible.</p>}
    </main>
  );
}
