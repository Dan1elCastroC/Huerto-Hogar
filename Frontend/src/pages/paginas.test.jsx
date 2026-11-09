import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import AboutPage from './AboutPage';
import BlogDetailPage from './BlogDetailPage';
import BlogPage from './BlogPage';
import CartPage from './CartPage';
import CatalogPage from './CatalogPage';
import CategoriesPage from './CategoriesPage';
import ContactPage from './ContactPage';
import HomePage from './HomePage';
import LoginPage from './LoginPage';
import OffersPage from './OffersPage';
import OrderHistoryPage from './OrderHistoryPage';
import ProductDetailPage from './ProductDetailPage';
import ProfilePage from './ProfilePage';
import RegisterPage from './RegisterPage';
import { blogService } from '../services/blogService';
import { contactoService } from '../services/contactoService';
import { cuponService } from '../services/cuponService';
import { pedidoService } from '../services/pedidoService';
import { usuarioService } from '../services/usuarioService';
import { authService } from '../services/authService';

vi.mock('../services/blogService', () => ({ blogService: { getAll: vi.fn() } }));
vi.mock('../services/contactoService', () => ({ contactoService: { create: vi.fn() } }));
vi.mock('../services/cuponService', () => ({ cuponService: { validate: vi.fn() } }));
vi.mock('../services/pedidoService', () => ({ pedidoService: { historial: vi.fn() } }));
vi.mock('../services/usuarioService', () => ({ usuarioService: { getProfile: vi.fn(), updateProfile: vi.fn(), changePassword: vi.fn() } }));
vi.mock('../services/authService', () => ({ authService: { registro: vi.fn(), login: vi.fn(), getPerfil: vi.fn() } }));

const productos = [
  { id: 1, nombre: 'Manzanas', categoriaNombre: 'Frutas', precio: 2000, stock: 4, imagenUrl: '/m.png' },
  { id: 2, nombre: 'Tomates', categoria: 'Verduras', precio: 1500, stock: 0 },
];

beforeEach(() => {
  blogService.getAll.mockResolvedValue([]);
});

describe('AboutPage', () => {
  it('muestra información y lleva al catálogo', () => {
    const cambiarVista = vi.fn();
    render(<AboutPage cambiarVista={cambiarVista} />);
    expect(screen.getByRole('heading', { name: 'Nosotros' })).toBeInTheDocument();
    expect(screen.getByAltText(/Cultivo y productos/)).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Explorar productos' }));
    expect(cambiarVista).toHaveBeenCalledWith('catalogo');
  });
});

describe('BlogDetailPage', () => {
  it('muestra título, imagen y contenido, y permite volver', () => {
    const volver = vi.fn();
    render(<BlogDetailPage blog={{ titulo: 'Compost', descripcion: 'Paso 1', imagenUrl: '/c.png' }} volver={volver} />);
    expect(screen.getByRole('heading', { level: 1, name: 'Compost' })).toBeInTheDocument();
    expect(screen.getByText('Paso 1')).toBeInTheDocument();
    expect(document.querySelector('img')).toHaveAttribute('src', '/c.png');
    fireEvent.click(screen.getByRole('button', { name: 'Volver a noticias' }));
    expect(volver).toHaveBeenCalled();
  });

  it('omite la imagen si no existe y avisa si no hay noticia', () => {
    const { rerender } = render(<BlogDetailPage blog={{ titulo: 'T', descripcion: 'D' }} volver={vi.fn()} />);
    expect(document.querySelector('img')).toBeNull();
    rerender(<BlogDetailPage blog={null} volver={vi.fn()} />);
    expect(screen.getByRole('status')).toHaveTextContent('Noticia no disponible.');
  });
});

