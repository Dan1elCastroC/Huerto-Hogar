// src/services/api.js
const API_BASE_URL = "http://localhost:8080";

const getHeaders = (token) => {
  const headers = {
    "Content-Type": "application/json",
  };
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }
  return headers;
};

export const authService = {
  login: async (credentials) => {
    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(credentials),
    });
    if (!response.ok) throw new Error("Credenciales inválidas");
    return await response.json();
  },
  registro: async (userData) => {
    const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(userData),
    });
    if (!response.ok) throw new Error("Error al registrar usuario");
    return await response.json();
  }
};

export const productoService = {
  getAll: async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/productos`);
      if (!response.ok) throw new Error("Error al obtener productos");
      return await response.json();
    } catch (error) {
      console.error("Usando respaldo local por error de red:", error);
      // Datos de respaldo si el backend no está corriendo en el momento
      return [
        { id: 1, nombre: "Kit de Huerto Urbano", precio: 15990, categoria: "Huerto", stock: 12, imagen: "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=400" },
        { id: 2, nombre: "Compostera Doméstica", precio: 28990, categoria: "Hogar", stock: 5, imagen: "https://images.unsplash.com/photo-1592417817098-8f3d691a4bf5?w=400" },
        { id: 3, nombre: "Set de Herramientas de Jardín", precio: 12490, categoria: "Huerto", stock: 20, imagen: "https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=400" },
        { id: 4, nombre: "Sustrato Premium 25L", precio: 6990, categoria: "Hogar", stock: 15, imagen: "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=400" }
      ];
    }
  },
  create: async (producto, token) => {
    const response = await fetch(`${API_BASE_URL}/api/productos`, {
      method: "POST",
      headers: getHeaders(token),
      body: JSON.stringify(producto),
    });
    if (!response.ok) throw new Error("Error al crear producto");
    return await response.json();
  },
  delete: async (id, token) => {
    const response = await fetch(`${API_BASE_URL}/api/productos/${id}`, {
      method: "DELETE",
      headers: getHeaders(token),
    });
    if (!response.ok) throw new Error("Error al eliminar producto");
    return true;
  }
};

export const pedidoService = {
  crearPedido: async (pedidoData, token) => {
    const response = await fetch(`${API_BASE_URL}/api/pedidos`, {
      method: "POST",
      headers: getHeaders(token),
      body: JSON.stringify(pedidoData),
    });
    if (!response.ok) throw new Error("Error al procesar la compra");
    return await response.json();
  }
};