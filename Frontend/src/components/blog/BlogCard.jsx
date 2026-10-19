export default function BlogCard({ blog }) {
  return (
    <article className="tarjeta-blog h-100">
      {blog.imagenUrl && <img src={blog.imagenUrl} alt="" className="w-100" style={{ height: 180, objectFit: 'cover' }} />}
      <div className="p-4">
        <h2 className="h5 fw-bold">{blog.titulo}</h2>
        <p className="mb-0 text-muted">{blog.descripcion}</p>
      </div>
    </article>
  );
}
