import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import CouponManager from './CouponManager';
import { cuponService } from '../../services/cuponService';

vi.mock('../../services/cuponService', () => ({ cuponService: { getAll: vi.fn(), create: vi.fn(), delete: vi.fn() } }));

describe('CouponManager', () => {
  beforeEach(() => {
    cuponService.getAll.mockResolvedValue([
      { id: 1, codigo: 'VERANO10', porcentaje: 10, fechaExpiracion: '2099-01-01' },
      { id: 2, codigo: 'SIEMPRE', porcentaje: 5, fechaExpiracion: null },
    ]);
  });

  it('lista cupones con porcentaje y vigencia', async () => {
    render(<CouponManager token="tk" />);
    expect(await screen.findByText('VERANO10')).toBeInTheDocument();
    expect(screen.getByText('10%')).toBeInTheDocument();
    expect(screen.getByText('2099-01-01')).toBeInTheDocument();
    expect(screen.getByText('Sin vencimiento')).toBeInTheDocument();
    expect(cuponService.getAll).toHaveBeenCalledWith('tk');
  });

  it('muestra error de carga', async () => {
    cuponService.getAll.mockRejectedValue(new Error('No autorizado'));
    render(<CouponManager token="tk" />);
    expect(await screen.findByText('No autorizado')).toHaveClass('alert-danger');
  });

  it('crea un cupón con código en mayúsculas y porcentaje numérico', async () => {
    cuponService.create.mockResolvedValue({ id: 3, codigo: 'NUEVO', porcentaje: 15, fechaExpiracion: null });
    render(<CouponManager token="tk" />);
    await screen.findByText('VERANO10');
    fireEvent.change(screen.getByLabelText('Código'), { target: { value: 'nuevo' } });
    expect(screen.getByLabelText('Código')).toHaveValue('NUEVO');
    fireEvent.change(screen.getByLabelText('Descuento (%)'), { target: { value: '15' } });
    fireEvent.click(screen.getByRole('button', { name: 'Crear' }));
    expect(await screen.findByText('Cupón creado correctamente.')).toBeInTheDocument();
    expect(cuponService.create).toHaveBeenCalledWith({ codigo: 'NUEVO', porcentaje: 15, fechaExpiracion: null }, 'tk');
    expect(screen.getByText('NUEVO')).toBeInTheDocument();
    expect(screen.getByLabelText('Código')).toHaveValue('');
  });

  it('envía la fecha de expiración cuando se indica', async () => {
    cuponService.create.mockResolvedValue({ id: 4, codigo: 'F', porcentaje: 1, fechaExpiracion: '2030-05-05' });
    render(<CouponManager token="tk" />);
    await screen.findByText('VERANO10');
    fireEvent.change(screen.getByLabelText('Código'), { target: { value: 'f' } });
    fireEvent.change(screen.getByLabelText('Descuento (%)'), { target: { value: '1' } });
    fireEvent.change(screen.getByLabelText('Expiración'), { target: { value: '2030-05-05' } });
    fireEvent.click(screen.getByRole('button', { name: 'Crear' }));
    await waitFor(() => expect(cuponService.create).toHaveBeenCalledWith(
      { codigo: 'F', porcentaje: 1, fechaExpiracion: '2030-05-05' }, 'tk'));
  });

  it('muestra el error si falla la creación', async () => {
    cuponService.create.mockRejectedValue(new Error('Código repetido'));
    render(<CouponManager token="tk" />);
    await screen.findByText('VERANO10');
    fireEvent.change(screen.getByLabelText('Código'), { target: { value: 'a' } });
    fireEvent.change(screen.getByLabelText('Descuento (%)'), { target: { value: '5' } });
    fireEvent.click(screen.getByRole('button', { name: 'Crear' }));
    expect(await screen.findByText('Código repetido')).toBeInTheDocument();
  });

  it('desactiva un cupón tras confirmar y respeta la cancelación', async () => {
    cuponService.delete.mockResolvedValue('ok');
    vi.spyOn(window, 'confirm').mockReturnValueOnce(false).mockReturnValueOnce(true);
    render(<CouponManager token="tk" />);
    await screen.findByText('VERANO10');
    fireEvent.click(screen.getAllByRole('button', { name: 'Desactivar' })[0]);
    expect(cuponService.delete).not.toHaveBeenCalled();
    fireEvent.click(screen.getAllByRole('button', { name: 'Desactivar' })[0]);
    await waitFor(() => expect(screen.queryByText('VERANO10')).not.toBeInTheDocument());
    expect(cuponService.delete).toHaveBeenCalledWith(1, 'tk');
    expect(screen.getByText('SIEMPRE')).toBeInTheDocument();
  });

  it('muestra error si falla la desactivación', async () => {
    cuponService.delete.mockRejectedValue(new Error('Fallo servidor'));
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    render(<CouponManager token="tk" />);
    await screen.findByText('VERANO10');
    fireEvent.click(screen.getAllByRole('button', { name: 'Desactivar' })[0]);
    expect(await screen.findByText('Fallo servidor')).toBeInTheDocument();
    expect(screen.getByText('VERANO10')).toBeInTheDocument();
  });
});