describe('BlogPage', () => {
  it('muestra carga y luego las noticias; abre el detalle', async () => {
    blogService.getAll.mockResolvedValue([{ id: 1, titulo: 'Riego', descripcion: 'Temprano' }]);
    const onVerDetalle = vi.fn();
    render(<BlogPage onVerDetalle={onVerDetalle} />);
    expect(screen.getByText('Cargando noticias...')).toBeInTheDocument();
    fireEvent.click(await screen.findByRole('button', { name: 'Leer Riego' }));
    expect(onVerDetalle).toHaveBeenCalledWith({ id: 1, titulo: 'Riego', descripcion: 'Temprano' });
  });

  it('muestra estado vacío', async () => {
    render(<BlogPage />);
    expect(await screen.findByText('Todavía no hay noticias publicadas.')).toBeInTheDocument();
  });

  it('muestra error de carga y usa mensaje por defecto sin message', async () => {
    blogService.getAll.mockRejectedValueOnce(new Error('Falla'));
    const { unmount } = render(<BlogPage />);
    expect(await screen.findByText('Falla')).toBeInTheDocument();
    unmount();
    blogService.getAll.mockRejectedValueOnce({});
    render(<BlogPage />);
    expect(await screen.findByText('No se pudieron cargar las noticias.')).toBeInTheDocument();
  });
});

describe('páginas que delegan en componentes (Cart, Catalog, Home, Login, Register)', () => {
  it('CartPage muestra carrito vacío y navega al catálogo', () => {
    const cambiarVista = vi.fn();
    render(<CartPage carrito={[]} user={null} cambiarVista={cambiarVista} />);
    expect(screen.getByText('Tu carrito está vacío')).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Ver productos' }));
    expect(cambiarVista).toHaveBeenCalledWith('catalogo');
  });

  it('CartPage con productos permite cambiar cantidad y eliminar', () => {
    const cambiarCantidad = vi.fn();
    const eliminarDelCarrito = vi.fn();
    const carrito = [{ id: 1, nombre: 'Kit', precio: 5000, cantidad: 1, stock: 3 }];
    render(<CartPage carrito={carrito} user={null} cambiarVista={vi.fn()} cambiarCantidad={cambiarCantidad} eliminarDelCarrito={eliminarDelCarrito} vaciarCarrito={vi.fn()} />);
    fireEvent.click(screen.getByRole('button', { name: 'Aumentar cantidad de Kit' }));
    expect(cambiarCantidad).toHaveBeenCalledWith(1, 2);
    fireEvent.click(screen.getByRole('button', { name: 'Eliminar Kit' }));
    expect(eliminarDelCarrito).toHaveBeenCalledWith(1);
  });

  it('CatalogPage lista productos y agrega al carrito', () => {
    const agregarAlCarrito = vi.fn();
    render(<CatalogPage productos={productos} categorias={[]} agregarAlCarrito={agregarAlCarrito} />);
    fireEvent.click(screen.getByRole('button', { name: 'Añadir al Carrito' }));
    expect(agregarAlCarrito).toHaveBeenCalledWith(productos[0]);
  });

  it('HomePage renderiza la portada', () => {
    render(<HomePage cambiarVista={vi.fn()} productos={[]} />);
    expect(screen.getByRole('heading', { name: 'Del campo a tu hogar' })).toBeInTheDocument();
  });

  it('LoginPage inicia sesión y avisa al padre', async () => {
    authService.login.mockResolvedValue({ token: 't', rol: 'CLIENTE' });
    authService.getPerfil.mockResolvedValue({ nombre: 'Ana', correo: 'a@x.cl' });
    const onLoginSuccess = vi.fn();
    render(<LoginPage onLoginSuccess={onLoginSuccess} cambiarVista={vi.fn()} />);
    fireEvent.change(screen.getByLabelText('Correo electrónico'), { target: { value: 'a@x.cl' } });
    fireEvent.change(screen.getByLabelText('Contraseña'), { target: { value: 'clave1' } });
    fireEvent.click(screen.getByRole('button', { name: 'Ingresar' }));
    await waitFor(() => expect(onLoginSuccess).toHaveBeenCalled());
  });

  it('RegisterPage muestra el formulario de registro', () => {
    render(<RegisterPage cambiarVista={vi.fn()} />);
    expect(screen.getByLabelText('RUN')).toBeInTheDocument();
    expect(screen.getByLabelText('Repetir contraseña')).toBeInTheDocument();
  });
});

