import { useEffect, useState } from 'react';
import MainPage from '../components/MainPage';
import BlogManager from '../components/admin/BlogManager';
import CategoryManager from '../components/admin/CategoryManager';
import ContactManager from '../components/admin/ContactManager';
import CouponManager from '../components/admin/CouponManager';
import OrderManager from '../components/admin/OrderManager';
import ReportManager from '../components/admin/ReportManager';
import UserManager from '../components/admin/UserManager';
import ProfilePage from './ProfilePage';
import { reporteService } from '../services/reporteService';

const sections = [
  ['inicio', 'Inicio'],
  ['productos', 'Productos'],
  ['categorias', 'Categorías'],
  ['cupones', 'Cupones'],
  ['blogs', 'Noticias'],
  ['contactos', 'Contactos'],
  ['usuarios', 'Usuarios'],
  ['pedidos', 'Pedidos'],
  ['reportes', 'Reportes'],
  ['perfil', 'Mi perfil'],
];

export default function AdminPage({ productos, setProductos, categorias, setCategorias, user, token, cambiarVista, onLogout, onUserUpdated }) {
  const [section, setSection] = useState('inicio');
  const [resumen, setResumen] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    if (section !== 'inicio') return;
    let activo = true;
    reporteService.resumen(token).then((data) => { if (activo) setResumen(data); })
      .catch((requestError) => { if (activo) setError(requestError.message); });
    return () => { activo = false; };
  }, [section, token]);

  const renderSection = () => {
    const props = { token };
    switch (section) {
      case 'inicio': return <section><h1 className="h3 text-success fw-bold">Panel de administración</h1><p>Bienvenido, {user?.nombre || user?.correo || 'Administrador'}.</p>{error && <div className="alert alert-danger" role="alert">{error}</div>}{!resumen && !error && <p role="status">Cargando indicadores...</p>}{resumen && <div className="row g-3">{[['Compras', resumen.totalPedidos], ['Productos', resumen.totalProductos], ['Usuarios', resumen.totalUsuarios]].map(([label, value]) => <div className="col-md-4" key={label}><div className="card p-4"><span>{label}</span><strong className="display-6 text-success">{value}</strong></div></div>)}</div>}</section>;
      case 'categorias': return <CategoryManager {...props} categorias={categorias} setCategorias={setCategorias} />;
      case 'cupones': return <CouponManager {...props} />;
      case 'blogs': return <BlogManager {...props} />;
      case 'contactos': return <ContactManager {...props} />;
      case 'usuarios': return <UserManager {...props} />;
      case 'pedidos': return <OrderManager {...props} />;
      case 'reportes': return <ReportManager {...props} />;
      case 'perfil': return <ProfilePage user={user} onUserUpdated={onUserUpdated} />;
      default: return <MainPage productos={productos} setProductos={setProductos} categorias={categorias} user={user} token={token} />;
    }
  };

  return (
    <div className="admin-layout">
      <aside className="admin-sidebar" aria-label="Secciones de administración">
        <div className="marca">Administración</div>
        {sections.map(([value, label]) => (
          <button key={value} type="button" aria-current={section === value ? 'page' : undefined} className={`btn text-start w-100 rounded-0 ${section === value ? 'active' : ''}`} onClick={() => { setSection(value); setError(''); }}>{label}</button>
        ))}
        <hr />
        <button type="button" className="btn text-start w-100" onClick={() => cambiarVista?.('catalogo')}>Tienda</button>
        <button type="button" className="btn text-start w-100" onClick={onLogout}>Cerrar sesión</button>
      </aside>
      <div className="admin-content">{renderSection()}</div>
    </div>
  );
}
