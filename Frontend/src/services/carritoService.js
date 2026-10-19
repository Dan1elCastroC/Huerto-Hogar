import { request } from './httpClient';

export const carritoService = {
  get: (token, cupon) => request(`/api/v1/entities/carrito${cupon ? `?cupon=${encodeURIComponent(cupon)}` : ''}`, { token }),
  add: (productoId, cantidad, token) => request('/api/v1/entities/carrito/agregar', {
    method: 'POST', token, body: JSON.stringify({ productoId, cantidad }),
  }),
  update: (productoId, cantidad, token) => request(`/api/v1/entities/carrito/actualizar/${productoId}?cantidad=${cantidad}`, {
    method: 'PUT', token,
  }),
  remove: (productoId, token) => request(`/api/v1/entities/carrito/eliminar/${productoId}`, { method: 'DELETE', token }),
  clear: (token) => request('/api/v1/entities/carrito/vaciar', { method: 'DELETE', token }),
};

export default carritoService;
