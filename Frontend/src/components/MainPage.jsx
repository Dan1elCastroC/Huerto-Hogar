// src/components/MainPage.jsx
import React, { useState } from 'react';
import { productoService } from '../services/api';

export default function MainPage({ productos, setProductos, user, token }) {
  const [nuevoProd, setNuevoProd] = useState({ nombre: '', precio: '', categoria: '', stock: '', imagen: '' });
  const [mensaje, setMensaje] = useState(null);

  const handleCrearProducto = async (e) => {
    e.preventDefault();
    try {
      const creado = await productoService.create(nuevoProd, token);
      setProductos([...productos, creado]);
      setMensaje({ tipo: 'success', texto: 'Producto registrado correctamente en el servidor.' });
      setNuevoProd({ nombre: '', precio: '', categoria: '', stock: '', imagen: '' });
    } catch (err) {
      setMensaje({ tipo: 'danger', texto: 'Error al registrar el producto en el backend.' });
    }
  };

  const handleEliminar = async (id) => {
    try {
      await productoService.delete(id, token);
      setProductos(productos.filter(p => p.id !== id));
      setMensaje({ tipo: 'success', texto: 'Producto eliminado correctamente.' });
    } catch (err) {
      setMensaje({ tipo: 'danger', texto: 'No se pudo eliminar el producto.' });
    }
  };

  return (
    <div className="container my-5">
      <div className="row mb-4">
        <div className="col">
          <h2 className="text-success fw-bold">📊 Panel Principal de Administración</h2>
          <p className="text-muted">Bienvenido, <strong>{user?.username || 'Administrador'}</strong>. Controla el inventario conectado al backend.</p>
        </div>
      </div>

      {mensaje && <div className={`alert alert-${mensaje.tipo}`}>{mensaje.texto}</div>}

      {/* Formulario de registro de productos */}
      <div className="card shadow-sm border-0 p-4 mb-5 bg-white rounded-4">
        <h4 className="fw-bold mb-3 text-success">Agregar Nuevo Producto</h4>
        <form onSubmit={handleCrearProducto} className="row g-3">
          <div className="col-md-3">
            <input type="text" className="form-control" placeholder="Nombre" value={nuevoProd.nombre} onChange={e => setNuevoProd({...nuevoProd, nombre: e.target.value})} required />
          </div>
          <div className="col-md-2">
            <input type="number" className="form-control" placeholder="Precio ($)" value={nuevoProd.precio} onChange={e => setNuevoProd({...nuevoProd, precio: e.target.value})} required />
          </div>
          <div className="col-md-2">
            <input type="text" className="form-control" placeholder="Categoría" value={nuevoProd.categoria} onChange={e => setNuevoProd({...nuevoProd, categoria: e.target.value})} required />
          </div>
          <div className="col-md-2">
            <input type="number" className="form-control" placeholder="Stock" value={nuevoProd.stock} onChange={e => setNuevoProd({...nuevoProd, stock: e.target.value})} required />
          </div>
          <div className="col-md-3">
            <input type="text" className="form-control" placeholder="URL Imagen" value={nuevoProd.imagen} onChange={e => setNuevoProd({...nuevoProd, imagen: e.target.value})} />
          </div>
          <div className="col-12">
            <button type="submit" className="btn btn-success fw-bold px-4">Guardar en Base de Datos</button>
          </div>
        </form>
      </div>

      {/* Listado de Inventario */}
      <div className="card shadow-sm border-0 p-4 bg-white rounded-4">
        <h4 className="fw-bold mb-3 text-success">Inventario Actual</h4>
        <div className="table-responsive">
          <table className="table table-hover align-middle">
            <thead className="table-success">
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Categoría</th>
                <th>Precio</th>
                <th>Stock</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {productos.map(p => (
                <tr key={p.id}>
                  <td>{p.id}</td>
                  <td className="fw-bold">{p.nombre}</td>
                  <td><span className="badge bg-secondary">{p.categoria}</span></td>
                  <td>${p.precio?.toLocaleString()}</td>
                  <td>{p.stock} un.</td>
                  <td>
                    <button className="btn btn-danger btn-sm" onClick={() => handleEliminar(p.id)}>Eliminar</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}