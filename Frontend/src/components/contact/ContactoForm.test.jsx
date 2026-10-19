import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import ContactoForm from './ContactoForm';
import { contactoService } from '../../services/contactoService';

vi.mock('../../services/contactoService', () => ({
  contactoService: {
    create: vi.fn(),
  },
}));

describe('ContactoForm', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('envía el mensaje y muestra confirmación sin depender del backend', async () => {
    contactoService.create.mockResolvedValue({ id: 8 });
    const onSubmitted = vi.fn();
    render(<ContactoForm user={{ token: 'token-demo' }} onSubmitted={onSubmitted} />);

    fireEvent.change(screen.getByLabelText('Nombre'), { target: { value: 'Ana Cliente' } });
    fireEvent.change(screen.getByLabelText('Correo electrónico'), { target: { value: 'ana@correo.cl' } });
    fireEvent.change(screen.getByLabelText('Mensaje'), { target: { value: 'Necesito ayuda con mi pedido.' } });
    fireEvent.click(screen.getByRole('button', { name: 'Enviar mensaje' }));

    await waitFor(() => expect(contactoService.create).toHaveBeenCalledWith({
      nombre: 'Ana Cliente',
      correo: 'ana@correo.cl',
      comentario: 'Necesito ayuda con mi pedido.',
    }, 'token-demo'));
    expect(await screen.findByRole('alert')).toHaveTextContent('Tu mensaje fue enviado correctamente.');
    expect(onSubmitted).toHaveBeenCalledTimes(1);
  });
});
