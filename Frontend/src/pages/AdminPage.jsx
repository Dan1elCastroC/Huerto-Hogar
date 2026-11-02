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
  { id: 'inicio',     label: 'Dashboard',   icon: '🏠' },
  { id: 'pedidos',    label: 'Órdenes',      icon: '📋' },
  { id: 'productos',  label: 'Productos',    icon: '📦' },
  { id: 'categorias', label: 'Categorías',   icon: '🏷️' },
  { id: 'usuarios',   label: 'Usuarios',     icon: '👥' },
  { id: 'reportes',   label: 'Reportes',     icon: '📊' },
  { id: 'blogs',      label: 'Noticias',     icon: '📰' },
  { id: 'cupones',    label: 'Cupones',      icon: '🎟️' },
  { id: 'contactos',  label: 'Contactos',    icon: '✉️' },
  { id: 'perfil',     label: 'Mi perfil',    icon: '👤' },
];

const dashModules = [
  { id: 'inicio',     label: 'Dashboard',   icon: '🏠', desc: 'Visión general de todas las métricas y estadísticas clave del sistema.' },
  { id: 'pedidos',    label: 'Órdenes',      icon: '📋', desc: 'Gestión y seguimiento de todas las órdenes de compra realizadas.' },
  { id: 'productos',  label: 'Productos',    icon: '📦', desc: 'Administrar inventario y detalles de los productos disponibles.' },
  { id: 'categorias', label: 'Categorías',   icon: '🏷️', desc: 'Organizar productos en categorías para facilitar su navegación.' },
  { id: 'usuarios',   label: 'Usuarios',     icon: '👥', desc: 'Gestión de cuentas de usuario y sus roles dentro del sistema.' },
  { id: 'reportes',   label: 'Reportes',     icon: '📊', desc: 'Generación de informes detallados sobre las operaciones del sistema.' },
  { id: 'perfil',     label: 'Perfil',       icon: '👤', desc: 'Administración de la información personal y configuraciones de cuenta.' },
  { id: 'catalogo',   label: 'Tienda',       icon: '🛒', desc: 'Visualiza tu tienda en tiempo real, visualiza los reportes de los usuarios.', externa: true },
];

export default function AdminPage({ productos, setProductos, categorias, setCategorias, user, token, cambiarVista, onLogout, onUserUpdated }) {
  const [section, setSection] = useState('inicio');
  const [resumen, setResumen] = useState(null);
  const [error, setError] = useState('');
  const [sidebarOpen, setSidebarOpen] = useState(true);

  useEffect(() => {
    if (section !== 'inicio') return;
    let activo = true;
    reporteService.resumen(token)
      .then((data) => { if (activo) setResumen(data); })
      .catch((e) => { if (activo) setError(e.message); });
    return () => { activo = false; };
  }, [section, token]);

  const navigate = (id) => {
    if (id === 'catalogo') { cambiarVista?.('catalogo'); return; }
    setSection(id);
    setError('');
  };

  const renderSection = () => {
    const props = { token };
    switch (section) {
      case 'inicio':    return <Dashboard resumen={resumen} error={error} user={user} onNavigate={navigate} />;
      case 'categorias':return <CategoryManager {...props} categorias={categorias} setCategorias={setCategorias} />;
      case 'cupones':   return <CouponManager {...props} />;
      case 'blogs':     return <BlogManager {...props} />;
      case 'contactos': return <ContactManager {...props} />;
      case 'usuarios':  return <UserManager {...props} />;
      case 'pedidos':   return <OrderManager {...props} />;
      case 'reportes':  return <ReportManager {...props} />;
      case 'perfil':    return <ProfilePage user={user} onUserUpdated={onUserUpdated} />;
      default:          return <MainPage productos={productos} setProductos={setProductos} categorias={categorias} user={user} token={token} />;
    }
  };

  const currentLabel = sections.find(s => s.id === section)?.label || 'Panel';

  return (
    <div style={{ display: 'flex', minHeight: '100vh', background: '#f0f2f5' }}>
      {/* ── SIDEBAR ── */}
      <aside style={{
        width: sidebarOpen ? 220 : 0,
        minWidth: sidebarOpen ? 220 : 0,
        background: '#1a1f2e',
        color: '#c5cdd8',
        display: 'flex',
        flexDirection: 'column',
        transition: 'width 0.25s, min-width 0.25s',
        overflow: 'hidden',
        flexShrink: 0,
        position: 'relative',
        zIndex: 10,
      }}>
        {/* Brand */}
        <div style={{ padding: '20px 20px 14px', borderBottom: '1px solid #2d3545', whiteSpace: 'nowrap' }}>
          <div style={{ fontSize: 13, color: '#6b7a99', textTransform: 'uppercase', letterSpacing: 1, marginBottom: 2 }}>Company name</div>
          <div style={{ fontSize: 15, fontWeight: 700, color: '#fff' }}>🌱 Huerto Hogar</div>
        </div>

        {/* Nav */}
        <nav style={{ flex: 1, padding: '10px 0', overflowY: 'auto' }}>
          {sections.map(({ id, label, icon }) => (
            <button
              key={id}
              onClick={() => navigate(id)}
              style={{
                display: 'flex', alignItems: 'center', gap: 10,
                width: '100%', border: 'none', background: 'none',
                color: section === id ? '#fff' : '#8b96a8',
                padding: '10px 20px',
                fontSize: 14,
                fontWeight: section === id ? 600 : 400,
                cursor: 'pointer',
                borderLeft: section === id ? '3px solid #4361ee' : '3px solid transparent',
                background: section === id ? 'rgba(67,97,238,0.12)' : 'none',
                textAlign: 'left', whiteSpace: 'nowrap',
                transition: 'background 0.15s, color 0.15s',
              }}
            >
              <span style={{ fontSize: 16 }}>{icon}</span> {label}
            </button>
          ))}
        </nav>

        {/* Bottom actions */}
        <div style={{ borderTop: '1px solid #2d3545', padding: '10px 0' }}>
          <button onClick={() => cambiarVista?.('catalogo')} style={bottomBtnStyle}>🛒 Tienda</button>
          <button onClick={onLogout} style={{ ...bottomBtnStyle, color: '#e57373' }}>🚪 Cerrar sesión</button>
        </div>
      </aside>

      {/* ── MAIN ── */}
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minWidth: 0 }}>
        {/* Top bar */}
        <header style={{
          background: '#fff', borderBottom: '1px solid #e5e9f0',
          padding: '0 24px', height: 56,
          display: 'flex', alignItems: 'center', gap: 12,
          position: 'sticky', top: 0, zIndex: 9,
        }}>
          <button
            onClick={() => setSidebarOpen(v => !v)}
            style={{ background: 'none', border: 'none', fontSize: 20, cursor: 'pointer', padding: '0 4px', color: '#555' }}
            title="Menú"
          >☰</button>
          <span style={{ fontSize: 15, fontWeight: 600, color: '#1a1f2e' }}>{currentLabel}</span>
          <div style={{ marginLeft: 'auto', display: 'flex', alignItems: 'center', gap: 10 }}>
            <span style={{ fontSize: 13, color: '#6b7a99' }}>
              {user?.nombre || user?.correo || 'Administrador'}
            </span>
            <div style={{
              width: 34, height: 34, borderRadius: '50%',
              background: 'linear-gradient(135deg,#4361ee,#3a0ca3)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              color: '#fff', fontWeight: 700, fontSize: 14,
            }}>
              {(user?.nombre || 'A')[0].toUpperCase()}
            </div>
          </div>
        </header>

        {/* Content */}
        <main style={{ flex: 1, padding: 24, overflowY: 'auto' }}>
          {renderSection()}
        </main>
      </div>
    </div>
  );
}

