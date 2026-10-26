export default function CategoriesPage({ categorias, productos, loading, errorProductos, onSeleccionar }) {
  const nombres = [...new Set([...categorias.map((item) => item.nombre), ...productos.map((item) => item.categoriaNombre || item.categoria)].filter(Boolean))];
  return (
    <main className="container my-5">
      <div className="seccion-titulo"><h1>Categorías</h1><p>Elige una categoría para explorar sus productos.</p></div>
      {loading ? <p role="status">Cargando categorías...</p> : errorProductos ? <div className="alert alert-danger" role="alert">{errorProductos}</div> : (
        <div className="row g-4">
          {!nombres.length && <p role="status">Todavía no hay categorías disponibles.</p>}
          {nombres.map((nombre) => {
            const items = productos.filter((item) => (item.categoriaNombre || item.categoria) === nombre);
            const imagen = items[0]?.imagenUrl || items[0]?.imagen;
            return (
              <div className="col-sm-6 col-lg-4" key={nombre}>
                <article className="card h-100 border-0 shadow-sm">
                  {imagen && <img src={imagen} alt="" className="card-img-top" style={{ height: 180, objectFit: 'cover' }} />}
                  <div className="card-body d-flex flex-column">
                    <h2 className="h4 text-success">{nombre}</h2>
                    <p>{items.length} producto(s) disponibles en el catálogo.</p>
                    <button type="button" className="btn btn-outline-success mt-auto" onClick={() => onSeleccionar(nombre)}>Ver productos de {nombre}</button>
                  </div>
                </article>
              </div>
            );
          })}
        </div>
      )}
    </main>
  );
}
