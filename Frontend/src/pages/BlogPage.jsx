import { useEffect, useState } from 'react';
import BlogList from '../components/blog/BlogList';
import { blogService } from '../services/blogService';

export default function BlogPage() {
  const [blogs, setBlogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    blogService.getAll()
      .then(setBlogs)
      .catch((requestError) => setError(requestError.message || 'No se pudieron cargar las noticias.'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <main className="container my-5">
      <div className="seccion-titulo">
        <h1>Noticias y consejos</h1>
        <p>Ideas para cuidar tu huerto y vivir de manera más sostenible.</p>
      </div>
      <BlogList blogs={blogs} loading={loading} error={error} />
    </main>
  );
}
