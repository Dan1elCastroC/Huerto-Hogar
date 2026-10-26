import { fireEvent, render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import ProductList from './ProductList';

const productos = [
  { id: 1, nombre: 'Kit de Huerto', precio: 12000, stock: 4, categoriaNombre: 'Huertos' },
  { id: 2, nombre: 'Semillas de tomate', precio: 2500, stock: 10, categoriaNombre: 'Semillas' },
];

describe('ProductList', () => {
  it('muestra los productos y permite agregarlos al carrito', () => {
    const agregarAlCarrito = vi.fn();
    render(<ProductList productos={productos} categorias={[]} agregarAlCarrito={agregarAlCarrito} loading={false} />);

    expect(screen.getByText('Kit de Huerto')).toBeInTheDocument();
    expect(screen.getByText('Semillas de tomate')).toBeInTheDocument();
    fireEvent.click(screen.getAllByRole('button', { name: 'Añadir al Carrito' })[0]);

    expect(agregarAlCarrito).toHaveBeenCalledWith(productos[0]);
  });

  it('filtra por texto y categoría', () => {
    render(<ProductList productos={productos} categorias={[]} agregarAlCarrito={vi.fn()} loading={false} />);

    fireEvent.change(screen.getByLabelText('Buscar productos'), { target: { value: 'tomate' } });
    expect(screen.getByText('Semillas de tomate')).toBeInTheDocument();
    expect(screen.queryByText('Kit de Huerto')).not.toBeInTheDocument();

    fireEvent.change(screen.getByLabelText('Buscar productos'), { target: { value: '' } });
    fireEvent.change(screen.getByLabelText('Filtrar por categoría'), { target: { value: 'Huertos' } });
    expect(screen.getByText('Kit de Huerto')).toBeInTheDocument();
    expect(screen.queryByText('Semillas de tomate')).not.toBeInTheDocument();
  });

  it('informa cuando los productos están cargando', () => {
    render(<ProductList productos={[]} agregarAlCarrito={vi.fn()} loading />);

    expect(screen.getByRole('status')).toBeInTheDocument();
    expect(screen.getByText('Cargando productos desde el servidor...')).toBeInTheDocument();
  });

  it('muestra el error de conexión sin ofrecer productos', () => {
    render(<ProductList productos={[]} agregarAlCarrito={vi.fn()} loading={false} errorProductos="No se pudieron cargar los productos desde el backend." />);

    expect(screen.getByRole('alert')).toHaveTextContent('No se pudieron cargar los productos desde el backend.');
    expect(screen.queryByText('No hay productos disponibles en este momento.')).not.toBeInTheDocument();
  });
});
