import { describe, expect, it } from 'vitest';
import {
  addItemToCart,
  getCartTotal,
  removeCartItem,
  updateCartItem,
} from './cartUtils';

describe('cartUtils', () => {
  it('adds a new product with one unit without mutating the cart', () => {
    const cart = [];
    const product = { id: 1, nombre: 'Semillas', precio: 2500, stock: 3 };

    const result = addItemToCart(cart, product);

    expect(result).toEqual([{ ...product, cantidad: 1 }]);
    expect(cart).toEqual([]);
  });

  it('increments an existing product but never exceeds stock', () => {
    const cart = [{ id: 1, precio: 2500, stock: 2, cantidad: 2 }];

    const result = addItemToCart(cart, { id: 1 });

    expect(result).toEqual([{ id: 1, precio: 2500, stock: 2, cantidad: 2 }]);
  });

  it('updates quantity and removes the product when quantity reaches zero', () => {
    const cart = [
      { id: 1, precio: 2500, stock: 5, cantidad: 1 },
      { id: 2, precio: 1000, stock: 5, cantidad: 1 },
    ];

    expect(updateCartItem(cart, 1, 4)[0].cantidad).toBe(4);
    expect(updateCartItem(cart, 1, 99)[0].cantidad).toBe(5);
    expect(updateCartItem(cart, 1, 0)).toEqual([cart[1]]);
  });

  it('removes only the selected product', () => {
    const cart = [{ id: 1, cantidad: 1 }, { id: 2, cantidad: 2 }];

    expect(removeCartItem(cart, 1)).toEqual([{ id: 2, cantidad: 2 }]);
  });

  it('calculates the total using numeric and string prices', () => {
    const cart = [
      { precio: 2500, cantidad: 2 },
      { precio: '1000', cantidad: 3 },
    ];

    expect(getCartTotal(cart)).toBe(8000);
  });
});
