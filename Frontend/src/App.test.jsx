import { fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import App from './App';
import { authService } from './services/authService';
import { categoriaService } from './services/categoriaService';
import { productoService } from './services/productoService';
import { blogService } from './services/blogService';
import { cuponService } from './services/cuponService';
import { carritoService } from './services/carritoService';
import { pedidoService } from './services/pedidoService';
import { usuarioService } from './services/usuarioService';

vi.mock('./services/authService', () => ({ authService: { login: vi.fn(), getPerfil: vi.fn() } }));
vi.mock('./services/categoriaService', () => ({ categoriaService: { getAll: vi.fn(), create: vi.fn(), delete: vi.fn() } }));
vi.mock('./services/productoService', () => ({ productoService: { getAll: vi.fn() } }));
vi.mock('./services/blogService', () => ({ blogService: { getAll: vi.fn() } }));
vi.mock('./services/cuponService', () => ({ cuponService: { validate: vi.fn() } }));
vi.mock('./services/carritoService', () => ({ carritoService: { add: vi.fn(), update: vi.fn(), remove: vi.fn(), clear: vi.fn() } }));
vi.mock('./services/pedidoService', () => ({ pedidoService: { crearPedido: vi.fn(), historial: vi.fn() } }));
vi.mock('./services/usuarioService', () => ({ usuarioService: { getProfile: vi.fn(), updateProfile: vi.fn(), changePassword: vi.fn() } }));

describe('App', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
    productoService.getAll.mockResolvedValue([]);
    categoriaService.getAll.mockResolvedValue([]);
    blogService.getAll.mockResolvedValue([]);
    cuponService.validate.mockRejectedValue(new Error('Cupón no disponible'));
    carritoService.add.mockResolvedValue({});
    carritoService.update.mockResolvedValue({});
    carritoService.remove.mockResolvedValue({});
    carritoService.clear.mockResolvedValue({});
  });

  it('navega por nosotros, categorías y detalle sin perder el filtro al volver', async () => {
    productoService.getAll.mockResolvedValue([
      { id: 1, nombre: 'Manzanas', categoriaNombre: 'Frutas', precio: 2000, stock: 4, descripcion: 'Manzanas frescas' },
      { id: 2, nombre: 'Tomates', categoriaNombre: 'Verduras', precio: 1500, stock: 8 },
    ]);
    categoriaService.getAll.mockResolvedValue([{ id: 1, nombre: 'Frutas' }, { id: 2, nombre: 'Verduras' }]);
    render(<App />);
    fireEvent.click(screen.getByRole('link', { name: 'Nosotros' }));
    expect(screen.getByRole('heading', { name: 'Nosotros' })).toBeInTheDocument();
    fireEvent.click(screen.getByRole('link', { name: 'Categorías' }));
    fireEvent.click(await screen.findByRole('button', { name: 'Ver productos de Frutas' }));
    expect(screen.getByText('Manzanas')).toBeInTheDocument();
    expect(screen.queryByText('Tomates')).not.toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Ver detalle de Manzanas' }));
    expect(screen.getByText('Manzanas frescas')).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Añadir al carrito' }));
    expect(screen.getByRole('link', { name: /Carrito 1/ })).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Volver a productos' }));
    expect(screen.getByText('Manzanas')).toBeInTheDocument();
    expect(screen.queryByText('Tomates')).not.toBeInTheDocument();
  });

  it('abre una noticia real y permite regresar al listado', async () => {
    blogService.getAll.mockResolvedValue([{ id: 4, titulo: 'Cuidar el huerto', descripcion: 'Riega temprano para aprovechar el agua.' }]);
    render(<App />);
    fireEvent.click(screen.getByRole('link', { name: 'Noticias' }));
    fireEvent.click(await screen.findByRole('button', { name: 'Leer Cuidar el huerto' }));
    expect(screen.getByRole('heading', { level: 1, name: 'Cuidar el huerto' })).toBeInTheDocument();
    expect(screen.getByText('Riega temprano para aprovechar el agua.')).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Volver a noticias' }));
    expect(await screen.findByRole('heading', { name: 'Noticias y consejos' })).toBeInTheDocument();
  });

  it('muestra ofertas sólo después de validar el cupón público', async () => {
    productoService.getAll.mockResolvedValue([{ id: 1, nombre: 'Manzanas', precio: 2000, stock: 4 }]);
    cuponService.validate.mockResolvedValue({ codigo: 'BIENVENIDA10', porcentaje: 10, activo: true, fechaExpiracion: '2099-12-31' });
    render(<App />);
    fireEvent.click(screen.getByRole('link', { name: 'Ofertas' }));
    expect(await screen.findByText('Precio con cupón: $1.800')).toBeInTheDocument();
    expect(cuponService.validate).toHaveBeenCalledWith('BIENVENIDA10');
    expect(screen.getByText(/Ingresa BIENVENIDA10 en el carrito/)).toBeInTheDocument();
  });

  it('no anuncia descuentos de un cupón vencido', async () => {
    cuponService.validate.mockResolvedValue({ codigo: 'BIENVENIDA10', porcentaje: 10, activo: true, fechaExpiracion: '2000-01-01' });
    render(<App />);
    fireEvent.click(screen.getByRole('link', { name: 'Ofertas' }));
    await waitFor(() => expect(screen.getByRole('status')).toHaveTextContent('No hay promociones vigentes.'));
    expect(screen.queryByText(/Precio con cupón/)).not.toBeInTheDocument();
  });

  it('informa en el catálogo cuando falla la conexión al backend', async () => {
    productoService.getAll.mockRejectedValue(new Error('Failed to fetch'));
    render(<App />);

    fireEvent.click(screen.getByRole('link', { name: 'Productos' }));

    expect(await screen.findByRole('alert')).toHaveTextContent('No se pudieron cargar los productos desde el backend.');
  });

  it('cierra la sesión y mantiene al usuario desconectado al recargar', async () => {
    authService.login.mockResolvedValue({ token: 'token-prueba', rol: 'CLIENTE' });
    authService.getPerfil.mockResolvedValue({ nombre: 'Ana', apellidos: 'Pérez', correo: 'ana@correo.cl' });
    const app = render(<App />);
    fireEvent.click(screen.getByRole('link', { name: 'Iniciar sesión' }));
    fireEvent.change(screen.getByLabelText('Correo electrónico'), { target: { value: 'ana@correo.cl' } });
    fireEvent.change(screen.getByLabelText('Contraseña'), { target: { value: 'prueba' } });
    fireEvent.click(screen.getByRole('button', { name: 'Ingresar' }));
    await screen.findByRole('link', { name: 'Mi perfil' });
    expect(screen.getByText('Ana Pérez')).toBeInTheDocument();

    fireEvent.click(screen.getByRole('link', { name: 'Cerrar sesión' }));

    expect(localStorage.getItem('huerto-hogar-user')).toBeNull();
    expect(screen.queryByRole('link', { name: 'Mi perfil' })).not.toBeInTheDocument();
    expect(screen.queryByText('Ana Pérez')).not.toBeInTheDocument();
    app.unmount();
    render(<App />);
    expect(screen.getByRole('link', { name: 'Iniciar sesión' })).toBeInTheDocument();
    expect(screen.queryByRole('link', { name: 'Cerrar sesión' })).not.toBeInTheDocument();
    expect(screen.queryByText('Ana Pérez')).not.toBeInTheDocument();
    await waitFor(() => expect(productoService.getAll).toHaveBeenCalledTimes(2));
  });

  it('actualiza las categorías disponibles al crear y eliminar una categoría', async () => {
    localStorage.setItem('huerto-hogar-user', JSON.stringify({ token: 'token-prueba', rol: 'ADMINISTRADOR' }));
    categoriaService.create.mockResolvedValue({ id: 9, nombre: 'Semillas', descripcion: 'Para cultivar' });
    categoriaService.delete.mockResolvedValue('Eliminada');
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    render(<App />);
    fireEvent.click(screen.getByRole('link', { name: 'Panel principal' }));
    const navegacionAdmin = within(screen.getByRole('navigation'));
    fireEvent.click(navegacionAdmin.getByRole('button', { name: /Categorías/ }));
    await waitFor(() => expect(categoriaService.getAll).toHaveBeenCalledTimes(2));
    fireEvent.change(screen.getByLabelText('Nombre'), { target: { value: 'Semillas' } });
    fireEvent.change(screen.getByLabelText('Descripción'), { target: { value: 'Para cultivar' } });
    fireEvent.click(screen.getByRole('button', { name: 'Crear' }));
    await screen.findByText('Categoría creada correctamente.');

    fireEvent.click(navegacionAdmin.getByRole('button', { name: /Productos/ }));

    expect(within(screen.getByLabelText('Categoría')).getByRole('option', { name: 'Semillas' })).toBeInTheDocument();
    categoriaService.getAll.mockResolvedValue([{ id: 9, nombre: 'Semillas', descripcion: 'Para cultivar' }]);
    fireEvent.click(navegacionAdmin.getByRole('button', { name: /Categorías/ }));
    await screen.findByText('Semillas');
    fireEvent.click(screen.getByRole('button', { name: 'Eliminar' }));
    await waitFor(() => expect(screen.queryByText('Semillas')).not.toBeInTheDocument());
    fireEvent.click(navegacionAdmin.getByRole('button', { name: /Productos/ }));
    expect(within(screen.getByLabelText('Categoría')).queryByRole('option', { name: 'Semillas' })).not.toBeInTheDocument();
  });

  describe('carrito, sesión y navegación', () => {
    const manzanas = { id: 1, nombre: 'Manzanas', categoriaNombre: 'Frutas', precio: 2000, stock: 3 };
    const tomates = { id: 2, nombre: 'Tomates', categoriaNombre: 'Verduras', precio: 1500, stock: 8 };
    const cartKey = 'huerto-hogar-cart';
    const userKey = 'huerto-hogar-user';

    beforeEach(() => {
      productoService.getAll.mockResolvedValue([manzanas, tomates]);
    });

    const abrirCatalogo = async () => {
      render(<App />);
      fireEvent.click(screen.getByRole('link', { name: 'Productos' }));
      await screen.findByText('Manzanas');
    };

    const iniciarSesionUI = async (correo) => {
      fireEvent.click(screen.getByRole('link', { name: 'Iniciar sesión' }));
      fireEvent.change(screen.getByLabelText('Correo electrónico'), { target: { value: correo } });
      fireEvent.change(screen.getByLabelText('Contraseña'), { target: { value: 'clave1' } });
      fireEvent.click(screen.getByRole('button', { name: 'Ingresar' }));
    };

    it('agrega productos, suma la insignia y persiste el carrito en localStorage', async () => {
      await abrirCatalogo();
      const botones = screen.getAllByRole('button', { name: 'Añadir al Carrito' });
      fireEvent.click(botones[0]);
      fireEvent.click(botones[0]);
      fireEvent.click(botones[1]);
      expect(screen.getByRole('link', { name: /Carrito 3/ })).toBeInTheDocument();
      await waitFor(() => expect(JSON.parse(localStorage.getItem(cartKey))).toEqual([
        expect.objectContaining({ id: 1, cantidad: 2 }),
        expect.objectContaining({ id: 2, cantidad: 1 }),
      ]));
      expect(carritoService.add).not.toHaveBeenCalled();
    });

    it('recupera el carrito guardado al recargar y tolera datos corruptos', () => {
      localStorage.setItem(cartKey, JSON.stringify([{ ...manzanas, cantidad: 2 }]));
      const app = render(<App />);
      expect(screen.getByRole('link', { name: /Carrito 2/ })).toBeInTheDocument();
      app.unmount();
      localStorage.setItem(cartKey, '{no-json');
      localStorage.setItem(userKey, '{no-json');
      render(<App />);
      expect(screen.getByRole('link', { name: /Carrito 0/ })).toBeInTheDocument();
      expect(screen.getByRole('link', { name: 'Iniciar sesión' })).toBeInTheDocument();
    });

    it('cambia cantidades, elimina productos y vacía el carrito', async () => {
      localStorage.setItem(cartKey, JSON.stringify([{ ...manzanas, cantidad: 1 }, { ...tomates, cantidad: 1 }]));
      vi.spyOn(window, 'confirm').mockReturnValue(true);
      render(<App />);
      fireEvent.click(screen.getByRole('link', { name: /Carrito/ }));
      expect(screen.getByText('Carrito de compras', { exact: false })).toBeInTheDocument();
      fireEvent.click(screen.getByRole('button', { name: 'Aumentar cantidad de Manzanas' }));
      expect(screen.getByRole('link', { name: /Carrito 3/ })).toBeInTheDocument();
      fireEvent.click(screen.getByRole('button', { name: 'Disminuir cantidad de Manzanas' }));
      expect(screen.getByRole('link', { name: /Carrito 2/ })).toBeInTheDocument();
      fireEvent.click(screen.getByRole('button', { name: 'Eliminar Tomates' }));
      expect(screen.queryByText('Tomates')).not.toBeInTheDocument();
      expect(JSON.parse(localStorage.getItem(cartKey))).toHaveLength(1);
      fireEvent.click(screen.getByRole('button', { name: 'Vaciar carrito' }));
      expect(screen.getByText('Tu carrito está vacío')).toBeInTheDocument();
      expect(JSON.parse(localStorage.getItem(cartKey))).toEqual([]);
      fireEvent.click(screen.getByRole('button', { name: 'Ver productos' }));
      expect(await screen.findByRole('heading', { name: 'Catálogo de Productos y Semillas' })).toBeInTheDocument();
    });

    it('bajar la cantidad a cero quita el producto', () => {
      localStorage.setItem(cartKey, JSON.stringify([{ ...manzanas, cantidad: 1 }]));
      render(<App />);
      fireEvent.click(screen.getByRole('link', { name: /Carrito/ }));
      fireEvent.click(screen.getByRole('button', { name: 'Disminuir cantidad de Manzanas' }));
      expect(screen.getByText('Tu carrito está vacío')).toBeInTheDocument();
    });

    it('con sesión iniciada sincroniza cada operación del carrito con el backend', () => {
      localStorage.setItem(userKey, JSON.stringify({ token: 'tk', rol: 'CLIENTE', nombre: 'Ana' }));
      localStorage.setItem(cartKey, JSON.stringify([{ ...manzanas, cantidad: 1 }, { ...tomates, cantidad: 1 }]));
      vi.spyOn(window, 'confirm').mockReturnValue(true);
      render(<App />);
      fireEvent.click(screen.getByRole('link', { name: /Carrito/ }));
      fireEvent.click(screen.getByRole('button', { name: 'Aumentar cantidad de Manzanas' }));
      expect(carritoService.update).toHaveBeenCalledWith(1, 2, 'tk');
      fireEvent.click(screen.getByRole('button', { name: 'Eliminar Tomates' }));
      expect(carritoService.remove).toHaveBeenCalledWith(2, 'tk');
      fireEvent.click(screen.getByRole('button', { name: 'Vaciar carrito' }));
      expect(carritoService.clear).toHaveBeenCalledWith('tk');
    });

    it('con sesión iniciada agregar desde el catálogo llama al backend y tolera su fallo', async () => {
      localStorage.setItem(userKey, JSON.stringify({ token: 'tk', rol: 'CLIENTE' }));
      carritoService.add.mockRejectedValue(new Error('offline'));
      const warn = vi.spyOn(console, 'warn').mockImplementation(() => {});
      await abrirCatalogo();
      fireEvent.click(screen.getAllByRole('button', { name: 'Añadir al Carrito' })[0]);
      expect(carritoService.add).toHaveBeenCalledWith(1, 1, 'tk');
      expect(screen.getByRole('link', { name: /Carrito 1/ })).toBeInTheDocument();
      await waitFor(() => expect(warn).toHaveBeenCalled());
    });

    it('al iniciar sesión envía el carrito de invitado al backend y va al catálogo', async () => {
      localStorage.setItem(cartKey, JSON.stringify([{ ...manzanas, cantidad: 2 }]));
      authService.login.mockResolvedValue({ token: 'tk', rol: 'CLIENTE' });
      authService.getPerfil.mockResolvedValue({ nombre: 'Ana', apellidos: 'Pérez', correo: 'ana@x.cl' });
      render(<App />);
      await iniciarSesionUI('ana@x.cl');
      expect(await screen.findByRole('heading', { name: 'Catálogo de Productos y Semillas' })).toBeInTheDocument();
      await waitFor(() => expect(carritoService.add).toHaveBeenCalledWith(1, 2, 'tk'));
      expect(carritoService.clear).toHaveBeenCalledWith('tk');
      expect(JSON.parse(localStorage.getItem(userKey))).toMatchObject({ token: 'tk', nombre: 'Ana' });
    });

    it('un administrador entra al panel sin navbar ni footer de tienda', async () => {
      authService.login.mockResolvedValue({ token: 'tk', rol: 'ADMINISTRADOR' });
      authService.getPerfil.mockResolvedValue({ nombre: 'Root', correo: 'root@x.cl' });
      render(<App />);
      await iniciarSesionUI('root@x.cl');
      await waitFor(() => expect(screen.queryByRole('link', { name: 'Iniciar sesión' })).not.toBeInTheDocument());
      expect(screen.queryByRole('link', { name: 'Home' })).not.toBeInTheDocument();
      expect(screen.queryByText(/Todos los derechos reservados/)).not.toBeInTheDocument();
    });

    it('confirma una compra: crea el pedido, vacía el carrito y recarga el catálogo', async () => {
      localStorage.setItem(userKey, JSON.stringify({ token: 'tk', rol: 'CLIENTE', nombre: 'Ana', direccion: 'Calle 1' }));
      localStorage.setItem(cartKey, JSON.stringify([{ ...manzanas, cantidad: 2 }]));
      pedidoService.crearPedido.mockResolvedValue({ id: 77, direccionEntrega: 'Calle 1', total: 4000 });
      render(<App />);
      await waitFor(() => expect(productoService.getAll).toHaveBeenCalledTimes(1));
      fireEvent.click(screen.getByRole('link', { name: /Carrito/ }));
      fireEvent.click(screen.getByRole('button', { name: 'Proceder al checkout' }));
      fireEvent.click(screen.getByRole('button', { name: 'Confirmar compra' }));
      expect(await screen.findByText('¡Compra realizada con éxito!')).toBeInTheDocument();
      expect(pedidoService.crearPedido).toHaveBeenCalledWith(expect.objectContaining({ direccionEntrega: 'Calle 1' }), 'tk');
      expect(screen.getByRole('link', { name: /Carrito 0/ })).toBeInTheDocument();
      expect(JSON.parse(localStorage.getItem(cartKey))).toEqual([]);
      await waitFor(() => expect(productoService.getAll).toHaveBeenCalledTimes(2));
    });

    it('si el pedido falla muestra el error y conserva el carrito', async () => {
      localStorage.setItem(userKey, JSON.stringify({ token: 'tk', rol: 'CLIENTE', direccion: 'Calle 1' }));
      localStorage.setItem(cartKey, JSON.stringify([{ ...manzanas, cantidad: 1 }]));
      pedidoService.crearPedido.mockRejectedValue(new Error('Stock insuficiente'));
      render(<App />);
      fireEvent.click(screen.getByRole('link', { name: /Carrito/ }));
      fireEvent.click(screen.getByRole('button', { name: 'Proceder al checkout' }));
      fireEvent.click(screen.getByRole('button', { name: 'Confirmar compra' }));
      expect(await screen.findByText('Stock insuficiente')).toBeInTheDocument();
      expect(screen.getByRole('link', { name: /Carrito 1/ })).toBeInTheDocument();
    });

    it('un visitante no puede confirmar la compra y conserva su carrito', async () => {
      localStorage.setItem(cartKey, JSON.stringify([{ ...manzanas, cantidad: 1 }]));
      render(<App />);
      fireEvent.click(screen.getByRole('link', { name: /Carrito/ }));
      fireEvent.click(screen.getByRole('button', { name: 'Proceder al checkout' }));
      fireEvent.click(screen.getByRole('button', { name: 'Iniciar sesión' }));
      expect(await screen.findByLabelText('Correo electrónico')).toBeInTheDocument();
      expect(pedidoService.crearPedido).not.toHaveBeenCalled();
      expect(screen.getByRole('link', { name: /Carrito 1/ })).toBeInTheDocument();
    });

    it('usuario autenticado navega a perfil y pedidos; contacto precarga sus datos', async () => {
      localStorage.setItem(userKey, JSON.stringify({ token: 'tk', rol: 'CLIENTE', nombre: 'Ana', correo: 'ana@x.cl' }));
      usuarioService.getProfile.mockResolvedValue({ nombre: 'Ana' });
      pedidoService.historial.mockResolvedValue([]);
      render(<App />);
      fireEvent.click(screen.getByRole('link', { name: 'Mi perfil' }));
      expect(await screen.findByRole('heading', { name: 'Mi perfil' })).toBeInTheDocument();
      fireEvent.click(screen.getByRole('link', { name: 'Mis pedidos' }));
      expect(await screen.findByText('Todavía no tienes pedidos registrados.')).toBeInTheDocument();
      fireEvent.click(screen.getByRole('link', { name: 'Contacto' }));
      expect(screen.getByLabelText('Correo electrónico')).toHaveValue('ana@x.cl');
    });

    it('navega entre vistas públicas y vuelve al inicio con el logo', async () => {
      render(<App />);
      expect(screen.getByRole('heading', { name: 'Del campo a tu hogar' })).toBeInTheDocument();
      fireEvent.click(screen.getByRole('link', { name: 'Registrarse' }));
      expect(screen.getByLabelText('Repetir contraseña')).toBeInTheDocument();
      fireEvent.click(screen.getByRole('link', { name: 'Contacto' }));
      expect(screen.getByRole('heading', { name: 'Contáctanos' })).toBeInTheDocument();
      fireEvent.click(screen.getByRole('link', { name: 'Ofertas' }));
      expect(await screen.findByRole('heading', { name: 'Ofertas' })).toBeInTheDocument();
      fireEvent.click(screen.getByRole('link', { name: /HUERTO HOGAR/ }));
      expect(screen.getByRole('heading', { name: 'Del campo a tu hogar' })).toBeInTheDocument();
      expect(screen.getByText(/Todos los derechos reservados/)).toBeInTheDocument();
    });

    it('el detalle abierto desde ofertas vuelve a ofertas', async () => {
      cuponService.validate.mockResolvedValue({ codigo: 'BIENVENIDA10', porcentaje: 10, activo: true, fechaExpiracion: '2099-12-31' });
      render(<App />);
      fireEvent.click(screen.getByRole('link', { name: 'Ofertas' }));
      fireEvent.click(await screen.findByRole('button', { name: 'Ver detalle de Manzanas' }));
      fireEvent.click(screen.getByRole('button', { name: 'Volver a productos' }));
      expect(await screen.findByRole('heading', { name: 'Ofertas' })).toBeInTheDocument();
    });

    it('el detalle abierto desde el inicio permite agregar al carrito', async () => {
      render(<App />);
      fireEvent.click(await screen.findByRole('button', { name: 'Ver detalle de Manzanas' }));
      fireEvent.click(screen.getByRole('button', { name: 'Añadir al carrito' }));
      expect(screen.getByRole('link', { name: /Carrito 1/ })).toBeInTheDocument();
    });
  });
});
