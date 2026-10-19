import { request } from './httpClient';

export const usuarioService = {
  getProfile: (token) => request('/api/usuarios/perfil', { token }),
  updateProfile: (data, token) => request('/api/usuarios/perfil', {
    method: 'PUT', token, body: JSON.stringify(data),
  }),
  changePassword: (data, token) => request('/api/usuarios/perfil/contrasena', {
    method: 'PUT', token, body: JSON.stringify(data),
  }),
  getAll: (token) => request('/api/admin/usuarios', { token }),
  getById: (id, token) => request(`/api/admin/usuarios/${id}`, { token }),
  create: (data, token) => request('/api/admin/usuarios', {
    method: 'POST', token, body: JSON.stringify(data),
  }),
  update: (id, data, token) => request(`/api/admin/usuarios/${id}`, {
    method: 'PUT', token, body: JSON.stringify(data),
  }),
  toggleActive: (id, token) => request(`/api/admin/usuarios/${id}/toggle-activo`, { method: 'PATCH', token }),
  delete: (id, token) => request(`/api/admin/usuarios/${id}`, { method: 'DELETE', token }),
};

export default usuarioService;
