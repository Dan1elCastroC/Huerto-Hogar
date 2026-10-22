import { describe, expect, it } from 'vitest';
import { productoService } from './productoService';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8087';

describe('Integración real con el backend Huerto Hogar', () => {
  it('consulta productos reales del backend y no datos simulados', async () => {
    const response = await fetch(`${API_URL}/api/v1/entities/productos`);
    expect(response.ok, `Backend no disponible en ${API_URL}. Inicia Spring Boot antes de ejecutar esta prueba.`).toBe(true);
    const data = await response.json();
    expect(Array.isArray(data)).toBe(true);
    expect(data.length).toBeGreaterThan(0);
    expect(data.some((p) => p.codigo === 'KIT-001')).toBe(true);
  });

  it('consulta categorías reales del backend', async () => {
    const response = await fetch(`${API_URL}/api/v1/entities/categorias`);
    expect(response.ok, `No se pudieron consultar categorías en ${API_URL}`).toBe(true);
    const data = await response.json();
    expect(Array.isArray(data)).toBe(true);
    expect(data.length).toBeGreaterThan(0);
  });
});
