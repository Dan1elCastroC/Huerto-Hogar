import { fireEvent, render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import AdminPage from './AdminPage';

vi.mock('../components/MainPage', () => ({ default: () => <div>Inventario de productos</div> }));
vi.mock('../components/admin/BlogManager', () => ({ default: () => <div>Gestor de noticias</div> }));
vi.mock('../components/admin/CategoryManager', () => ({ default: () => <div>Gestor de categorías</div> }));
vi.mock('../components/admin/ContactManager', () => ({ default: () => <div>Gestor de contactos</div> }));
vi.mock('../components/admin/CouponManager', () => ({ default: () => <div>Gestor de cupones</div> }));
vi.mock('../components/admin/OrderManager', () => ({ default: () => <div>Gestor de pedidos</div> }));
vi.mock('../components/admin/ReportManager', () => ({ default: () => <div>Gestor de reportes</div> }));
vi.mock('../components/admin/UserManager', () => ({ default: () => <div>Gestor de usuarios</div> }));

describe('AdminPage', () => {
  it('cambia entre las secciones administrativas', () => {
    render(<AdminPage productos={[]} setProductos={() => {}} categorias={[]} user={null} token="token-demo" />);

    expect(screen.getByText('Inventario de productos')).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Categorías' }));
    expect(screen.getByText('Gestor de categorías')).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Reportes' }));
    expect(screen.getByText('Gestor de reportes')).toBeInTheDocument();
  });
});
