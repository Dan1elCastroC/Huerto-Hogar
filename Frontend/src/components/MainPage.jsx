import React, { useState } from 'react';
import { productoService } from '../services/api';

const initialProduct = {
  codigo: '',
  nombre: '',
  descripcion: '',
  precio: '',
  stock: '',
  stockCritico: '',
  imagenUrl: '',
  categoriaId: '',
};

export default function MainPage({ productos, setProductos, categorias, user, token }) {
  const [nuevoProd, setNuevoProd] = useState(initialProduct);
  const [mensaje, setMensaje] = useState(null);
  const [guardando, setGuardando] = useState(false);

  const actualizarCampo = (event) => {
    const { name, value } = event.target;
    setNuevoProd((current) => ({ ...current, [name]: value }));
  };

  const handleCrearProducto = async (event) => {
    event.preventDefault();
    setMensaje(null);
    setGuardando(true);
    try {
      const payload = {
        ...nuevoProd,
        precio: Number(nuevoProd.precio),
        stock: Number(nuevoProd.stock),
        stockCritico: nuevoProd.stockCritico === '' ? null : Number(nuevoProd.stockCritico),
        categoriaId: Number(nuevoProd.categoriaId),
      };
      const creado = await productoService.create(payload, token);
      setProductos((current) => [...current, creado]);
      setMensaje({ tipo: 'success', texto: 'Producto registrado correctamente en la base de datos.' });
      setNuevoProd(initialProduct);
    } catch (error) {
      setMensaje({ tipo: 'danger', texto: error.message || 'No se pudo registrar el producto.' });
    } finally {
      setGuardando(false);
    }
  };

  const handleEliminar = async (id) => {
    if (!window.confirm('¿Seguro que deseas eliminar este producto?')) return;
    try {
      await productoService.delete(id, token);
      setProductos((current) => current.filter((producto) => producto.id !== id));
      setMensaje({ tipo: 'success', texto: 'Producto eliminado correctamente.' });
    } catch (error) {
      setMensaje({ tipo: 'danger', texto: error.message || 'No se pudo eliminar el producto.' });
    }
  };

  return (
    <div className="container my-5">
      <div className="row mb-4">
        <div className="col">
          <h2 className="text-success fw-bold">📊 Panel principal de administración</h2>
          <p className="text-muted">Bienvenido, <strong>{user?.correo || 'Administrador'}</strong>. Gestiona el inventario conectado al backend.</p>
        </div>
      </div>

      {mensaje && <div className={`alert alert-${mensaje.tipo}`} role="alert">{mensaje.texto}</div>}

      <div className="card shadow-sm border-0 p-4 mb-5 bg-white rounded-4">
        <h3 className="h4 fw-bold mb-3 text-success">Agregar nuevo producto</h3>
        <form onSubmit={handleCrearProducto} className="row g-3">
          <div className="col-md-2"><label htmlFor="producto-codigo" className="form-label">Código</label><input id="producto-codigo" name="codigo" type="text" className="form-control" value={nuevoProd.codigo} onChange={actualizarCampo} required /></div>
          <div className="col-md-4"><label htmlFor="producto-nombre" className="form-label">Nombre</label><input id="producto-nombre" name="nombre" type="text" className="form-control" value={nuevoProd.nombre} onChange={actualizarCampo} required /></div>
          <div className="col-md-3"><label htmlFor="producto-categoria" className="form-label">Categoría</label><select id="producto-categoria" name="categoriaId" className="form-select" value={nuevoProd.categoriaId} onChange={actualizarCampo} required><option value="">Selecciona...</option>{categorias.map((categoria) => <option key={categoria.id} value={categoria.id}>{categoria.nombre}</option>)}</select></div>
          <div className="col-md-3"><label htmlFor="producto-precio" className="form-label">Precio</label><input id="producto-precio" name="precio" type="number" min="0" className="form-control" value={nuevoProd.precio} onChange={actualizarCampo} required /></div>
          <div className="col-md-3"><label htmlFor="producto-stock" className="form-label">Stock</label><input id="producto-stock" name="stock" type="number" min="0" className="form-control" value={nuevoProd.stock} onChange={actualizarCampo} required /></div>
          <div className="col-md-3"><label htmlFor="producto-stock-critico" className="form-label">Stock crítico</label><input id="producto-stock-critico" name="stockCritico" type="number" min="0" className="form-control" value={nuevoProd.stockCritico} onChange={actualizarCampo} /></div>
          <div className="col-md-6"><label htmlFor="producto-imagen" className="form-label">URL de imagen</label><input id="producto-imagen" name="imagenUrl" type="url" className="form-control" value={nuevoProd.imagenUrl} onChange={actualizarCampo} /></div>
          <div className="col-12"><label htmlFor="producto-descripcion" className="form-label">Descripción</label><textarea id="producto-descripcion" name="descripcion" className="form-control" rows="2" value={nuevoProd.descripcion} onChange={actualizarCampo} /></div>
          <div className="col-12"><button type="submit" className="btn btn-success fw-bold px-4" disabled={guardando}>{guardando ? 'Guardando...' : 'Guardar en base de datos'}</button></div>
        </form>
      </div>

      <div className="card shadow-sm border-0 p-4 bg-white rounded-4">
        <h3 className="h4 fw-bold mb-3 text-success">Inventario actual</h3>
        <div className="table-responsive">
          <table className="table table-hover align-middle">
            <thead className="table-success"><tr><th>ID</th><th>Nombre</th><th>Categoría</th><th>Precio</th><th>Stock</th><th>Acciones</th></tr></thead>
            <tbody>
              {productos.map((producto) => (
                <tr key={producto.id}>
                  <td>{producto.id}</td>
                  <td className="fw-bold">{producto.nombre}</td>
                  <td><span className="badge bg-secondary">{producto.categoriaNombre || producto.categoria || 'Sin categoría'}</span></td>
                  <td>${Number(producto.precio).toLocaleString()}</td>
                  <td>{producto.stock} un.</td>
                  <td><button type="button" className="btn btn-danger btn-sm" onClick={() => handleEliminar(producto.id)}>Eliminar</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
