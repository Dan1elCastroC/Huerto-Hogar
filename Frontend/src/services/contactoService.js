import { request } from './httpClient';

export const contactoService = {
  create: (contact, token) => request('/api/v1/entities/contacto', {
    method: 'POST', token, body: JSON.stringify(contact),
  }),
  getAll: (token) => request('/api/v1/entities/contacto', { token }),
  markRead: (id, token) => request(`/api/v1/entities/contacto/${id}/leido`, { method: 'PATCH', token }),
};

export default contactoService;
