import { fireEvent, render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import Navbar from './Navbar';

const setup = (props = {}) => {
  const cambiarVista = vi.fn();
  const onLogout = vi.fn();
  render(<Navbar vista="landing" cambiarVista={cambiarVista} carritoCount={0} user={null} onLogout={onLogout} {...props} />);
  return { cambiarVista, onLogout };
};

describe('Navbar', () => {
  it('visitante: muestra enlaces públicos, login/registro y oculta zona de usuario', () => {
    setup();
    for (const name of ['Home', 'Productos', 'Categorías', 'Ofertas', 'Nosotros', 'Noticias', 'Contacto', 'Iniciar sesión', 'Registrarse']) {
      expect(screen.getByRole('link', { name })).toBeInTheDocument();
    }
    expect(screen.queryByRole('link', { name: 'Mi perfil' })).not.toBeInTheDocument();
    expect(screen.queryByRole('link', { name: 'Cerrar sesión' })).not.toBeInTheDocument();
    expect(screen.queryByRole('link', { name: 'Panel principal' })).not.toBeInTheDocument();
  });

  it('marca como activo el enlace de la vista actual', () => {
    setup({ vista: 'blog' });
    expect(screen.getByRole('link', { name: 'Noticias' })).toHaveClass('active');
    expect(screen.getByRole('link', { name: 'Home' })).not.toHaveClass('active');
  });

  it.each([
    ['Home', 'landing'], ['Productos', 'catalogo'], ['Categorías', 'categorias'], ['Ofertas', 'ofertas'],
    ['Nosotros', 'nosotros'], ['Noticias', 'blog'], ['Contacto', 'contacto'],
    ['Iniciar sesión', 'login'], ['Registrarse', 'registro'], [/Carrito/, 'carrito'], [/HUERTO HOGAR/, 'landing'],
  ])('al hacer clic en %s navega a %s', (name, destino) => {
    const { cambiarVista } = setup();
    fireEvent.click(screen.getByRole('link', { name }));
    expect(cambiarVista).toHaveBeenCalledWith(destino);
  });

  it('cliente autenticado: muestra nombre completo, perfil, pedidos y cierra sesión', () => {
    const { cambiarVista, onLogout } = setup({ user: { nombre: 'Ana', apellidos: 'Pérez', correo: 'a@x.cl', rol: 'CLIENTE' } });
    expect(screen.getByText('Ana Pérez')).toBeInTheDocument();
    expect(screen.queryByRole('link', { name: 'Iniciar sesión' })).not.toBeInTheDocument();
    expect(screen.queryByRole('link', { name: 'Panel principal' })).not.toBeInTheDocument();
    fireEvent.click(screen.getByRole('link', { name: 'Mi perfil' }));
    fireEvent.click(screen.getByRole('link', { name: 'Mis pedidos' }));
    expect(cambiarVista).toHaveBeenNthCalledWith(1, 'perfil');
    expect(cambiarVista).toHaveBeenNthCalledWith(2, 'pedidos');
    fireEvent.click(screen.getByRole('link', { name: 'Cerrar sesión' }));
    expect(onLogout).toHaveBeenCalledTimes(1);
  });

  it('usa el correo cuando el usuario no tiene nombre', () => {
    setup({ user: { correo: 'solo@correo.cl' } });
    expect(screen.getByText('solo@correo.cl')).toBeInTheDocument();
  });

  it('administrador: muestra y navega al panel principal', () => {
    const { cambiarVista } = setup({ user: { nombre: 'Root', rol: 'ADMINISTRADOR' }, vista: 'main' });
    const panel = screen.getByRole('link', { name: 'Panel principal' });
    expect(panel).toHaveClass('active');
    fireEvent.click(panel);
    expect(cambiarVista).toHaveBeenCalledWith('main');
  });

  it('muestra la cantidad del carrito en la insignia', () => {
    setup({ carritoCount: 7 });
    expect(screen.getByRole('link', { name: /Carrito 7/ })).toBeInTheDocument();
  });

  it('el botón móvil abre y cierra el menú, y navegar lo cierra', () => {
    setup();
    const toggle = screen.getByRole('button', { name: 'Abrir menú' });
    const menu = document.getElementById('navbarNav');
    expect(toggle).toHaveAttribute('aria-expanded', 'false');
    expect(menu).not.toHaveClass('show');
    fireEvent.click(toggle);
    expect(toggle).toHaveAttribute('aria-expanded', 'true');
    expect(menu).toHaveClass('show');
    fireEvent.click(screen.getByRole('link', { name: 'Productos' }));
    expect(menu).not.toHaveClass('show');
    fireEvent.click(toggle);
    fireEvent.click(toggle);
    expect(menu).not.toHaveClass('show');
  });

  it('cerrar sesión también cierra el menú móvil', () => {
    setup({ user: { nombre: 'Ana' } });
    fireEvent.click(screen.getByRole('button', { name: 'Abrir menú' }));
    fireEvent.click(screen.getByRole('link', { name: 'Cerrar sesión' }));
    expect(document.getElementById('navbarNav')).not.toHaveClass('show');
  });
});
