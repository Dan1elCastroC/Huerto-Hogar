import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import Carrito from './Carrito';

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
});
