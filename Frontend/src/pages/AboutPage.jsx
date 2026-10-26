export default function AboutPage({ cambiarVista }) {
  return (
    <main className="container my-5">
      <div className="row g-4 align-items-center">
        <div className="col-lg-6">
          <h1 className="text-success fw-bold">Nosotros</h1>
          <p className="lead">Huerto Hogar conecta el cuidado del huerto con una vida más sostenible.</p>
          <p>En nuestra tienda encontrarás frutas, verduras, hierbas y productos para cultivar en casa. Queremos acercar el campo a tu hogar y ayudarte a elegir lo que necesitas para tu huerto.</p>
          <p>Revisa el catálogo por categorías o visita nuestras noticias para conocer consejos de cultivo.</p>
          <button type="button" className="btn btn-success" onClick={() => cambiarVista('catalogo')}>Explorar productos</button>
        </div>
        <div className="col-lg-6"><img src="/img/huerto-hogar1.png" alt="Cultivo y productos de Huerto Hogar" className="img-fluid rounded" /></div>
      </div>
    </main>
  );
}
