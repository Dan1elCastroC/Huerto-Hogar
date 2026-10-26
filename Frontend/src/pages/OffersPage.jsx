import { useEffect, useState } from 'react';
import ProductList from '../components/ProductList';
import { cuponService } from '../services/cuponService';

export default function OffersPage({ productos, loading, errorProductos, agregarAlCarrito, onVerDetalle }) {
  const [cupon, setCupon] = useState(null);
  const [consultando, setConsultando] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let vigente = true;
    cuponService.validate('BIENVENIDA10')
      .then((data) => {
        const hoy = new Date();
        const fecha = `${hoy.getFullYear()}-${String(hoy.getMonth() + 1).padStart(2, '0')}-${String(hoy.getDate()).padStart(2, '0')}`;
        if (!data.activo || !(Number(data.porcentaje) > 0 && Number(data.porcentaje) <= 100) || (data.fechaExpiracion && data.fechaExpiracion < fecha)) {
          throw new Error('El cupón no está vigente.');
        }
        if (vigente) setCupon(data);
      })
      .catch((requestError) => { if (vigente) setError(requestError.message || 'No se pudieron consultar las promociones.'); })
      .finally(() => { if (vigente) setConsultando(false); });
    return () => { vigente = false; };
  }, []);

  return (
    <main className="my-5">
      <div className="container seccion-titulo"><h1>Ofertas</h1><p>Promociones verificadas con nuestra tienda.</p></div>
      {consultando ? <p className="container" role="status">Consultando promociones vigentes...</p> : !cupon ? (
        <div className="container"><p role="status">No hay promociones vigentes.</p><p className="text-muted">{error}</p></div>
      ) : <>
        <div className="container"><div className="alert alert-success">
          <strong>{Number(cupon.porcentaje)}% de descuento con {cupon.codigo}.</strong>
          <p className="mb-0">Ingresa {cupon.codigo} en el carrito para aplicar el descuento. Los precios con cupón son referenciales; el total se valida al confirmar la compra.</p>
          {cupon.fechaExpiracion && <small>Vigencia hasta {cupon.fechaExpiracion}.</small>}
        </div></div>
        <ProductList productos={productos} agregarAlCarrito={agregarAlCarrito} loading={loading} errorProductos={errorProductos} onVerDetalle={onVerDetalle} cupon={cupon} titulo="Productos con cupón" />
      </>}
    </main>
  );
}
