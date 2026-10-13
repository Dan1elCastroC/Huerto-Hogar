const API_BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";
const API_PATH = `${API_BASE_URL}/api`;

const getHeaders = (token) => {
  const headers = {
    "Content-Type": "application/json",
  };
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }
  return headers;
};

const parseResponse = async (response) => {
  const contentType = response.headers.get("content-type") || "";
  const data = contentType.includes("application/json")
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    const message = typeof data === "string" ? data : data.message;
    throw new Error(message || "La solicitud no pudo completarse");
  }

  return data;
};

const normalizeProduct = (product) => ({
  ...product,
  categoria: product.categoria || product.categoriaNombre || "Sin categoría",
  categoriaNombre: product.categoriaNombre || product.categoria || "Sin categoría",
  imagen: product.imagen || product.imagenUrl || "",
  imagenUrl: product.imagenUrl || product.imagen || "",
});

export const authService = {
  login: async (credentials) => {
    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ correo: credentials.correo, contrasena: credentials.contrasena }),
    });
    return parseResponse(response);
  },
  registro: async (userData) => {
    const response = await fetch(`${API_BASE_URL}/api/auth/registro`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(userData),
    });
    return parseResponse(response);
  },
  getPerfil: async (token) => parseResponse(await fetch(`${API_BASE_URL}/api/usuarios/perfil`, {
    headers: getHeaders(token),
  })),
};

export const productoService = {
  getAll: async () => {
    try {
      const response = await fetch(`${API_PATH}/v1/entities/productos`);
      const products = await parseResponse(response);
      return products.map(normalizeProduct);
    } catch (error) {
      console.warn("Usando respaldo local por error de red:", error.message);
      // Datos de respaldo si el backend no está corriendo en el momento
      return [
        { id: 1, codigo: "KIT-001", nombre: "Kit de Huerto Urbano", precio: 15990, categoria: "Huerto", stock: 12, imagen: "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=400" },
        { id: 2, codigo: "COM-001", nombre: "Compostera Doméstica", precio: 28990, categoria: "Hogar", stock: 5, imagen: "https://images.unsplash.com/photo-1592417817098-8f3d691a4bf5?w=400" },
        { id: 3, codigo: "HER-001", nombre: "Set de Herramientas de Jardín", precio: 12490, categoria: "Huerto", stock: 20, imagen: "https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=400" },
        { id: 4, codigo: "SUS-001", nombre: "Sustrato Premium 25L", precio: 6990, categoria: "Hogar", stock: 15, imagen: "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=400" }
      ];
    }
  },
  create: async (producto, token) => {
    const response = await fetch(`${API_PATH}/v1/entities/productos`, {
      method: "POST",
      headers: getHeaders(token),
      body: JSON.stringify(producto),
    });
    return normalizeProduct(await parseResponse(response));
  },
  update: async (id, producto, token) => {
    const response = await fetch(`${API_PATH}/v1/entities/productos/${id}`, {
      method: "PUT",
      headers: getHeaders(token),
      body: JSON.stringify(producto),
    });
    return normalizeProduct(await parseResponse(response));
  },
  delete: async (id, token) => {
    const response = await fetch(`${API_PATH}/v1/entities/productos/${id}`, {
      method: "DELETE",
      headers: getHeaders(token),
    });
    await parseResponse(response);
    return true;
  }
};

export const categoriaService = {
  getAll: async () => parseResponse(await fetch(`${API_PATH}/v1/entities/categorias`)),
};

export const carritoService = {
  get: async (token) => parseResponse(await fetch(`${API_PATH}/v1/entities/carrito`, {
    headers: getHeaders(token),
  })),
  add: async (productoId, cantidad, token) => parseResponse(await fetch(`${API_PATH}/v1/entities/carrito/agregar`, {
    method: "POST",
    headers: getHeaders(token),
    body: JSON.stringify({ productoId, cantidad }),
  })),
  update: async (productoId, cantidad, token) => parseResponse(await fetch(`${API_PATH}/v1/entities/carrito/actualizar/${productoId}?cantidad=${cantidad}`, {
    method: "PUT",
    headers: getHeaders(token),
  })),
  remove: async (productoId, token) => parseResponse(await fetch(`${API_PATH}/v1/entities/carrito/eliminar/${productoId}`, {
    method: "DELETE",
    headers: getHeaders(token),
  })),
  clear: async (token) => parseResponse(await fetch(`${API_PATH}/v1/entities/carrito/vaciar`, {
    method: "DELETE",
    headers: getHeaders(token),
  })),
};

export const pedidoService = {
  crearPedido: async (pedidoData, token) => {
    const response = await fetch(`${API_PATH}/v1/entities/pedidos/checkout`, {
      method: "POST",
      headers: getHeaders(token),
      body: JSON.stringify(pedidoData),
    });
    return parseResponse(response);
  },
  historial: async (token) => parseResponse(await fetch(`${API_PATH}/v1/entities/pedidos/mis-pedidos`, {
    headers: getHeaders(token),
  })),
};
