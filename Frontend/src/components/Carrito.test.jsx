import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import Carrito from './Carrito';
import { cuponService } from '../services/cuponService';

vi.mock('../services/cuponService', () => ({ cuponService: { validate: vi.fn() } }));

const carrito = [{
  id: 1,
  nombre: 'Kit de Huerto',
  precio: 5000,
  cantidad: 1,
  stock: 3,
}];

const baseProps = () => ({
  carrito,
  user: { id: 3, direccion: 'Av. Central 10' },
  cambiarVista: vi.fn(),
  vaciarCarrito: vi.fn(),
  cambiarCantidad: vi.fn(),
  eliminarDelCarrito: vi.fn(),
  finalizarCompra: vi.fn(),
  sincronizandoCarrito: false,
});

describe('Carrito', () => {
  beforeEach(() => { vi.clearAllMocks(); });
  it('muestra el total y permite modificar el carrito', () => {
    const props = baseProps();
    render(<Carrito {...props} />);

    expect(screen.getByText('Kit de Huerto')).toBeInTheDocument();
    expect(screen.getAllByText(`$${(5000).toLocaleString()}`)).toHaveLength(3);
    fireEvent.click(screen.getByRole('button', { name: 'Aumentar cantidad de Kit de Huerto' }));
    fireEvent.click(screen.getByRole('button', { name: 'Eliminar Kit de Huerto' }));

    expect(props.cambiarCantidad).toHaveBeenCalledWith(1, 2);
    expect(props.eliminarDelCarrito).toHaveBeenCalledWith(1);
  });

  it('confirma una compra sin realizar llamadas de red desde el componente', async () => {
    const props = baseProps();
    props.finalizarCompra.mockResolvedValue({ id: 20, direccionEntrega: 'Calle Nueva 20', total: 5000 });
    render(<Carrito {...props} />);
    fireEvent.click(screen.getByRole('button', { name: 'Proceder al checkout' }));
    fireEvent.change(screen.getByLabelText('Dirección de entrega'), { target: { value: 'Calle Nueva 20' } });
    fireEvent.click(screen.getByRole('button', { name: 'Confirmar compra' }));

    await waitFor(() => expect(props.finalizarCompra).toHaveBeenCalledWith({
      direccionEntrega: 'Calle Nueva 20',
      fechaEntregaDeseada: undefined,
      codigoCupon: undefined,
    }));
    expect(await screen.findByText('¡Compra realizada con éxito!')).toBeInTheDocument();
  });

  it('muestra el estado vacío cuando no hay productos', () => {
    render(<Carrito {...baseProps()} carrito={[]} />);

    expect(screen.getByText('Tu carrito está vacío')).toBeInTheDocument();
  });

  it('aplica un cupón validado y actualiza el descuento al cambiar cantidades', async () => {
    const props = baseProps();
    cuponService.validate.mockResolvedValue({ codigo: 'AHORRO10', porcentaje: '10.00' });
    const vista = render(<Carrito {...props} />);
    fireEvent.click(screen.getByRole('button', { name: 'Proceder al checkout' }));
    fireEvent.change(screen.getByLabelText(/Cupón/), { target: { value: ' ahorro10 ' } });
    fireEvent.click(screen.getByRole('button', { name: 'Aplicar cupón' }));

    await screen.findByText('Descuento (AHORRO10)');
    expect(cuponService.validate).toHaveBeenCalledWith('ahorro10');
    expect(screen.getByText('Descuento (AHORRO10)').parentElement).toHaveTextContent(`-$${(500).toLocaleString()}`);
    expect(screen.getByText('Total', { exact: true }).parentElement).toHaveTextContent(`$${(4500).toLocaleString()}`);
    vista.rerender(<Carrito {...props} carrito={[{ ...carrito[0], cantidad: 2 }]} />);
    expect(screen.getByText('Descuento (AHORRO10)').parentElement).toHaveTextContent(`-$${(1000).toLocaleString()}`);
    expect(screen.getByText('Total', { exact: true }).parentElement).toHaveTextContent(`$${(9000).toLocaleString()}`);
  });

  it.each([[33.9, 15, 5.09, 28.81], [100.5, 1, 1.01, 99.49], [5000, 33.33, 1666.5, 3333.5]])(
    'redondea a dos decimales como el backend para subtotal %s y porcentaje %s',
    async (precio, porcentaje, descuento, total) => {
      cuponService.validate.mockResolvedValue({ codigo: 'REDONDEO', porcentaje });
      render(<Carrito {...baseProps()} carrito={[{ ...carrito[0], precio }]} />);
      fireEvent.click(screen.getByRole('button', { name: 'Proceder al checkout' }));
      fireEvent.change(screen.getByLabelText(/Cupón/), { target: { value: 'REDONDEO' } });
      fireEvent.click(screen.getByRole('button', { name: 'Aplicar cupón' }));

      await screen.findByText('Descuento (REDONDEO)');
      expect(screen.getByText('Descuento (REDONDEO)').parentElement).toHaveTextContent(`-$${descuento.toLocaleString()}`);
      expect(screen.getByText('Total', { exact: true }).parentElement).toHaveTextContent(`$${total.toLocaleString()}`);
    },
  );

  it('quita el cupón y no lo envía al confirmar la compra', async () => {
    const props = baseProps();
    props.finalizarCompra.mockResolvedValue({ id: 21, direccionEntrega: props.user.direccion, total: 5000 });
    cuponService.validate.mockResolvedValue({ codigo: 'AHORRO10', porcentaje: 10 });
    render(<Carrito {...props} />);
    fireEvent.click(screen.getByRole('button', { name: 'Proceder al checkout' }));
    fireEvent.change(screen.getByLabelText(/Cupón/), { target: { value: 'AHORRO10' } });
    fireEvent.click(screen.getByRole('button', { name: 'Aplicar cupón' }));
    await screen.findByText('Descuento (AHORRO10)');

    fireEvent.click(screen.getByRole('button', { name: 'Quitar cupón' }));

    expect(screen.queryByText('Descuento (AHORRO10)')).not.toBeInTheDocument();
    expect(screen.getByText('Total', { exact: true }).parentElement).toHaveTextContent(`$${(5000).toLocaleString()}`);
    expect(screen.getByLabelText(/Cupón/)).toHaveValue('');
    fireEvent.click(screen.getByRole('button', { name: 'Confirmar compra' }));
    await waitFor(() => expect(props.finalizarCompra).toHaveBeenCalledWith(expect.objectContaining({ codigoCupon: undefined })));
  });

  it.each(['El cupón ha expirado', 'Cupón inválido o expirado', 'Failed to fetch'])(
    'muestra el error %s y permite comprar sin cupón', async (mensaje) => {
      const props = baseProps();
      props.finalizarCompra.mockResolvedValue({ id: 22, direccionEntrega: props.user.direccion, total: 5000 });
      cuponService.validate.mockRejectedValue(new Error(mensaje));
      render(<Carrito {...props} />);
      fireEvent.click(screen.getByRole('button', { name: 'Proceder al checkout' }));
      fireEvent.change(screen.getByLabelText(/Cupón/), { target: { value: 'INVALIDO' } });
      fireEvent.click(screen.getByRole('button', { name: 'Aplicar cupón' }));

      expect(await screen.findByRole('alert')).toHaveTextContent(mensaje);
      expect(screen.getByText('Total', { exact: true }).parentElement).toHaveTextContent(`$${(5000).toLocaleString()}`);
      fireEvent.click(screen.getByRole('button', { name: 'Confirmar compra' }));
      await waitFor(() => expect(props.finalizarCompra).toHaveBeenCalledWith(expect.objectContaining({ codigoCupon: undefined })));
    },
  );

  it('no envía un código escrito que todavía no fue aplicado', async () => {
    const props = baseProps();
    props.finalizarCompra.mockResolvedValue({ id: 23, direccionEntrega: props.user.direccion, total: 5000 });
    render(<Carrito {...props} />);
    fireEvent.click(screen.getByRole('button', { name: 'Proceder al checkout' }));
    fireEvent.change(screen.getByLabelText(/Cupón/), { target: { value: 'NOAPLICADO' } });
    fireEvent.click(screen.getByRole('button', { name: 'Confirmar compra' }));

    await waitFor(() => expect(props.finalizarCompra).toHaveBeenCalledWith(expect.objectContaining({ codigoCupon: undefined })));
    expect(cuponService.validate).not.toHaveBeenCalled();
  });

  it('envía el código canónico y muestra un vencimiento detectado al confirmar', async () => {
    const props = baseProps();
    props.finalizarCompra.mockRejectedValue(new Error('El cupón ha expirado'));
    cuponService.validate.mockResolvedValue({ codigo: 'AHORRO10', porcentaje: 10 });
    render(<Carrito {...props} />);
    fireEvent.click(screen.getByRole('button', { name: 'Proceder al checkout' }));
    fireEvent.change(screen.getByLabelText(/Cupón/), { target: { value: 'ahorro10' } });
    fireEvent.click(screen.getByRole('button', { name: 'Aplicar cupón' }));
    await screen.findByText('Descuento (AHORRO10)');
    fireEvent.click(screen.getByRole('button', { name: 'Confirmar compra' }));

    await waitFor(() => expect(props.finalizarCompra).toHaveBeenCalledWith(expect.objectContaining({ codigoCupon: 'AHORRO10' })));
    expect(await screen.findByText('El cupón ha expirado')).toBeInTheDocument();
    expect(screen.queryByText('¡Compra realizada con éxito!')).not.toBeInTheDocument();
  });

  it('muestra los datos reales de la cuenta y la dirección autocompletada', () => {
    render(<Carrito {...baseProps()} user={{ nombre: 'Ana', apellidos: 'Pérez', correo: 'ana@correo.cl', direccion: 'Av. Central 10' }} />);
    fireEvent.click(screen.getByRole('button', { name: 'Proceder al checkout' }));

    expect(screen.getByText('Ana Pérez')).toBeInTheDocument();
    expect(screen.getByText('ana@correo.cl')).toBeInTheDocument();
    expect(screen.getByLabelText('Dirección de entrega')).toHaveValue('Av. Central 10');
    expect(screen.getByLabelText('Modalidad de entrega')).toHaveValue('proxima');
    expect(screen.queryByLabelText('Fecha deseada')).not.toBeInTheDocument();
  });

  it('envía la fecha elegida al programar la entrega', async () => {
    const props = baseProps();
    props.finalizarCompra.mockResolvedValue({ id: 24, direccionEntrega: props.user.direccion, total: 5000 });
    render(<Carrito {...props} />);
    fireEvent.click(screen.getByRole('button', { name: 'Proceder al checkout' }));
    fireEvent.change(screen.getByLabelText('Modalidad de entrega'), { target: { value: 'programada' } });
    expect(screen.getByLabelText('Fecha deseada')).toBeRequired();
    fireEvent.change(screen.getByLabelText('Fecha deseada'), { target: { value: '2099-12-31' } });
    fireEvent.click(screen.getByRole('button', { name: 'Confirmar compra' }));

    await waitFor(() => expect(props.finalizarCompra).toHaveBeenCalledWith(expect.objectContaining({ fechaEntregaDeseada: '2099-12-31' })));
  });

  it('limpia la fecha al cambiar a próxima disponibilidad y no la envía', async () => {
    const props = baseProps();
    props.finalizarCompra.mockResolvedValue({ id: 25, direccionEntrega: props.user.direccion, total: 5000 });
    render(<Carrito {...props} />);
    fireEvent.click(screen.getByRole('button', { name: 'Proceder al checkout' }));
    fireEvent.change(screen.getByLabelText('Modalidad de entrega'), { target: { value: 'programada' } });
    fireEvent.change(screen.getByLabelText('Fecha deseada'), { target: { value: '2099-12-31' } });
    fireEvent.change(screen.getByLabelText('Modalidad de entrega'), { target: { value: 'proxima' } });
    fireEvent.change(screen.getByLabelText('Modalidad de entrega'), { target: { value: 'programada' } });
    expect(screen.getByLabelText('Fecha deseada')).toHaveValue('');
    fireEvent.change(screen.getByLabelText('Modalidad de entrega'), { target: { value: 'proxima' } });
    fireEvent.click(screen.getByRole('button', { name: 'Confirmar compra' }));

    await waitFor(() => expect(props.finalizarCompra).toHaveBeenCalledWith(expect.objectContaining({ fechaEntregaDeseada: undefined })));
  });

  it.each(['', '2000-01-01'])('rechaza una entrega programada sin fecha válida: %s', (fecha) => {
    const props = baseProps();
    render(<Carrito {...props} />);
    fireEvent.click(screen.getByRole('button', { name: 'Proceder al checkout' }));
    fireEvent.change(screen.getByLabelText('Modalidad de entrega'), { target: { value: 'programada' } });
    fireEvent.change(screen.getByLabelText('Fecha deseada'), { target: { value: fecha } });
    fireEvent.submit(screen.getByRole('button', { name: 'Confirmar compra' }).closest('form'));

    expect(props.finalizarCompra).not.toHaveBeenCalled();
    expect(screen.getByRole('alert')).toHaveTextContent('Selecciona una fecha de entrega desde hoy.');
  });

  it('usa el día local al programar una entrega de noche', () => {
    vi.useFakeTimers();
    try {
      vi.setSystemTime(new Date(2070, 0, 1, 23, 30));
      render(<Carrito {...baseProps()} />);
      fireEvent.click(screen.getByRole('button', { name: 'Proceder al checkout' }));
      fireEvent.change(screen.getByLabelText('Modalidad de entrega'), { target: { value: 'programada' } });

      expect(screen.getByLabelText('Fecha deseada')).toHaveAttribute('min', '2070-01-01');
    } finally {
      vi.useRealTimers();
    }
  });

  it('mantiene el acceso a iniciar sesión para un carrito anónimo', () => {
    const props = baseProps();
    render(<Carrito {...props} user={null} />);
    fireEvent.click(screen.getByRole('button', { name: 'Proceder al checkout' }));
    expect(screen.queryByRole('button', { name: 'Confirmar compra' })).not.toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Iniciar sesión' }));

    expect(props.cambiarVista).toHaveBeenCalledWith('login');
    expect(props.finalizarCompra).not.toHaveBeenCalled();
  });
});
