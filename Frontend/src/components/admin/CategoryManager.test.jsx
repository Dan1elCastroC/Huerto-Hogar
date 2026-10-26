import { useState } from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { expect, test, vi } from 'vitest';
import CategoryManager from './CategoryManager';
import { categoriaService } from '../../services/categoriaService';

vi.mock('../../services/categoriaService', () => ({ categoriaService: { getAll: vi.fn(), update: vi.fn(), create: vi.fn() } }));
test('editar categoría usa el endpoint existente y refleja el cambio', async () => {
  const categoria = { id: 1, nombre: 'Frutas', descripcion: 'Frescas' };
  categoriaService.getAll.mockResolvedValue([categoria]);
  categoriaService.update.mockResolvedValue({ ...categoria, nombre: 'Frutas frescas' });
  function Vista() { const [categorias, setCategorias] = useState([]); return <CategoryManager categorias={categorias} setCategorias={setCategorias} token="token" />; }
  render(<Vista />);
  fireEvent.click(await screen.findByRole('button', { name: 'Editar Frutas' }));
  fireEvent.change(screen.getByLabelText('Nombre'), { target: { value: 'Frutas frescas' } });
  fireEvent.click(screen.getByRole('button', { name: 'Guardar cambios' }));
  await waitFor(() => expect(categoriaService.update).toHaveBeenCalledWith(1, { nombre: 'Frutas frescas', descripcion: 'Frescas' }, 'token'));
  expect(await screen.findByText('Frutas frescas')).toBeInTheDocument();
});
