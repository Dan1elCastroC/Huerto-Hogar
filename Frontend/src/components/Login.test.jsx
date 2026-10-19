import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import Login from './Login';
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
});
