import { request } from './httpClient';

export const blogService = {
  getAll: () => request('/api/v1/entities/blogs'),
  getById: (id) => request(`/api/v1/entities/blogs/${id}`),
  create: (blog, token) => request('/api/v1/entities/blogs', {
    method: 'POST', token, body: JSON.stringify(blog),
  }),
  update: (id, blog, token) => request(`/api/v1/entities/blogs/${id}`, {
    method: 'PUT', token, body: JSON.stringify(blog),
  }),
  delete: (id, token) => request(`/api/v1/entities/blogs/${id}`, { method: 'DELETE', token }),
};

export default blogService;
