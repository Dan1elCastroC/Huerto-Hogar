import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { expect, test, vi } from 'vitest';
import MainPage from './MainPage';
import { productoService } from '../services/productoService';

vi.mock('../services/productoService', () => ({ productoService: { update: vi.fn(), create: vi.fn(), delete: vi.fn() } }));
const producto = { id: 1, codigo: 'FRU-001', nombre: 'Manzanas', precio: 1200, stock: 2, stockCritico: 3, categoriaId: 1, categoriaNombre: 'Frutas', imagenUrl: '/img/manzanas.jpg' };

test('editar producto persiste sus campos y actualiza el inventario', async () => {
  const setProductos = vi.fn();
  productoService.update.mockResolvedValue({ ...producto, nombre: 'Manzanas verdes' });
  render(<MainPage productos={[producto]} setProductos={setProductos} categorias={[{ id: 1, nombre: 'Frutas' }]} token="token" />);
  fireEvent.click(screen.getByRole('button', { name: 'Editar Manzanas' }));
  fireEvent.change(screen.getByLabelText('Nombre'), { target: { value: 'Manzanas verdes' } });
  fireEvent.click(screen.getByRole('button', { name: 'Guardar cambios' }));
  await waitFor(() => expect(productoService.update).toHaveBeenCalledWith(1, expect.objectContaining({ nombre: 'Manzanas verdes', categoriaId: 1, stock: 2 }), 'token'));
  expect(setProductos.mock.calls[0][0]([producto])[0].nombre).toBe('Manzanas verdes');
});

test('filtra el inventario por stock crítico', () => {
  render(<MainPage productos={[producto, { ...producto, id: 2, nombre: 'Tomates', stock: 20 }]} categorias={[]} token="token" />);
  fireEvent.click(screen.getByRole('button', { name: 'Ver stock crítico' }));
  expect(screen.getByText('Manzanas')).toBeInTheDocument();
  expect(screen.queryByText('Tomates')).not.toBeInTheDocument();
});
