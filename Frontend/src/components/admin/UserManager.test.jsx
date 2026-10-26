import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { beforeEach, expect, test, vi } from 'vitest';
import UserManager from './UserManager';
import { usuarioService } from '../../services/usuarioService';
import { pedidoService } from '../../services/pedidoService';

vi.mock('../../services/usuarioService', () => ({ usuarioService: { getAll: vi.fn(), create: vi.fn(), update: vi.fn(), toggleActive: vi.fn() } }));
vi.mock('../../services/pedidoService', () => ({ pedidoService: { getAll: vi.fn() } }));
const usuario = { id: 1, run: '190110222', nombre: 'Ana', apellidos: 'Pérez', correo: 'ana@gmail.com', direccion: 'Calle 1', region: 'Biobío', comuna: 'Concepción', rol: 'CLIENTE', activo: true };
beforeEach(() => usuarioService.getAll.mockResolvedValue([usuario]));

test('permite crear un usuario administrador desde un formulario completo', async () => {
  usuarioService.create.mockResolvedValue({ ...usuario, id: 2, nombre: 'Luis' });
  render(<UserManager token="token" />);
  await screen.findByText('Ana Pérez');
  fireEvent.click(screen.getByRole('button', { name: 'Nuevo usuario' }));
  for (const [label, value] of [['RUN', '190110222'], ['Nombre', 'Luis'], ['Apellidos', 'Pérez'], ['Correo electrónico', 'luis@gmail.com'], ['Contraseña', 'clave1'], ['Confirmar contraseña', 'clave1'], ['Dirección', 'Calle 1'], ['Región', 'Biobío'], ['Comuna', 'Concepción']]) {
    fireEvent.change(screen.getByLabelText(label), { target: { value } });
  }
  fireEvent.change(screen.getByLabelText('Rol'), { target: { value: 'ADMINISTRADOR' } });
  fireEvent.click(screen.getByRole('button', { name: 'Crear usuario' }));
  await waitFor(() => expect(usuarioService.create).toHaveBeenCalledWith(expect.objectContaining({ nombre: 'Luis', rol: 'ADMINISTRADOR' }), 'token'));
  expect(await screen.findByText('Luis Pérez')).toBeInTheDocument();
});

test('permite editar un usuario conservando su identificador', async () => {
  usuarioService.update.mockResolvedValue({ ...usuario, nombre: 'Anita' });
  render(<UserManager token="token" />);
  fireEvent.click(await screen.findByRole('button', { name: 'Editar Ana Pérez' }));
  fireEvent.change(screen.getByLabelText('Nombre'), { target: { value: 'Anita' } });
  expect(screen.getByLabelText('Nueva contraseña')).not.toBeRequired();
  expect(screen.getByLabelText('RUN')).toHaveAttribute('readonly');
  fireEvent.click(screen.getByRole('button', { name: 'Guardar cambios' }));
  await waitFor(() => expect(usuarioService.update).toHaveBeenCalledWith(1, expect.objectContaining({ nombre: 'Anita', run: '190110222', contrasena: null }), 'token'));
  expect(await screen.findByText('Anita Pérez')).toBeInTheDocument();
});

test('el historial del usuario muestra únicamente sus compras', async () => {
  pedidoService.getAll.mockResolvedValue([{ id: 10, usuarioId: 1, estado: 'PENDIENTE', total: 2000, detalles: [] }, { id: 20, usuarioId: 2, estado: 'PENDIENTE', total: 1000, detalles: [] }]);
  render(<UserManager token="token" />);
  fireEvent.click(await screen.findByRole('button', { name: 'Historial de Ana Pérez' }));
  expect(await screen.findByText('Pedido #10')).toBeInTheDocument();
  expect(screen.queryByText('Pedido #20')).not.toBeInTheDocument();
});
