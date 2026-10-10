// src/App.jsx
import React, { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import './index.css';

import Navbar from './components/Navbar';
import LandingPage from './components/LandingPage';
import Login from './components/Login';
import ProductList from './components/ProductList';
import Carrito from './components/Carrito';
import MainPage from './components/MainPage';
import { productoService } from './services/api';

export default function App() {
  const [vista, setVista] = useState('landing'); // 'landing', 'login', 'catalogo', 'carrito', 'main'
  const [productos, setProductos] = useState([]);
  const [carrito, setCarrito] = useState([]);
  const [loading, setLoading] = useState(true);
  const [user, setUser] = useState(null); // Contendrá datos y token JWT

  // Cargar productos desde el backend al iniciar
  useEffect(() => {
    productoService.getAll()
      .then(data => {
        setProductos(data);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  const agregarAlCarrito = (producto) => {
    setCarrito(prev => {
      const existe = prev.find(item => item.id === producto.id);
      if (existe) {
        return prev.map(item => item.id === producto.id ? { ...item, cantidad: item.cantidad + 1 } : item);
      }
      return [...prev, { ...producto, cantidad: 1 }];
    });
  };

  const totalItems = carrito.reduce((acc, item) => acc + item.cantidad, 0);

  return (
    <div className="bg-light min-vh-100 d-flex flex-column">
      <Navbar 
        vista={vista} 
        cambiarVista={setVista} 
        carritoCount={totalItems} 
        user={user} 
        onLogout={() => { setUser(null); setVista('landing'); }} 
      />

      <div className="flex-grow-1">
        {vista === 'landing' && <LandingPage cambiarVista={setVista} />}
        {vista === 'login' && <Login onLoginSuccess={(userData) => setUser(userData)} cambiarVista={setVista} />}
        {vista === 'catalogo' && <ProductList productos={productos} agregarAlCarrito={agregarAlCarrito} loading={loading} />}
        {vista === 'carrito' && <Carrito carrito={carrito} vaciarCarrito={() => setCarrito([])} token={user?.token} />}
        {vista === 'main' && <MainPage productos={productos} setProductos={setProductos} user={user} token={user?.token} />}
      </div>
    </div>
  );
}