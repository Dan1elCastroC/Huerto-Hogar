import { request } from './httpClient';

export const categoriaService = {
  getAll: () => request('/api/v1/entities/categorias'),
  getById: (id) => request(`/api/v1/entities/categorias/${id}`),
  create: (category, token) => request('/api/v1/entities/categorias', {
    method: 'POST', token, body: JSON.stringify(category),
  }),
  update: (id, category, token) => request(`/api/v1/entities/categorias/${id}`, {
    method: 'PUT', token, body: JSON.stringify(category),
  }),
  delete: (id, token) => request(`/api/v1/entities/categorias/${id}`, { method: 'DELETE', token }),
};

export default categoriaService;