/* ──────────────── DASHBOARD COMPONENT ──────────────── */
function Dashboard({ resumen, error, user, onNavigate }) {
  const statCards = [
    { label: 'Compras',   value: resumen?.totalPedidos   ?? '—', sub: 'Probabilidad de aumento: 20%', color: '#4361ee', icon: '🛒' },
    { label: 'Productos', value: resumen?.totalProductos ?? '—', sub: 'Inventario actual: 500',        color: '#2ec4b6', icon: '📦' },
    { label: 'Usuarios',  value: resumen?.totalUsuarios  ?? '—', sub: 'Nuevos usuarios este mes: 128', color: '#f4a261', icon: '👥' },
  ];

  return (
    <div>
      <h1 style={{ fontSize: 22, fontWeight: 700, color: '#1a1f2e', marginBottom: 4 }}>Dashboard</h1>
      <p style={{ fontSize: 13, color: '#6b7a99', marginBottom: 24 }}>Resumen de las actividades diarias</p>

      {error && <div className="alert alert-danger mb-4">{error}</div>}

      {/* Stat cards */}
      <div className="row g-3 mb-4">
        {statCards.map(({ label, value, sub, color, icon }) => (
          <div className="col-md-4" key={label}>
            <div style={{
              background: color, borderRadius: 12, padding: '20px 24px',
              color: '#fff', position: 'relative', overflow: 'hidden',
            }}>
              <div style={{ fontSize: 28, marginBottom: 4 }}>{icon}</div>
              <div style={{ fontSize: 13, opacity: 0.85, marginBottom: 2 }}>{label}</div>
              <div style={{ fontSize: 36, fontWeight: 800, lineHeight: 1.1 }}>
                {resumen ? value : <span style={{ fontSize: 14, opacity: 0.7 }}>Cargando…</span>}
              </div>
              <div style={{ fontSize: 11, opacity: 0.8, marginTop: 6 }}>{sub}</div>
              {/* decorative circle */}
              <div style={{
                position: 'absolute', right: -20, top: -20,
                width: 100, height: 100, borderRadius: '50%',
                background: 'rgba(255,255,255,0.1)',
              }} />
            </div>
          </div>
        ))}
      </div>

      {/* Module grid */}
      <div className="row g-3">
        {dashModules.map(({ id, label, icon, desc }) => (
          <div className="col-sm-6 col-lg-3" key={id}>
            <button
              onClick={() => onNavigate(id)}
              style={{
                width: '100%', border: 'none', textAlign: 'left',
                background: '#fff', borderRadius: 12, padding: '20px',
                cursor: 'pointer', transition: 'box-shadow 0.2s, transform 0.2s',
                boxShadow: '0 1px 4px rgba(0,0,0,0.07)',
              }}
              onMouseEnter={e => { e.currentTarget.style.boxShadow='0 6px 20px rgba(0,0,0,0.12)'; e.currentTarget.style.transform='translateY(-3px)'; }}
              onMouseLeave={e => { e.currentTarget.style.boxShadow='0 1px 4px rgba(0,0,0,0.07)'; e.currentTarget.style.transform='none'; }}
            >
              <div style={{ fontSize: 28, marginBottom: 10 }}>{icon}</div>
              <div style={{ fontSize: 14, fontWeight: 700, color: '#1a1f2e', marginBottom: 4 }}>{label}</div>
              <div style={{ fontSize: 12, color: '#8b96a8', lineHeight: 1.5 }}>{desc}</div>
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}

const bottomBtnStyle = {
  display: 'flex', alignItems: 'center', gap: 10,
  width: '100%', border: 'none', background: 'none',
  color: '#8b96a8', padding: '10px 20px',
  fontSize: 14, cursor: 'pointer', textAlign: 'left',
  whiteSpace: 'nowrap',
};
