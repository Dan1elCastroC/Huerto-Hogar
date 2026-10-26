import { render, screen, fireEvent } from '@testing-library/react';
import { beforeEach, expect, test, vi } from 'vitest';
import AdminPage from './AdminPage';
import { reporteService } from '../services/reporteService';

vi.mock('../services/reporteService', () => ({ reporteService: { resumen: vi.fn() } }));
beforeEach(() => reporteService.resumen.mockResolvedValue({ totalPedidos: 17, totalProductos: 23, totalUsuarios: 9 }));

test('abre el dashboard con indicadores del backend y accesos a tienda y sesión', async () => {
  const cambiarVista = vi.fn();
  const onLogout = vi.fn();
  render(<AdminPage productos={[]} categorias={[]} token="token" cambiarVista={cambiarVista} onLogout={onLogout} />);
  expect(await screen.findByText('17')).toBeInTheDocument();
  expect(screen.getByText('23')).toBeInTheDocument();
  expect(screen.getByText('9')).toBeInTheDocument();
  expect(reporteService.resumen).toHaveBeenCalledWith('token');
  fireEvent.click(screen.getByRole('button', { name: 'Tienda' }));
  expect(cambiarVista).toHaveBeenCalledWith('catalogo');
  fireEvent.click(screen.getByRole('button', { name: 'Cerrar sesión' }));
  expect(onLogout).toHaveBeenCalledOnce();
});
