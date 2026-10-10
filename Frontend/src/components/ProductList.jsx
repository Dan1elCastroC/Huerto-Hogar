// src/components/ProductList.jsx
import React from 'react';

export default function ProductList({ productos, agregarAlCarrito, loading }) {
  if (loading) {
    return (
      <div className="text-center my-5 py-5">
        <div className="spinner-border text-success" role="status"></div>
        <p className="text-muted mt-2">Cargando productos desde el servidor...</p>
      </div>
    );
  }

  return (
    <div className="container my-5">
      <h2 className="text-center mb-4 text-success fw-bold">Catálogo de Productos y Semillas</h2>
      <div className="row">
        {productos.length === 0 ? (
          <div className="alert alert-warning text-center">No hay productos disponibles en este momento.</div>
        ) : (
          productos.map((producto) => (
            <div className="col-md-3 mb-4" key={producto.id}>
              <div className="card h-100 shadow-sm border-0">
                <img 
                  src={producto.imagen || "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=400"} 
                  className="card-img-top" 
                  alt={producto.nombre} 
                  style={{ height: '180px', objectFit: 'cover' }} 
                />
                <div className="card-body d-flex flex-column">
                  <span className="badge bg-secondary mb-2 align-self-start">{producto.categoria}</span>
                  <h5 className="card-title fw-bold">{producto.nombre}</h5>
                  <p className="card-text text-success fw-bold fs-5">${producto.precio?.toLocaleString()}</p>
                  <small className="text-muted mb-3">Stock disponible: {producto.stock}</small>
                  <button 
                    className="btn btn-success mt-auto w-100 fw-bold"
                    onClick={() => agregarAlCarrito(producto)}
                  >
                    Añadir al Carrito
                  </button>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}