describe('CategoriesPage', () => {
  const base = { categorias: [{ id: 1, nombre: 'Frutas' }, { id: 2, nombre: 'Semillas' }], productos, loading: false, errorProductos: '', onSeleccionar: vi.fn() };

  it('une categorías del backend y de productos, sin duplicados, con conteo', () => {
    render(<CategoriesPage {...base} />);
    expect(screen.getByRole('heading', { name: 'Frutas' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: 'Semillas' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: 'Verduras' })).toBeInTheDocument();
    expect(screen.getAllByRole('heading', { level: 2 })).toHaveLength(3);
    expect(screen.getAllByText('1 producto(s) disponibles en el catálogo.')).toHaveLength(2);
    expect(screen.getByText('0 producto(s) disponibles en el catálogo.')).toBeInTheDocument();
  });

  it('selecciona una categoría', () => {
    const onSeleccionar = vi.fn();
    render(<CategoriesPage {...base} onSeleccionar={onSeleccionar} />);
    fireEvent.click(screen.getByRole('button', { name: 'Ver productos de Semillas' }));
    expect(onSeleccionar).toHaveBeenCalledWith('Semillas');
  });

  it('estados de carga, error y vacío', () => {
    const { rerender } = render(<CategoriesPage {...base} loading />);
    expect(screen.getByRole('status')).toHaveTextContent('Cargando categorías...');
    rerender(<CategoriesPage {...base} errorProductos="Backend caído" />);
    expect(screen.getByRole('alert')).toHaveTextContent('Backend caído');
    rerender(<CategoriesPage {...base} categorias={[]} productos={[]} />);
    expect(screen.getByRole('status')).toHaveTextContent('Todavía no hay categorías disponibles.');
  });
});

describe('ProductDetailPage', () => {
  it('muestra detalle, agrega al carrito y vuelve', () => {
    const agregarAlCarrito = vi.fn();
    const volver = vi.fn();
    render(<ProductDetailPage producto={{ ...productos[0], descripcion: 'Dulces' }} agregarAlCarrito={agregarAlCarrito} volver={volver} />);
    expect(screen.getByRole('heading', { name: 'Manzanas' })).toBeInTheDocument();
    expect(screen.getByText('Dulces')).toBeInTheDocument();
    expect(screen.getByText('$2.000')).toBeInTheDocument();
    expect(screen.getByText('Stock disponible: 4 unidad(es).')).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Añadir al carrito' }));
    expect(agregarAlCarrito).toHaveBeenCalledWith(expect.objectContaining({ id: 1 }));
    fireEvent.click(screen.getByRole('button', { name: 'Volver a productos' }));
    expect(volver).toHaveBeenCalled();
  });

  it('deshabilita sin stock y usa textos y imagen por defecto', () => {
    render(<ProductDetailPage producto={{ id: 9, nombre: 'Agotado', precio: 100, stock: 0 }} agregarAlCarrito={vi.fn()} volver={vi.fn()} />);
    expect(screen.getByRole('button', { name: 'Sin stock' })).toBeDisabled();
    expect(screen.getByText('Sin categoría')).toBeInTheDocument();
    expect(screen.getByText(/Consulta la categoría/)).toBeInTheDocument();
    expect(screen.getByAltText('Agotado')).toHaveAttribute('src', '/img/huerto-hogar1.png');
  });

  it('avisa si no hay producto', () => {
    const volver = vi.fn();
    render(<ProductDetailPage producto={null} agregarAlCarrito={vi.fn()} volver={volver} />);
    expect(screen.getByRole('status')).toHaveTextContent('Producto no disponible.');
    fireEvent.click(screen.getByRole('button', { name: 'Volver a productos' }));
    expect(volver).toHaveBeenCalled();
  });
});

