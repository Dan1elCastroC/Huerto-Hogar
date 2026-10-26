import { render, screen, fireEvent } from '@testing-library/react';
import { expect, test, vi } from 'vitest';
import OrderManager from './OrderManager';
import { pedidoService } from '../../services/pedidoService';

vi.mock('../../services/pedidoService', () => ({ pedidoService: { getAll: vi.fn(), updateStatus: vi.fn() } }));
test('muestra la boleta persistida con productos, descuento y total', async () => {
  pedidoService.getAll.mockResolvedValue([{ id: 7, estado: 'PENDIENTE', direccionEntrega: 'Calle 123', subtotal: 2000, descuento: 200, total: 1800, cuponAplicado: 'QA10', detalles: [{ productoId: 1, productoNombre: 'Semillas', cantidad: 2, precioUnitario: 1000, subtotal: 2000 }] }]);
  render(<OrderManager token="token" />);
  fireEvent.click(await screen.findByRole('button', { name: 'Ver boleta #7' }));
  expect(screen.getByRole('heading', { name: 'Boleta #7' })).toBeInTheDocument();
  expect(screen.getByText('Semillas')).toBeInTheDocument();
  expect(screen.getByText('QA10')).toBeInTheDocument();
  expect(screen.getByText(/Descuento/)).toBeInTheDocument();
  fireEvent.click(screen.getByRole('button', { name: 'Cerrar boleta' }));
  expect(screen.queryByRole('heading', { name: 'Boleta #7' })).not.toBeInTheDocument();
});
