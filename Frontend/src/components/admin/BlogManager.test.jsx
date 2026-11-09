import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import BlogManager from './BlogManager';
import { blogService } from '../../services/blogService';

vi.mock('../../services/blogService', () => ({ blogService: { getAll: vi.fn(), create: vi.fn(), delete: vi.fn() } }));

const blog = { id: 1, titulo: 'Riego eficiente', descripcion: 'Riega temprano' };

describe('BlogManager', () => {
  beforeEach(() => {
    blogService.getAll.mockResolvedValue([blog]);
  });

  it('lista las noticias existentes', async () => {
    render(<BlogManager token="tk" />);
    expect(await screen.findByText('Riego eficiente')).toBeInTheDocument();
    expect(screen.getByText('Riega temprano')).toBeInTheDocument();
  });

  it('muestra el error si falla la carga', async () => {
    blogService.getAll.mockRejectedValue(new Error('Sin conexión'));
    render(<BlogManager token="tk" />);
    expect(await screen.findByText('Sin conexión')).toHaveClass('alert-danger');
  });

  it('publica una noticia, la agrega a la lista y limpia el formulario', async () => {
    blogService.create.mockResolvedValue({ id: 2, titulo: 'Compost', descripcion: 'Aprende a hacer compost' });
    render(<BlogManager token="tk" />);
    await screen.findByText('Riego eficiente');
    fireEvent.change(screen.getByLabelText('Título'), { target: { value: 'Compost' } });
    fireEvent.change(screen.getByLabelText('Imagen URL'), { target: { value: 'http://x.cl/a.png' } });
    fireEvent.change(screen.getByLabelText('Contenido'), { target: { value: 'Aprende a hacer compost' } });
    fireEvent.click(screen.getByRole('button', { name: 'Publicar' }));
    expect(await screen.findByText('Noticia publicada.')).toBeInTheDocument();
    expect(blogService.create).toHaveBeenCalledWith(
      { titulo: 'Compost', descripcion: 'Aprende a hacer compost', imagenUrl: 'http://x.cl/a.png' }, 'tk');
    expect(screen.getByText('Compost')).toBeInTheDocument();
    expect(screen.getByLabelText('Título')).toHaveValue('');
  });

  it('informa el error al fallar la publicación', async () => {
    blogService.create.mockRejectedValue(new Error('Título duplicado'));
    render(<BlogManager token="tk" />);
    await screen.findByText('Riego eficiente');
    fireEvent.change(screen.getByLabelText('Título'), { target: { value: 'X' } });
    fireEvent.change(screen.getByLabelText('Contenido'), { target: { value: 'Y' } });
    fireEvent.click(screen.getByRole('button', { name: 'Publicar' }));
    expect(await screen.findByText('Título duplicado')).toBeInTheDocument();
    expect(screen.getByLabelText('Título')).toHaveValue('X');
  });

  it('desactiva una noticia sólo si se confirma', async () => {
    blogService.delete.mockResolvedValue('ok');
    const confirmSpy = vi.spyOn(window, 'confirm').mockReturnValueOnce(false).mockReturnValueOnce(true);
    render(<BlogManager token="tk" />);
    fireEvent.click(await screen.findByRole('button', { name: 'Desactivar' }));
    expect(blogService.delete).not.toHaveBeenCalled();
    fireEvent.click(screen.getByRole('button', { name: 'Desactivar' }));
    await waitFor(() => expect(screen.queryByText('Riego eficiente')).not.toBeInTheDocument());
    expect(blogService.delete).toHaveBeenCalledWith(1, 'tk');
    expect(confirmSpy).toHaveBeenCalledTimes(2);
  });

  it('informa el error al fallar la eliminación', async () => {
    blogService.delete.mockRejectedValue(new Error('Prohibido'));
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    render(<BlogManager token="tk" />);
    fireEvent.click(await screen.findByRole('button', { name: 'Desactivar' }));
    expect(await screen.findByText('Prohibido')).toBeInTheDocument();
    expect(screen.getByText('Riego eficiente')).toBeInTheDocument();
  });
});