describe('OffersPage', () => {
  const props = { productos, loading: false, errorProductos: '', agregarAlCarrito: vi.fn(), onVerDetalle: vi.fn() };

  it('muestra consulta y luego la oferta vigente con precios con cupón', async () => {
    cuponService.validate.mockResolvedValue({ codigo: 'BIENVENIDA10', porcentaje: 10, activo: true, fechaExpiracion: '2099-12-31' });
    render(<OffersPage {...props} />);
    expect(screen.getByRole('status')).toHaveTextContent('Consultando promociones vigentes...');
    expect(await screen.findByText('10% de descuento con BIENVENIDA10.')).toBeInTheDocument();
    expect(screen.getByText('Vigencia hasta 2099-12-31.')).toBeInTheDocument();
    expect(screen.getByText('Precio con cupón: $1.800')).toBeInTheDocument();
  });

  it('sin fecha de expiración no muestra vigencia', async () => {
    cuponService.validate.mockResolvedValue({ codigo: 'BIENVENIDA10', porcentaje: 10, activo: true });
    render(<OffersPage {...props} />);
    await screen.findByText('10% de descuento con BIENVENIDA10.');
    expect(screen.queryByText(/Vigencia hasta/)).not.toBeInTheDocument();
  });

  it.each([
    ['inactivo', { codigo: 'B', porcentaje: 10, activo: false }],
    ['porcentaje inválido', { codigo: 'B', porcentaje: 150, activo: true }],
    ['vencido', { codigo: 'B', porcentaje: 10, activo: true, fechaExpiracion: '2000-01-01' }],
  ])('no muestra ofertas con cupón %s', async (_n, cupon) => {
    cuponService.validate.mockResolvedValue(cupon);
    render(<OffersPage {...props} />);
    await waitFor(() => expect(screen.getByRole('status')).toHaveTextContent('No hay promociones vigentes.'));
    expect(screen.getByText('El cupón no está vigente.')).toBeInTheDocument();
  });

  it('muestra el error del servicio o el mensaje por defecto', async () => {
    cuponService.validate.mockRejectedValueOnce(new Error('Cupón no existe'));
    const { unmount } = render(<OffersPage {...props} />);
    expect(await screen.findByText('Cupón no existe')).toBeInTheDocument();
    unmount();
    cuponService.validate.mockRejectedValueOnce({});
    render(<OffersPage {...props} />);
    expect(await screen.findByText('No se pudieron consultar las promociones.')).toBeInTheDocument();
  });
});

describe('ContactPage', () => {
  it('muestra el formulario, precarga datos del usuario y envía el mensaje', async () => {
    contactoService.create.mockResolvedValue({});
    const user = { nombre: 'Ana', correo: 'ana@x.cl', token: 'tk' };
    render(<ContactPage user={user} />);
    expect(screen.getByRole('heading', { name: 'Contáctanos' })).toBeInTheDocument();
    expect(screen.getByLabelText('Nombre')).toHaveValue('Ana');
    expect(screen.getByLabelText('Correo electrónico')).toHaveValue('ana@x.cl');
    fireEvent.change(screen.getByLabelText('Mensaje'), { target: { value: 'Hola!' } });
    fireEvent.click(screen.getByRole('button', { name: 'Enviar mensaje' }));
    expect(await screen.findByText('Tu mensaje fue enviado correctamente.')).toBeInTheDocument();
    expect(contactoService.create).toHaveBeenCalledWith({ nombre: 'Ana', correo: 'ana@x.cl', comentario: 'Hola!' }, 'tk');
  });

  it('funciona para visitantes con formulario vacío', () => {
    render(<ContactPage user={null} />);
    expect(screen.getByLabelText('Nombre')).toHaveValue('');
  });
});

