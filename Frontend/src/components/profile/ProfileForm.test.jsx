import { fireEvent, render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import ProfileForm from './ProfileForm';

describe('ProfileForm', () => {
  it('permite editar los datos y guardarlos', () => {
    const onSave = vi.fn();
    render(<ProfileForm profile={{ nombre: 'Ana', apellidos: 'Cliente', direccion: 'Calle 1', region: 'RM', comuna: 'Santiago' }} onSave={onSave} saving={false} />);

    fireEvent.change(screen.getByLabelText('Nombre'), { target: { value: 'Ana María' } });
    fireEvent.click(screen.getByRole('button', { name: 'Guardar cambios' }));

    expect(onSave).toHaveBeenCalledWith(expect.objectContaining({ nombre: 'Ana María' }));
  });
});
