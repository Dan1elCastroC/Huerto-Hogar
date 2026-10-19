import { request } from './httpClient';

export const reporteService = {
  resumen: (token) => request('/api/v1/entities/reportes/resumen', { token }),
  stockCritico: (token) => request('/api/v1/entities/reportes/stock-critico', { token }),
  pedidosPorEstado: (token) => request('/api/v1/entities/reportes/pedidos-por-estado', { token }),
  productosMasVendidos: (token) => request('/api/v1/entities/reportes/productos-mas-vendidos', { token }),
};

export default reporteService;
