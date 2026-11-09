import { fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import ContactManager from './ContactManager';
import { contactoService } from '../../services/contactoService';

vi.mock('../../services/contactoService', () => ({ contactoService: { getAll: vi.fn(), markRead: vi.fn() } }));

const mensajes = [
  { id: 1, nombre: 'Ana', correo: 'ana@x.cl', comentario: 'Hola', leido: false },
  { id: 2, nombre: 'Luis', correo: 'luis@x.cl', comentario: 'Gracias', leido: true },
];

describe('ContactManager', () => {
  beforeEach(() => {
    contactoService.getAll.mockResolvedValue(mensajes);
  });

  it('lista mensajes con su estado y sólo ofrece marcar los pendientes', async () => {
    render(<ContactManager token="tk" />);
    const filaAna = (await screen.findByText('Ana')).closest('tr');
    expect(within(filaAna).getByText('Pendiente')).toBeInTheDocument();
    expect(filaAna).toHaveClass('table-warning');
    const filaLuis = screen.getByText('Luis').closest('tr');
    expect(within(filaLuis).getByText('Leído')).toBeInTheDocument();
    expect(within(filaLuis).queryByRole('button')).not.toBeInTheDocument();
    expect(contactoService.getAll).toHaveBeenCalledWith('tk');
  });

  it('marca un mensaje como leído con la respuesta del servidor', async () => {
    contactoService.markRead.mockResolvedValue({ ...mensajes[0], leido: true });
    render(<ContactManager token="tk" />);
    fireEvent.click(await screen.findByRole('button', { name: 'Marcar leído' }));
    await waitFor(() => expect(screen.getAllByText('Leído')).toHaveLength(2));
    expect(contactoService.markRead).toHaveBeenCalledWith(1, 'tk');
    expect(screen.queryByRole('button', { name: 'Marcar leído' })).not.toBeInTheDocument();
  });

  it('muestra error si falla marcar como leído', async () => {
    contactoService.markRead.mockRejectedValue(new Error('No se pudo'));
    render(<ContactManager token="tk" />);
    fireEvent.click(await screen.findByRole('button', { name: 'Marcar leído' }));
    expect(await screen.findByText('No se pudo')).toHaveClass('alert-danger');
    expect(screen.getByText('Pendiente')).toBeInTheDocument();
  });

  it('muestra error si falla la carga', async () => {
    contactoService.getAll.mockRejectedValue(new Error('Sin permisos'));
    render(<ContactManager token="tk" />);
    expect(await screen.findByText('Sin permisos')).toBeInTheDocument();
  });
});
