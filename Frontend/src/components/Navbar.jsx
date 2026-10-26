// src/components/Navbar.jsx
import React, { useState } from 'react';

export default function Navbar({ vista, cambiarVista, carritoCount, user, onLogout }) {
  const [abierto, setAbierto] = useState(false);

  const ir = (e, destino) => {
    e.preventDefault();
    cambiarVista(destino);
    setAbierto(false);
  };

  return (
    <header className="header">
      <nav className="navbar navbar-expand-xxl" aria-label="Navegación de la tienda">
        <div className="container">
          <a className="navbar-brand" href="#inicio" onClick={(e) => ir(e, 'landing')}>
            <img src="/img/huerto-hogar.png" alt="Logo Huerto Hogar" />
            HUERTO HOGAR
          </a>
          <button
            className="navbar-toggler"
            type="button"
            aria-label="Abrir menú"
            aria-expanded={abierto}
            aria-controls="navbarNav"
            onClick={() => setAbierto(!abierto)}
          >
            <span className="navbar-toggler-icon"></span>
          </button>

          <div className={`collapse navbar-collapse ${abierto ? 'show' : ''}`} id="navbarNav">
            <ul className="navbar-nav mx-auto">
              <li className="nav-item">
                <a className={`nav-link ${vista === 'landing' ? 'active' : ''}`} href="#inicio" onClick={(e) => ir(e, 'landing')}>
                  Home
                </a>
              </li>
              <li className="nav-item">
                <a className={`nav-link ${vista === 'catalogo' ? 'active' : ''}`} href="#productos" onClick={(e) => ir(e, 'catalogo')}>
                  Productos
                </a>
              </li>
              <li className="nav-item">
                <a className={`nav-link ${vista === 'categorias' ? 'active' : ''}`} href="#categorias" onClick={(e) => ir(e, 'categorias')}>Categorías</a>
              </li>
              <li className="nav-item">
                <a className={`nav-link ${vista === 'ofertas' ? 'active' : ''}`} href="#ofertas" onClick={(e) => ir(e, 'ofertas')}>Ofertas</a>
              </li>
              <li className="nav-item">
                <a className={`nav-link ${vista === 'nosotros' ? 'active' : ''}`} href="#nosotros" onClick={(e) => ir(e, 'nosotros')}>Nosotros</a>
              </li>
              <li className="nav-item">
                <a className={`nav-link ${vista === 'blog' ? 'active' : ''}`} href="#blog" onClick={(e) => ir(e, 'blog')}>
                  Noticias
                </a>
              </li>
              <li className="nav-item">
                <a className={`nav-link ${vista === 'contacto' ? 'active' : ''}`} href="#contacto" onClick={(e) => ir(e, 'contacto')}>
                  Contacto
                </a>
              </li>
              {user?.rol === 'ADMINISTRADOR' && (
                <li className="nav-item">
                  <a className={`nav-link ${vista === 'main' ? 'active' : ''}`} href="#panel" onClick={(e) => ir(e, 'main')}>
                    Panel principal
                  </a>
                </li>
              )}
            </ul>

            <div className="d-flex align-items-center flex-wrap">
              <div className="nav-auth-links">
                {user ? (
                  <>
                    <span className="nav-user-name me-3 small fw-semibold">{[user.nombre, user.apellidos].filter(Boolean).join(' ').trim() || user.correo}</span>
                    <a href="#perfil" onClick={(e) => ir(e, 'perfil')}>Mi perfil</a>
                    <a href="#pedidos" onClick={(e) => ir(e, 'pedidos')}>Mis pedidos</a>
                    <a href="#salir" onClick={(e) => { e.preventDefault(); setAbierto(false); onLogout(); }}>Cerrar sesión</a>
                  </>
                ) : (
                  <>
                    <a href="#login" onClick={(e) => ir(e, 'login')}>Iniciar sesión</a>
                    <a href="#registro" onClick={(e) => ir(e, 'registro')}>Registrarse</a>
                  </>
                )}
              </div>
              <a href="#carrito" className="nav-carrito ms-3" onClick={(e) => ir(e, 'carrito')}>
                🛒 Carrito <span className="badge rounded-pill">{carritoCount}</span>
              </a>
            </div>
          </div>
        </div>
      </nav>
    </header>
  );
}
