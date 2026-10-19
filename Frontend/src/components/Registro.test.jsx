import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import Registro from './Registro';
import { authService } from '../services/authService';

vi.mock('../services/authService', () => ({
  authService: {
    registro: vi.fn(),
  },
}));

const completarFormulario = () => {
  const valores = {
    RUN: '190110222',
    Nombre: 'Ana',
    Apellidos: 'Cliente',
    'Correo electrónico': 'ana@correo.cl',
    Contraseña: 'secreto',
    'Repetir contraseña': 'secreto',
    Región: 'Metropolitana',
    Comuna: 'Santiago',
    'Dirección de entrega': 'Av. Siempre Viva 123',
  };

  Object.entries(valores).forEach(([label, value]) => {
    fireEvent.change(screen.getByLabelText(label), { target: { value } });
  });
};

describe('Registro', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('valida que las contraseñas coincidan antes de llamar al servicio', () => {
    render(<Registro cambiarVista={vi.fn()} />);
    completarFormulario();
    fireEvent.change(screen.getByLabelText('Repetir contraseña'), { target: { value: 'diferente' } });
    fireEvent.click(screen.getByRole('button', { name: 'Crear cuenta' }));

    expect(screen.getByRole('alert')).toHaveTextContent('Las contraseñas no coinciden.');
    expect(authService.registro).not.toHaveBeenCalled();
  });

  it('registra los datos y muestra la confirmación', async () => {
    authService.registro.mockResolvedValue({ id: 1 });
    render(<Registro cambiarVista={vi.fn()} />);
    completarFormulario();
    fireEvent.click(screen.getByRole('button', { name: 'Crear cuenta' }));

    await waitFor(() => expect(authService.registro).toHaveBeenCalledWith(expect.objectContaining({
      run: '190110222',
      nombre: 'Ana',
      correo: 'ana@correo.cl',
      confirmarContrasena: 'secreto',
    })));
    expect(await screen.findByText('¡Registro completado!')).toBeInTheDocument();
  });
});
