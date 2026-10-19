import BlogCard from './BlogCard';

export default function BlogList({ blogs, loading, error }) {
  if (loading) return <p className="text-center text-muted">Cargando noticias...</p>;
  if (error) return <div className="alert alert-warning" role="alert">{error}</div>;
  if (!blogs.length) return <div className="alert alert-info" role="status">Todavía no hay noticias publicadas.</div>;

  return (
    <div className="row g-4">
      {blogs.map((blog) => (
        <div className="col-md-6" key={blog.id}>
          <BlogCard blog={blog} />
        </div>
      ))}
    </div>
  );
}
