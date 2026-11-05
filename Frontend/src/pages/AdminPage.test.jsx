import { render, screen, fireEvent, within } from '@testing-library/react';
import { beforeEach, expect, test, vi } from 'vitest';
import AdminPage from './AdminPage';
import { reporteService } from '../services/reporteService';

vi.mock('../services/reporteService', () => ({ reporteService: { resumen: vi.fn() } }));
beforeEach(() => reporteService.resumen.mockResolvedValue({ totalPedidos: 17, totalProductos: 23, totalUsuarios: 9 }));

test('muestra los indicadores del backend en el dashboard', async () => {
  render(<AdminPage productos={[]} categorias={[]} token="token" cambiarVista={vi.fn()} onLogout={vi.fn()} />);
  expect(await screen.findByText('17')).toBeInTheDocument();
  expect(screen.getByText('23')).toBeInTheDocument();
  expect(screen.getByText('9')).toBeInTheDocument();
  expect(reporteService.resumen).toHaveBeenCalledWith('token');
});

test('botón Tienda llama a cambiarVista con "catalogo"', async () => {
  const cambiarVista = vi.fn();
  render(<AdminPage productos={[]} categorias={[]} token="token" cambiarVista={cambiarVista} onLogout={vi.fn()} />);
  await screen.findByText('17'); // esperar que cargue
  fireEvent.click(within(screen.getByRole('complementary')).getByRole('button', { name: /Tienda/i }));
  expect(cambiarVista).toHaveBeenCalledWith('catalogo');
});

test('botón Cerrar sesión llama a onLogout', async () => {
  const onLogout = vi.fn();
  render(<AdminPage productos={[]} categorias={[]} token="token" cambiarVista={vi.fn()} onLogout={onLogout} />);
  await screen.findByText('17');
  fireEvent.click(screen.getByRole('button', { name: /Cerrar sesión/i }));
  expect(onLogout).toHaveBeenCalledOnce();
});

test('navegar entre secciones del sidebar cambia el contenido', async () => {
  render(<AdminPage productos={[]} categorias={[]} token="token" cambiarVista={vi.fn()} onLogout={vi.fn()} />);
  await screen.findByText('17');
  // El dashboard debe mostrar "Resumen de las actividades diarias"
  expect(screen.getByText(/Resumen de las actividades diarias/i)).toBeInTheDocument();
});

test('el sidebar tiene todos los módulos esperados', async () => {
  render(<AdminPage productos={[]} categorias={[]} token="token" cambiarVista={vi.fn()} onLogout={vi.fn()} />);
  await screen.findByText('17');
  expect(screen.getAllByRole('button', { name: /Dashboard/i }).length).toBeGreaterThan(0);
  expect(screen.getAllByRole('button', { name: /Órdenes/i }).length).toBeGreaterThan(0);
  expect(screen.getAllByRole('button', { name: /Usuarios/i }).length).toBeGreaterThan(0);
});