describe('OrderHistoryPage', () => {
  const user = { token: 'tk' };

  it('muestra carga y luego los pedidos del usuario', async () => {
    pedidoService.historial.mockResolvedValue([{ id: 5, estado: 'ENVIADO', total: 12000, direccionEntrega: 'Calle 1', detalles: [{ productoId: 1, productoNombre: 'Manzanas', cantidad: 2 }] }]);
    render(<OrderHistoryPage user={user} />);
    expect(screen.getByText('Cargando pedidos...')).toBeInTheDocument();
    expect(await screen.findByText('Pedido #5')).toBeInTheDocument();
    expect(screen.getByText('Enviado')).toBeInTheDocument();
    expect(pedidoService.historial).toHaveBeenCalledWith('tk');
    fireEvent.click(screen.getByRole('button', { name: /Pedido #5/ }));
    expect(screen.getByText(/Manzanas · 2 unidad/)).toBeInTheDocument();
  });

  it('muestra estado vacío y errores', async () => {
    pedidoService.historial.mockResolvedValueOnce([]);
    const { unmount } = render(<OrderHistoryPage user={user} />);
    expect(await screen.findByText('Todavía no tienes pedidos registrados.')).toBeInTheDocument();
    unmount();
    pedidoService.historial.mockRejectedValueOnce(new Error('Sesión expirada'));
    const second = render(<OrderHistoryPage user={user} />);
    expect(await screen.findByText('Sesión expirada')).toBeInTheDocument();
    second.unmount();
    pedidoService.historial.mockRejectedValueOnce({});
    render(<OrderHistoryPage user={user} />);
    expect(await screen.findByText('No se pudo cargar el historial.')).toBeInTheDocument();
  });
});

describe('ProfilePage', () => {
  const user = { token: 'tk', nombre: 'Ana', apellidos: 'Pérez', direccion: 'Calle 1', region: 'RM', comuna: 'Santiago' };

  it('carga el perfil desde el servicio y lo muestra en el formulario', async () => {
    usuarioService.getProfile.mockResolvedValue({ nombre: 'Ana María' });
    render(<ProfilePage user={user} onUserUpdated={vi.fn()} />);
    expect(screen.getByText('Cargando perfil...')).toBeInTheDocument();
    expect(await screen.findByLabelText('Nombre')).toHaveValue('Ana María');
    expect(usuarioService.getProfile).toHaveBeenCalledWith('tk');
  });

  it('muestra error si falla la carga del perfil', async () => {
    usuarioService.getProfile.mockRejectedValueOnce(new Error('No hay perfil'));
    const { unmount } = render(<ProfilePage user={user} />);
    expect(await screen.findByRole('alert')).toHaveTextContent('No hay perfil');
    unmount();
    usuarioService.getProfile.mockRejectedValueOnce({});
    render(<ProfilePage user={user} />);
    expect(await screen.findByRole('alert')).toHaveTextContent('No se pudo cargar tu perfil.');
  });

  it('guarda el perfil, avisa al padre y muestra confirmación', async () => {
    usuarioService.getProfile.mockResolvedValue({});
    usuarioService.updateProfile.mockResolvedValue({ nombre: 'Anita' });
    const onUserUpdated = vi.fn();
    render(<ProfilePage user={user} onUserUpdated={onUserUpdated} />);
    fireEvent.change(await screen.findByLabelText('Nombre'), { target: { value: 'Anita' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar cambios' }));
    expect(await screen.findByText('Perfil actualizado correctamente.')).toBeInTheDocument();
    expect(usuarioService.updateProfile).toHaveBeenCalledWith(expect.objectContaining({ nombre: 'Anita' }), 'tk');
    expect(onUserUpdated).toHaveBeenCalledWith(expect.objectContaining({ nombre: 'Anita', token: 'tk' }));
  });

  it('informa errores al guardar el perfil', async () => {
    usuarioService.getProfile.mockResolvedValue({});
    usuarioService.updateProfile.mockRejectedValue(new Error('Datos inválidos'));
    render(<ProfilePage user={user} />);
    await screen.findByLabelText('Nombre');
    fireEvent.click(screen.getByRole('button', { name: 'Guardar cambios' }));
    expect(await screen.findByText('Datos inválidos')).toBeInTheDocument();
  });

  it('cambia la contraseña, limpia el formulario y maneja errores', async () => {
    usuarioService.getProfile.mockResolvedValue({});
    usuarioService.changePassword.mockResolvedValueOnce({}).mockRejectedValueOnce(new Error('Clave actual incorrecta'));
    render(<ProfilePage user={user} />);
    await screen.findByLabelText('Nombre');
    const llenar = () => {
      fireEvent.change(screen.getByLabelText('Contraseña actual'), { target: { value: 'abcd' } });
      fireEvent.change(screen.getByLabelText('Nueva contraseña'), { target: { value: 'nuev' } });
      fireEvent.change(screen.getByLabelText('Confirmar contraseña'), { target: { value: 'nuev' } });
      fireEvent.click(screen.getByRole('button', { name: 'Actualizar contraseña' }));
    };
    llenar();
    expect(await screen.findByText('Contraseña actualizada correctamente.')).toBeInTheDocument();
    expect(usuarioService.changePassword).toHaveBeenCalledWith(
      { contrasenaActual: 'abcd', nuevaContrasena: 'nuev', confirmarContrasena: 'nuev' }, 'tk');
    expect(screen.getByLabelText('Contraseña actual')).toHaveValue('');
    llenar();
    expect(await screen.findByText('Clave actual incorrecta')).toBeInTheDocument();
  });
});
