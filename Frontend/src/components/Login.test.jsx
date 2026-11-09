import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import Login from './Login';
import { CUENTAS_RAPIDAS } from './cuentasRapidas';
import { authService } from '../services/authService';

vi.mock('../services/authService', () => ({
  authService: {
    login: vi.fn(),
    getPerfil: vi.fn(),
  },
}));

describe('Login', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('envía las credenciales y entrega la sesión junto con el perfil', async () => {
    authService.login.mockResolvedValue({ token: 'token-demo', rol: 'CLIENTE', id: 7 });
    authService.getPerfil.mockResolvedValue({ nombre: 'Ana Cliente', correo: 'ana@correo.cl' });
    const onLoginSuccess = vi.fn();

    render(<Login onLoginSuccess={onLoginSuccess} cambiarVista={vi.fn()} />);
    fireEvent.change(screen.getByLabelText('Correo electrónico'), { target: { value: 'ana@correo.cl' } });
    fireEvent.change(screen.getByLabelText('Contraseña'), { target: { value: 'secreto' } });
    fireEvent.click(screen.getByRole('button', { name: 'Ingresar' }));

    await waitFor(() => expect(onLoginSuccess).toHaveBeenCalledWith({
      token: 'token-demo',
      rol: 'CLIENTE',
      id: 7,
      nombre: 'Ana Cliente',
      correo: 'ana@correo.cl',
    }));
    expect(authService.login).toHaveBeenCalledWith({ correo: 'ana@correo.cl', contrasena: 'secreto' });
  });

  it('muestra un mensaje cuando las credenciales son rechazadas', async () => {
    authService.login.mockRejectedValue(new Error('401'));

    render(<Login onLoginSuccess={vi.fn()} cambiarVista={vi.fn()} />);
    fireEvent.change(screen.getByLabelText('Correo electrónico'), { target: { value: 'incorrecto@correo.cl' } });
    fireEvent.change(screen.getByLabelText('Contraseña'), { target: { value: 'incorrecta' } });
    fireEvent.click(screen.getByRole('button', { name: 'Ingresar' }));

    expect(await screen.findByText('Usuario o contraseña incorrectos. Intente nuevamente.')).toBeInTheDocument();
  });

  it.each(CUENTAS_RAPIDAS)('el acceso rápido «$rol» inicia sesión con $correo', async ({ rol, correo, contrasena }) => {
    authService.login.mockResolvedValue({ token: 't', rol: 'X', id: 1 });
    authService.getPerfil.mockResolvedValue({ nombre: rol });
    const onLoginSuccess = vi.fn();

    render(<Login onLoginSuccess={onLoginSuccess} cambiarVista={vi.fn()} />);
    fireEvent.click(screen.getByRole('button', { name: rol }));

    await waitFor(() => expect(onLoginSuccess).toHaveBeenCalledWith({ token: 't', rol: 'X', id: 1, nombre: rol }));
    expect(authService.login).toHaveBeenCalledWith({ correo, contrasena });
  });

  it('el acceso rápido muestra el error si el backend rechaza la cuenta', async () => {
    authService.login.mockRejectedValue(new Error('400'));

    render(<Login onLoginSuccess={vi.fn()} cambiarVista={vi.fn()} />);
    fireEvent.click(screen.getByRole('button', { name: 'Admin' }));

    expect(await screen.findByText('Usuario o contraseña incorrectos. Intente nuevamente.')).toBeInTheDocument();
  });
});

describe('Login sin conexión', () => {
  it('informa que no hay conexión en vez de culpar a las credenciales', async () => {
    const { ConexionError, MENSAJE_SIN_CONEXION } = await import('../services/httpClient');
    authService.login.mockRejectedValue(new ConexionError());

    render(<Login onLoginSuccess={vi.fn()} cambiarVista={vi.fn()} />);
    fireEvent.click(screen.getByRole('button', { name: 'Admin' }));

    expect(await screen.findByText(MENSAJE_SIN_CONEXION)).toBeInTheDocument();
  });
});
