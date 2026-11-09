import { fireEvent, render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import LandingPage from './LandingPage';

const productos = [
  { id: 1, nombre: 'Manzanas', categoria: 'Frutas', precio: 2000, stock: 5, imagenUrl: '/m.png' },
  { id: 2, nombre: 'Peras', categoria: 'Frutas', precio: 1500, stock: 0 },
  { id: 3, nombre: 'Kiwis', categoria: 'Frutas', precio: 3000, stock: 2, imagen: '/k.png' },
  { id: 4, nombre: 'Uvas', categoria: 'Frutas', precio: 900, stock: 2 },
];

describe('LandingPage', () => {
  it('muestra hero y características sin productos destacados cuando no hay productos', () => {
    render(<LandingPage cambiarVista={vi.fn()} />);
    expect(screen.getByRole('heading', { name: 'Del campo a tu hogar' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: 'Productos Frescos' })).toBeInTheDocument();
    expect(screen.queryByText('Productos destacados')).not.toBeInTheDocument();
  });

  it('navega al catálogo desde los dos botones', () => {
    const cambiarVista = vi.fn();
    render(<LandingPage cambiarVista={cambiarVista} productos={productos} />);
    fireEvent.click(screen.getByRole('button', { name: 'Ver productos' }));
    fireEvent.click(screen.getByRole('button', { name: 'Ver todos los productos' }));
    expect(cambiarVista).toHaveBeenCalledTimes(2);
    expect(cambiarVista).toHaveBeenCalledWith('catalogo');
  });

  it('destaca sólo los 3 primeros productos con precio e imagen alternativa', () => {
    render(<LandingPage cambiarVista={vi.fn()} productos={productos} />);
    expect(screen.getByRole('heading', { name: 'Productos destacados' })).toBeInTheDocument();
    expect(screen.getByText('Manzanas')).toBeInTheDocument();
    expect(screen.getByText('Kiwis')).toBeInTheDocument();
    expect(screen.queryByText('Uvas')).not.toBeInTheDocument();
    expect(screen.getByText('$2.000')).toBeInTheDocument();
    expect(screen.getByAltText('Kiwis')).toHaveAttribute('src', '/k.png');
    expect(screen.getByAltText('Peras')).toHaveAttribute('src', '/img/huerto-hogar1.png');
  });

  it('agrega al carrito, deshabilita sin stock y abre el detalle', () => {
    const agregarAlCarrito = vi.fn();
    const onVerDetalle = vi.fn();
    render(<LandingPage cambiarVista={vi.fn()} productos={productos} agregarAlCarrito={agregarAlCarrito} onVerDetalle={onVerDetalle} />);
    const botones = screen.getAllByRole('button', { name: 'Añadir al carrito' });
    expect(botones[1]).toBeDisabled();
    fireEvent.click(botones[0]);
    expect(agregarAlCarrito).toHaveBeenCalledWith(productos[0]);
    fireEvent.click(screen.getByRole('button', { name: 'Ver detalle de Manzanas' }));
    expect(onVerDetalle).toHaveBeenCalledWith(productos[0]);
  });

  it('no falla al añadir si no se entrega agregarAlCarrito ni onVerDetalle', () => {
    render(<LandingPage cambiarVista={vi.fn()} productos={productos} />);
    expect(screen.queryByRole('button', { name: /Ver detalle de/ })).not.toBeInTheDocument();
    fireEvent.click(screen.getAllByRole('button', { name: 'Añadir al carrito' })[0]);
  });
});
