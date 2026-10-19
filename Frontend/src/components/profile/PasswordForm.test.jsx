import { fireEvent, render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import PasswordForm from './PasswordForm';

describe('PasswordForm', () => {
  it('rechaza una confirmación de contraseña diferente', () => {
    const onSave = vi.fn();
    render(<PasswordForm onSave={onSave} saving={false} />);

    fireEvent.change(screen.getByLabelText('Contraseña actual'), { target: { value: 'actual' } });
    fireEvent.change(screen.getByLabelText('Nueva contraseña'), { target: { value: 'nueva' } });
    fireEvent.change(screen.getByLabelText('Confirmar contraseña'), { target: { value: 'otra' } });
    fireEvent.click(screen.getByRole('button', { name: 'Actualizar contraseña' }));

    expect(screen.getByRole('alert')).toHaveTextContent('Las contraseñas nuevas no coinciden.');
    expect(onSave).not.toHaveBeenCalled();
  });

  it('entrega las contraseñas cuando la confirmación es válida', () => {
    const onSave = vi.fn();
    render(<PasswordForm onSave={onSave} saving={false} />);

    fireEvent.change(screen.getByLabelText('Contraseña actual'), { target: { value: 'actual' } });
    fireEvent.change(screen.getByLabelText('Nueva contraseña'), { target: { value: 'nueva' } });
    fireEvent.change(screen.getByLabelText('Confirmar contraseña'), { target: { value: 'nueva' } });
    fireEvent.click(screen.getByRole('button', { name: 'Actualizar contraseña' }));

    expect(onSave).toHaveBeenCalledWith({
      contrasenaActual: 'actual',
      nuevaContrasena: 'nueva',
      confirmarContrasena: 'nueva',
    }, expect.any(Function));
  });
});
