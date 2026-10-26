// src/components/ProductList.jsx
import React, { useMemo, useState } from 'react';

export default function ProductList({ productos, categorias = [], agregarAlCarrito, loading, errorProductos, onVerDetalle, categoriaInicial = 'todas', cupon, titulo = 'Catálogo de Productos y Semillas' }) {
  const [busqueda, setBusqueda] = useState('');
  const [categoriaSeleccionada, setCategoriaSeleccionada] = useState(categoriaInicial);

  const categoriasDisponibles = useMemo(() => {
    const names = productos.map((producto) => producto.categoriaNombre || producto.categoria).filter(Boolean);
    return [...new Set([...categorias.map((categoria) => categoria.nombre), ...names])];
  }, [categorias, productos]);

  const productosFiltrados = useMemo(() => {
    const query = busqueda.trim().toLowerCase();
    return productos.filter((producto) => {
      const nombre = producto.nombre?.toLowerCase() || '';
      const categoria = (producto.categoriaNombre || producto.categoria || '').toLowerCase();
      const coincideTexto = !query || nombre.includes(query) || categoria.includes(query);
      const coincideCategoria = categoriaSeleccionada === 'todas' || categoria === categoriaSeleccionada.toLowerCase();
      return coincideTexto && coincideCategoria;
    });
  }, [busqueda, categoriaSeleccionada, productos]);

  if (loading) {
    return (
      <div className="text-center my-5 py-5">
        <div className="spinner-border text-success" role="status"></div>
        <p className="text-muted mt-2">Cargando productos desde el servidor...</p>
      </div>
    );
  }

  if (errorProductos) {
    return <div className="container my-5"><div className="alert alert-danger" role="alert">{errorProductos}</div></div>;
  }

  return (
    <div className="container my-5">
      <h2 className="text-center mb-4 text-success fw-bold">{titulo}</h2>
      <div className="row g-2 mb-4">
        <div className="col-md-8">
          <label htmlFor="buscar-productos" className="visually-hidden">Buscar productos</label>
          <input
            id="buscar-productos"
            type="search"
            className="form-control"
            placeholder="Buscar por nombre o categoría..."
            value={busqueda}
            onChange={(event) => setBusqueda(event.target.value)}
          />
        </div>
        <div className="col-md-4">
          <label htmlFor="filtro-categoria" className="visually-hidden">Filtrar por categoría</label>
          <select id="filtro-categoria" className="form-select" value={categoriaSeleccionada} onChange={(event) => setCategoriaSeleccionada(event.target.value)}>
            <option value="todas">Todas las categorías</option>
            {categoriasDisponibles.map((categoria) => <option key={categoria} value={categoria}>{categoria}</option>)}
          </select>
        </div>
      </div>
      <div className="row">
        {productosFiltrados.length === 0 ? (
          <div className="alert alert-warning text-center">No hay productos disponibles en este momento.</div>
        ) : (
          productosFiltrados.map((producto) => (
            <div className="col-md-3 mb-4" key={producto.id}>
              <div className="card h-100 shadow-sm border-0">
                <img 
                  src={producto.imagenUrl || producto.imagen || '/img/huerto-hogar1.png'}
                  className="card-img-top" 
                  alt={producto.nombre} 
                  style={{ height: '180px', objectFit: 'cover' }} 
                />
                <div className="card-body d-flex flex-column">
                  <span className="badge bg-secondary mb-2 align-self-start">{producto.categoriaNombre || producto.categoria || 'Sin categoría'}</span>
                  <h5 className="card-title fw-bold">{producto.nombre}</h5>
                  <p className="card-text text-success fw-bold fs-5">${Number(producto.precio).toLocaleString('es-CL')}</p>
                  {cupon && <p className="text-success fw-bold">Precio con cupón: ${Math.round(Number(producto.precio) * (1 - Number(cupon.porcentaje) / 100)).toLocaleString('es-CL')}</p>}
                  <small className="text-muted mb-3">Stock disponible: {producto.stock}</small>
                  {onVerDetalle && <button type="button" className="btn btn-outline-success mb-2" aria-label={`Ver detalle de ${producto.nombre}`} onClick={() => onVerDetalle(producto)}>Ver detalle</button>}
                  <button 
                    className="btn btn-success mt-auto w-100 fw-bold"
                    onClick={() => agregarAlCarrito(producto)}
                    disabled={!producto.stock || producto.stock < 1}
                  >
                    {producto.stock > 0 ? 'Añadir al Carrito' : 'Sin stock'}
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
