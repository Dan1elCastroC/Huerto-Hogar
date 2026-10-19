// src/App.jsx
import React, { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import './index.css';

import Navbar from './components/Navbar';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import CatalogPage from './pages/CatalogPage';
import CartPage from './pages/CartPage';
import AdminPage from './pages/AdminPage';
import BlogPage from './pages/BlogPage';
import ContactPage from './pages/ContactPage';
import ProfilePage from './pages/ProfilePage';
import OrderHistoryPage from './pages/OrderHistoryPage';
import { carritoService } from './services/carritoService';
import { categoriaService } from './services/categoriaService';
import { pedidoService } from './services/pedidoService';
import { productoService } from './services/productoService';
import { addItemToCart, removeCartItem, updateCartItem } from './services/cartUtils';

const USER_STORAGE_KEY = 'huerto-hogar-user';
const CART_STORAGE_KEY = 'huerto-hogar-cart';

export default function App() {
  const [vista, setVista] = useState('landing');
  const [productos, setProductos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [carrito, setCarrito] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem(CART_STORAGE_KEY)) || [];
    } catch {
      return [];
    }
  });
  const [loading, setLoading] = useState(true);
  const [sincronizandoCarrito, setSincronizandoCarrito] = useState(false);
  const [user, setUser] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem(USER_STORAGE_KEY)) || null;
    } catch {
      return null;
    }
  });

  // Cargar productos desde el backend al iniciar
  useEffect(() => {
    Promise.allSettled([productoService.getAll(), categoriaService.getAll()])
      .then(([productsResult, categoriesResult]) => {
        if (productsResult.status === 'fulfilled') setProductos(productsResult.value);
        if (categoriesResult.status === 'fulfilled') setCategorias(categoriesResult.value);
      })
      .finally(() => setLoading(false));
  }, []);

  const agregarAlCarrito = (producto) => {
    setCarrito((prev) => addItemToCart(prev, producto));
    if (user?.token) {
      carritoService.add(producto.id, 1, user.token).catch((error) => {
        console.warn('No se pudo sincronizar el carrito con el backend:', error.message);
      });
    }
  };

  const cambiarCantidad = (productoId, cantidad) => {
    setCarrito((prev) => updateCartItem(prev, productoId, cantidad));
    if (user?.token) {
      carritoService.update(productoId, cantidad, user.token).catch((error) => {
        console.warn('No se pudo actualizar el carrito en el backend:', error.message);
      });
    }
  };

  const eliminarDelCarrito = (productoId) => {
    setCarrito((prev) => removeCartItem(prev, productoId));
    if (user?.token) {
      carritoService.remove(productoId, user.token).catch((error) => {
        console.warn('No se pudo eliminar el producto del backend:', error.message);
      });
    }
  };

  const vaciarCarrito = () => {
    setCarrito([]);
    if (user?.token) {
      carritoService.clear(user.token).catch((error) => {
        console.warn('No se pudo vaciar el carrito en el backend:', error.message);
      });
    }
  };

  const iniciarSesion = (userData) => {
    setUser(userData);
    localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(userData));
    setVista(userData.rol === 'ADMINISTRADOR' ? 'main' : 'catalogo');

    if (userData.token && carrito.length > 0) {
      setSincronizandoCarrito(true);
      carritoService.clear(userData.token)
        .then(() => Promise.all(carrito.map((item) => carritoService.add(item.id, item.cantidad, userData.token))))
        .catch((error) => console.warn('No se pudo sincronizar el carrito al iniciar sesión:', error.message))
        .finally(() => setSincronizandoCarrito(false));
    }
  };

  const cerrarSesion = () => {
    setUser(null);
    localStorage.removeItem(USER_STORAGE_KEY);
    setVista('landing');
  };

  const actualizarUsuario = (updatedUser) => {
    setUser(updatedUser);
    localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(updatedUser));
  };

  const finalizarCompra = async (pedidoData) => {
    if (!user?.token) {
      throw new Error('Debes iniciar sesión para confirmar la compra.');
    }

    const pedido = await pedidoService.crearPedido(pedidoData, user.token);
    setCarrito([]);
    return pedido;
  };

  useEffect(() => {
    localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(carrito));
  }, [carrito]);

  const totalItems = carrito.reduce((acc, item) => acc + item.cantidad, 0);

  return (
    <div className="min-vh-100 d-flex flex-column">
      <Navbar 
        vista={vista} 
        cambiarVista={setVista} 
        carritoCount={totalItems} 
        user={user} 
        onLogout={cerrarSesion}
      />

      <div className="flex-grow-1">
        {vista === 'landing' && <HomePage cambiarVista={setVista} productos={productos} agregarAlCarrito={agregarAlCarrito} />}
        {vista === 'login' && <LoginPage onLoginSuccess={iniciarSesion} cambiarVista={setVista} />}
        {vista === 'registro' && <RegisterPage cambiarVista={setVista} />}
        {vista === 'catalogo' && <CatalogPage productos={productos} categorias={categorias} agregarAlCarrito={agregarAlCarrito} loading={loading} />}
        {vista === 'blog' && <BlogPage />}
        {vista === 'contacto' && <ContactPage user={user} />}
        {vista === 'perfil' && user ? <ProfilePage user={user} onUserUpdated={actualizarUsuario} /> : null}
        {vista === 'pedidos' && user ? <OrderHistoryPage user={user} /> : null}
        {vista === 'carrito' && (
          <CartPage
            carrito={carrito}
            user={user}
            cambiarVista={setVista}
            vaciarCarrito={vaciarCarrito}
            cambiarCantidad={cambiarCantidad}
            eliminarDelCarrito={eliminarDelCarrito}
            finalizarCompra={finalizarCompra}
            sincronizandoCarrito={sincronizandoCarrito}
          />
        )}
        {vista === 'main' && user?.rol === 'ADMINISTRADOR' && (
          <AdminPage productos={productos} setProductos={setProductos} categorias={categorias} user={user} token={user.token} />
        )}
      </div>

      <footer className="footer">
        <div className="container">
          <p>&copy; 2026 HUERTO HOGAR. Todos los derechos reservados.</p>
        </div>
      </footer>
    </div>
  );
}
