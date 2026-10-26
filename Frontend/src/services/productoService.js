
import { request } from './httpClient';

export const normalizeProduct = (product) => ({
  ...product,
  categoria: product.categoria || product.categoriaNombre || 'Sin categoría',
  categoriaNombre: product.categoriaNombre || product.categoria || 'Sin categoría',
  imagen: product.imagen || product.imagenUrl || '',
  imagenUrl: product.imagenUrl || product.imagen || '',
});

export const productoService = {
  // Consulta los productos reales del backend.
  // Si falla la conexión, el error se comunica al frontend.
  getAll: async () => {
    const products = await request('/api/v1/entities/productos');
    return products.map(normalizeProduct);
  },

  getById: async (id) =>
    normalizeProduct(
      await request(`/api/v1/entities/productos/${id}`)
    ),

  search: async (query) =>
    (
      await request(
        `/api/v1/entities/productos/buscar?q=${encodeURIComponent(query)}`
      )
    ).map(normalizeProduct),

  byCategory: async (categoryId) =>
    (
      await request(
        `/api/v1/entities/productos/categoria/${categoryId}`
      )
    ).map(normalizeProduct),

  criticalStock: async (token) =>
    (
      await request('/api/v1/entities/productos/stock-critico', {
        token,
      })
    ).map(normalizeProduct),

  create: async (product, token) =>
    normalizeProduct(
      await request('/api/v1/entities/productos', {
        method: 'POST',
        token,
        body: JSON.stringify(product),
      })
    ),

  update: async (id, product, token) =>
    normalizeProduct(
      await request(`/api/v1/entities/productos/${id}`, {
        method: 'PUT',
        token,
        body: JSON.stringify(product),
      })
    ),

  delete: async (id, token) => {
    await request(`/api/v1/entities/productos/${id}`, {
      method: 'DELETE',
      token,
    });

    return true;
  },
};

export const getProductos = productoService.getAll;
export const agregarProducto = productoService.create;
export const eliminarProducto = productoService.delete;

export default productoService;
