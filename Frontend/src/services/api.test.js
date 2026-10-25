import { afterEach, describe, expect, it, vi } from 'vitest';
import { productoService } from './api';

const jsonResponse = (body, ok = true) => ({
  ok,
  headers: { get: () => 'application/json' },
  json: async () => body,
  text: async () => JSON.stringify(body),
});

describe('productoService', () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it('normalizes the backend product shape', async () => {
    const fetchMock = vi.fn().mockResolvedValue(jsonResponse([
      { id: 7, nombre: 'Macetero', precio: 5000, categoriaNombre: 'Hogar', imagenUrl: '/macetero.png' },
    ]));
    vi.stubGlobal('fetch', fetchMock);

    const products = await productoService.getAll();

    expect(products[0]).toMatchObject({
      id: 7,
      categoria: 'Hogar',
      categoriaNombre: 'Hogar',
      imagen: '/macetero.png',
      imagenUrl: '/macetero.png',
    });
    expect(fetchMock).toHaveBeenCalledWith(
      'http://localhost:8087/api/v1/entities/productos',
      expect.objectContaining({
        headers: { 'Content-Type': 'application/json' },
      }),
    );
  });

  it('propagates the error when the backend request fails (no fake data)', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(jsonResponse({}, false)));

    await expect(productoService.getAll()).rejects.toThrow();
  });

  it('sends a token when creating a product and normalizes the response', async () => {
    const fetchMock = vi.fn().mockResolvedValue(jsonResponse({
      id: 9,
      nombre: 'Regadera',
      categoria: 'Jardín',
      imagen: '/regadera.png',
    }));
    vi.stubGlobal('fetch', fetchMock);

    const product = await productoService.create({ nombre: 'Regadera' }, 'token-demo');

    expect(product).toMatchObject({ categoriaNombre: 'Jardín', imagenUrl: '/regadera.png' });
    expect(fetchMock).toHaveBeenCalledWith(
      'http://localhost:8087/api/v1/entities/productos',
      expect.objectContaining({
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer token-demo',
        },
      }),
    );
  });
});
