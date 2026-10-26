export default function ProductDetailPage({ producto, agregarAlCarrito, volver }) {
  if (!producto) return <main className="container my-5"><p role="status">Producto no disponible.</p><button type="button" className="btn btn-outline-success" onClick={volver}>Volver a productos</button></main>;
  return (
    <main className="container my-5">
      <button type="button" className="btn btn-outline-success mb-4" onClick={volver}>Volver a productos</button>
      <div className="row g-4 align-items-start">
        <div className="col-md-6"><img src={producto.imagenUrl || producto.imagen || '/img/huerto-hogar1.png'} alt={producto.nombre} className="img-fluid rounded w-100" style={{ maxHeight: 480, objectFit: 'cover' }} /></div>
        <div className="col-md-6">
          <p className="text-muted">{producto.categoriaNombre || producto.categoria || 'Sin categoría'}</p>
          <h1 className="text-success fw-bold">{producto.nombre}</h1>
          <p className="lead">{producto.descripcion || 'Consulta la categoría, precio y disponibilidad de este producto.'}</p>
          <p className="fs-3 fw-bold">${Number(producto.precio).toLocaleString('es-CL')}</p>
          <p>Stock disponible: {producto.stock} unidad(es).</p>
          <button type="button" className="btn btn-success btn-lg" onClick={() => agregarAlCarrito(producto)} disabled={!(producto.stock > 0)}>{producto.stock > 0 ? 'Añadir al carrito' : 'Sin stock'}</button>
        </div>
      </div>
    </main>
  );
}
