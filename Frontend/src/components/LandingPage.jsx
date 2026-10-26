// src/components/LandingPage.jsx
import React from 'react';

export default function LandingPage({ cambiarVista, productos = [], agregarAlCarrito, onVerDetalle }) {
  const destacados = productos.slice(0, 3);

  return (
    <main className="main-content">
      {/* Hero */}
      <section className="hero text-center py-5">
        <div className="container">
          <h1 className="display-4">Del campo a tu hogar</h1>
          <p className="lead">
            HuertoHogar conecta a las familias chilenas con productos frescos, orgánicos
            <br className="d-none d-md-block" />
            y sostenibles, directo desde el campo hasta tu puerta.
          </p>
          <button className="btn btn-success btn-lg" onClick={() => cambiarVista('catalogo')}>
            Ver productos
          </button>
        </div>
        <div className="container">
          <img src="/img/huerto-hogar1.png" alt="Huerto Hogar" className="img-fluid mt-4" />
        </div>
      </section>

      {/* Características */}
      <section className="features py-5">
        <div className="container">
          <div className="row text-center">
            <div className="col-md-4 mb-4">
              <h3>Productos Frescos</h3>
              <p>Frutas, verduras y productos orgánicos seleccionados directamente desde el campo chileno.</p>
            </div>
            <div className="col-md-4 mb-4">
              <h3>Del Campo al Hogar</h3>
              <p>Con más de 6 años de experiencia, llevamos frescura a más de 9 ciudades de Chile.</p>
            </div>
            <div className="col-md-4 mb-4">
              <h3>Estilo de Vida Saludable</h3>
              <p>Promovemos una alimentación consciente y una conexión más cercana con los agricultores locales.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Productos destacados */}
      {destacados.length > 0 && (
        <section className="py-5" style={{ backgroundColor: '#ffffff' }}>
          <div className="container">
            <div className="seccion-titulo">
              <h2>Productos destacados</h2>
              <p>Una muestra de lo que encontrarás en nuestra tienda.</p>
            </div>
            <div className="row">
              {destacados.map((p) => (
                <div className="col-md-4 mb-4" key={p.id}>
                  <div className="tarjeta-producto h-100 p-3 d-flex flex-column">
                    <span className="badge-categoria align-self-start mb-2">{p.categoria}</span>
                    <h5 className="mb-1">{p.nombre}</h5>
                    <p className="fw-bold mb-3">${p.precio?.toLocaleString()}</p>
                    {onVerDetalle && <button type="button" className="btn btn-outline-success mb-2" onClick={() => onVerDetalle(p)}>Ver detalle de {p.nombre}</button>}
                    <button
                      className="btn btn-success mt-auto"
                      disabled={!(p.stock > 0)}
                      onClick={() => agregarAlCarrito && agregarAlCarrito(p)}
                    >
                      Añadir al carrito
                    </button>
                  </div>
                </div>
              ))}
            </div>
            <div className="text-center mt-3">
              <button className="btn btn-outline-success" onClick={() => cambiarVista('catalogo')}>
                Ver todos los productos
              </button>
            </div>
          </div>
        </section>
      )}
    </main>
  );
}
