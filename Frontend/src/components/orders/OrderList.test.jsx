import { fireEvent, render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import OrderList from './OrderList';

describe('OrderList', () => {
  it('permite abrir el detalle de un pedido', () => {
    render(<OrderList pedidos={[{
      id: 12,
      estado: 'ENVIADO',
      total: 9000,
      direccionEntrega: 'Calle 1',
      detalles: [{ productoId: 4, productoNombre: 'Semillas', cantidad: 2 }],
    }]} loading={false} />);

    const pedido = screen.getByRole('button', { name: /Pedido #12/ });
    expect(pedido).toHaveAttribute('aria-expanded', 'false');
    fireEvent.click(pedido);

    expect(pedido).toHaveAttribute('aria-expanded', 'true');
    expect(screen.getByText('Semillas · 2 unidad(es)')).toBeInTheDocument();
  });

  it('muestra el estado vacío', () => {
    render(<OrderList pedidos={[]} loading={false} />);

    expect(screen.getByRole('status')).toHaveTextContent('Todavía no tienes pedidos registrados.');
  });
});
