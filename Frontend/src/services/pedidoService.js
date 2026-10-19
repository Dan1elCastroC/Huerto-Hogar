import { request } from './httpClient';

export const pedidoService = {
  crearPedido: (pedidoData, token) => request('/api/v1/entities/pedidos/checkout', {
    method: 'POST', token, body: JSON.stringify(pedidoData),
  }),
  historial: (token) => request('/api/v1/entities/pedidos/mis-pedidos', { token }),
  getById: (id, token) => request(`/api/v1/entities/pedidos/mis-pedidos/${id}`, { token }),
  getAll: (token) => request('/api/v1/entities/pedidos', { token }),
  byStatus: (status, token) => request(`/api/v1/entities/pedidos/estado/${encodeURIComponent(status)}`, { token }),
  updateStatus: (id, status, token) => request(`/api/v1/entities/pedidos/${id}/estado?estado=${encodeURIComponent(status)}`, {
    method: 'PATCH', token,
  }),
};

export default pedidoService;
