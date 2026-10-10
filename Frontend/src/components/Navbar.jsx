// src/components/Navbar.jsx
import React from 'react';

export default function Navbar({ vista, cambiarVista, carritoCount, user, onLogout }) {
  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-success shadow-sm sticky-top">
      <div className="container">
        <a className="navbar-brand fw-bold" href="#home" onClick={() => cambiarVista('landing')}>
          🌱 Huerto Hogar
        </a>
        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse justify-content-end" id="navbarNav">
          <ul className="navbar-nav align-items-center gap-2">
            <li className="nav-item">
              <button className={`btn nav-link text-white ${vista === 'landing' ? 'fw-bold border-bottom' : ''}`} onClick={() => cambiarVista('landing')}>
                Inicio
              </button>
            </li>
            <li className="nav-item">
              <button className={`btn nav-link text-white ${vista === 'catalogo' ? 'fw-bold border-bottom' : ''}`} onClick={() => cambiarVista('catalogo')}>
                Catálogo
              </button>
            </li>
            <li className="nav-item">
              <button className={`btn nav-link text-white position-relative ${vista === 'carrito' ? 'fw-bold border-bottom' : ''}`} onClick={() => cambiarVista('carrito')}>
                🛒 Carrito
                {carritoCount > 0 && (
                  <span className="position-absolute top-25 start-100 translate-middle badge rounded-pill bg-danger">
                    {carritoCount}
                  </span>
                )}
              </button>
            </li>
            {user ? (
              <>
                <li className="nav-item">
                  <button className={`btn nav-link text-white ${vista === 'main' ? 'fw-bold border-bottom' : ''}`} onClick={() => cambiarVista('main')}>
                    📊 Panel Principal
                  </button>
                </li>
                <li className="nav-item ms-2">
                  <button className="btn btn-outline-light btn-sm" onClick={onLogout}>
                    Cerrar Sesión ({user.username || 'Usuario'})
                  </button>
                </li>
              </>
            ) : (
              <li className="nav-item ms-2">
                <button className="btn btn-light text-success fw-bold btn-sm px-3" onClick={() => cambiarVista('login')}>
                  Iniciar Sesión
                </button>
              </li>
            )}
          </ul>
        </div>
      </div>
    </nav>
  );
}