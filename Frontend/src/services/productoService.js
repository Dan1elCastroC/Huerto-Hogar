import { request } from './httpClient';

const fallbackProducts = [
  { id: 1, codigo: 'KIT-001', nombre: 'Kit de Huerto Urbano', precio: 15990, categoria: 'Huerto', stock: 12, imagen: 'https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=400' },
  { id: 2, codigo: 'COM-001', nombre: 'Compostera Doméstica', precio: 28990, categoria: 'Hogar', stock: 5, imagen: 'https://images.unsplash.com/photo-1592417817098-8f3d691a4bf5?w=400' },
  { id: 3, codigo: 'HER-001', nombre: 'Set de Herramientas de Jardín', precio: 12490, categoria: 'Huerto', stock: 20, imagen: 'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=400' },
  { id: 4, codigo: 'SUS-001', nombre: 'Sustrato Premium 25L', precio: 6990, categoria: 'Hogar', stock: 15, imagen: 'https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=400' },
];

export const normalizeProduct = (product) => ({
  ...product,
  categoria: product.categoria || product.categoriaNombre || 'Sin categoría',
  categoriaNombre: product.categoriaNombre || product.categoria || 'Sin categoría',
  imagen: product.imagen || product.imagenUrl || '',
  imagenUrl: product.imagenUrl || product.imagen || '',
});

export const productoService = {
  getAll: async () => {
    try {
      const products = await request('/api/v1/entities/productos');
      return products.map(normalizeProduct);
    } catch (error) {
      console.warn('Usando respaldo local por error de red:', error.message);
      return fallbackProducts;
    }
  },

  getById: async (id) => normalizeProduct(await request(`/api/v1/entities/productos/${id}`)),

  search: async (query) => (await request(`/api/v1/entities/productos/buscar?q=${encodeURIComponent(query)}`)).map(normalizeProduct),

  byCategory: async (categoryId) => (await request(`/api/v1/entities/productos/categoria/${categoryId}`)).map(normalizeProduct),

  criticalStock: async (token) => (await request('/api/v1/entities/productos/stock-critico', { token })).map(normalizeProduct),

  create: async (product, token) => normalizeProduct(await request('/api/v1/entities/productos', {
    method: 'POST',
    token,
    body: JSON.stringify(product),
  })),

  update: async (id, product, token) => normalizeProduct(await request(`/api/v1/entities/productos/${id}`, {
    method: 'PUT',
    token,
    body: JSON.stringify(product),
  })),

  delete: async (id, token) => {
    await request(`/api/v1/entities/productos/${id}`, { method: 'DELETE', token });
    return true;
  },
};

export const getProductos = productoService.getAll;
export const agregarProducto = productoService.create;
export const eliminarProducto = productoService.delete;

export default productoService;
