import { fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import App from './App';
import { authService } from './services/authService';
import { categoriaService } from './services/categoriaService';
import { productoService } from './services/productoService';
import { blogService } from './services/blogService';
import { cuponService } from './services/cuponService';

vi.mock('./services/authService', () => ({ authService: { login: vi.fn(), getPerfil: vi.fn() } }));
vi.mock('./services/categoriaService', () => ({ categoriaService: { getAll: vi.fn(), create: vi.fn(), delete: vi.fn() } }));
vi.mock('./services/productoService', () => ({ productoService: { getAll: vi.fn() } }));
vi.mock('./services/blogService', () => ({ blogService: { getAll: vi.fn() } }));
vi.mock('./services/cuponService', () => ({ cuponService: { validate: vi.fn() } }));

describe('App', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
    productoService.getAll.mockResolvedValue([]);
    categoriaService.getAll.mockResolvedValue([]);
    blogService.getAll.mockResolvedValue([]);
    cuponService.validate.mockRejectedValue(new Error('Cupón no disponible'));
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
    fireEvent.click(screen.getByRole('button', { name: 'Categorías' }));
    await waitFor(() => expect(categoriaService.getAll).toHaveBeenCalledTimes(2));
    fireEvent.change(screen.getByLabelText('Nombre'), { target: { value: 'Semillas' } });
    fireEvent.change(screen.getByLabelText('Descripción'), { target: { value: 'Para cultivar' } });
    fireEvent.click(screen.getByRole('button', { name: 'Crear' }));
    await screen.findByText('Categoría creada correctamente.');

    fireEvent.click(screen.getByRole('button', { name: 'Productos' }));

    expect(within(screen.getByLabelText('Categoría')).getByRole('option', { name: 'Semillas' })).toBeInTheDocument();
    categoriaService.getAll.mockResolvedValue([{ id: 9, nombre: 'Semillas', descripcion: 'Para cultivar' }]);
    fireEvent.click(screen.getByRole('button', { name: 'Categorías' }));
    await screen.findByText('Semillas');
    fireEvent.click(screen.getByRole('button', { name: 'Eliminar' }));
    await waitFor(() => expect(screen.queryByText('Semillas')).not.toBeInTheDocument());
    fireEvent.click(screen.getByRole('button', { name: 'Productos' }));
    expect(within(screen.getByLabelText('Categoría')).queryByRole('option', { name: 'Semillas' })).not.toBeInTheDocument();
  });
});
