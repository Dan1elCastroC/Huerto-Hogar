import { useState } from 'react';
import MainPage from '../components/MainPage';
import BlogManager from '../components/admin/BlogManager';
import CategoryManager from '../components/admin/CategoryManager';
import ContactManager from '../components/admin/ContactManager';
import CouponManager from '../components/admin/CouponManager';
import OrderManager from '../components/admin/OrderManager';
import ReportManager from '../components/admin/ReportManager';
import UserManager from '../components/admin/UserManager';

const sections = [
  ['productos', 'Productos'],
  ['categorias', 'Categorías'],
  ['cupones', 'Cupones'],
  ['blogs', 'Noticias'],
  ['contactos', 'Contactos'],
  ['usuarios', 'Usuarios'],
  ['pedidos', 'Pedidos'],
  ['reportes', 'Reportes'],
];

export default function AdminPage({ productos, setProductos, categorias, user, token }) {
  const [section, setSection] = useState('productos');

  const renderSection = () => {
    const props = { token };
    switch (section) {
      case 'categorias': return <CategoryManager {...props} />;
      case 'cupones': return <CouponManager {...props} />;
      case 'blogs': return <BlogManager {...props} />;
      case 'contactos': return <ContactManager {...props} />;
      case 'usuarios': return <UserManager {...props} />;
      case 'pedidos': return <OrderManager {...props} />;
      case 'reportes': return <ReportManager {...props} />;
      default: return <MainPage productos={productos} setProductos={setProductos} categorias={categorias} user={user} token={token} />;
    }
  };

  return (
    <main className="admin-layout">
      <aside className="admin-sidebar" aria-label="Secciones de administración">
        <div className="marca">Administración</div>
        {sections.map(([value, label]) => (
          <button key={value} type="button" className={`btn text-start w-100 rounded-0 ${section === value ? 'active' : ''}`} onClick={() => setSection(value)}>{label}</button>
        ))}
      </aside>
      <div className="admin-content">{renderSection()}</div>
    </main>
  );
}
