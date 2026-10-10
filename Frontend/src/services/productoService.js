// src/services/productoService.js

const API_URL = "http://localhost:8080/api/productos";

// Obtener todos los productos desde el backend de Spring Boot
export const getProductos = async () => {
  try {
    const response = await fetch(API_URL);
    if (!response.ok) {
      throw new Error("Error al conectar con el servidor");
    }
    return await response.json();
  } catch (error) {
    console.error("Error al conectar con el backend, usando respaldo local:", error);
    // Datos de respaldo por si el backend no está encendido
    return [
      { id: 1, nombre: "Kit de Huerto Urbano", precio: 15990, categoria: "Huerto", stock: 12, imagen: "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=400" },
      { id: 2, nombre: "Compostera Doméstica", precio: 28990, categoria: "Hogar", stock: 5, imagen: "https://images.unsplash.com/photo-1592417817098-8f3d691a4bf5?w=400" },
      { id: 3, nombre: "Set de Herramientas de Jardín", precio: 12490, categoria: "Huerto", stock: 20, imagen: "https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=400" }
    ];
  }
};

// Agregar un nuevo producto (Petición POST al backend)
export const agregarProducto = async (nuevoProducto, token) => {
  try {
    const response = await fetch(API_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        ...(token ? { "Authorization": `Bearer ${token}` } : {})
      },
      body: JSON.stringify(nuevoProducto)
    });
    if (!response.ok) throw new Error("No se pudo registrar el producto");
    return await response.json();
  } catch (error) {
    console.error("Error al agregar producto:", error);
    throw error;
  }
};

// Eliminar un producto por ID (Petición DELETE al backend)
export const eliminarProducto = async (id, token) => {
  try {
    const response = await fetch(`${API_URL}/${id}`, {
      method: "DELETE",
      headers: {
        ...(token ? { "Authorization": `Bearer ${token}` } : {})
      }
    });
    if (!response.ok) throw new Error("No se pudo eliminar el producto");
    return true;
  } catch (error) {
    console.error("Error al eliminar producto:", error);
    throw error;
  }
};