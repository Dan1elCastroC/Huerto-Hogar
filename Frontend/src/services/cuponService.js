import { request } from './httpClient';

export const cuponService = {
  getAll: (token) => request('/api/v1/entities/cupones', { token }),
  validate: (code) => request(`/api/v1/entities/cupones/validar/${encodeURIComponent(code)}`),
  create: (coupon, token) => request('/api/v1/entities/cupones', {
    method: 'POST', token, body: JSON.stringify(coupon),
  }),
  delete: (id, token) => request(`/api/v1/entities/cupones/${id}`, { method: 'DELETE', token }),
};

export default cuponService;
