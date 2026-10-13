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
      <nav className="navbar navbar-expand-lg">
        <div className="container">
          <a className="navbar-brand" href="#inicio" onClick={(e) => ir(e, 'landing')}>
            <img src="/img/huerto-hogar.png" alt="Logo Huerto Hogar" />
            HUERTO HOGAR
          </a>
          <button
            className="navbar-toggler"
            type="button"
            aria-label="Abrir menú"
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
                  <a href="#salir" onClick={(e) => { e.preventDefault(); setAbierto(false); onLogout(); }}>
                    Cerrar sesión ({user.username || user.correo || 'Usuario'})
                  </a>
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
