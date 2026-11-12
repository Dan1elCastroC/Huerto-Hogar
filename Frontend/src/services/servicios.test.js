import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { authService } from './authService';
import { blogService } from './blogService';
import { carritoService } from './carritoService';
import { categoriaService } from './categoriaService';
import { contactoService } from './contactoService';
import { cuponService } from './cuponService';
import { pedidoService } from './pedidoService';
import productoServiceDefault, {
  productoService, normalizeProduct, getProductos, agregarProducto, eliminarProducto,
} from './productoService';
import { regionService } from './regionService';
import { reporteService } from './reporteService';
import { usuarioService } from './usuarioService';
import { request, getHeaders, parseResponse, EVENTO_SESION_EXPIRADA } from './httpClient';

const BASE = 'http://localhost:8087';
const T = 'tok123';

const jsonRes = (body, ok = true, status = 200) => ({
  ok,
  status,
  headers: { get: () => 'application/json' },
  json: async () => body,
  text: async () => JSON.stringify(body),
});

let fetchMock;
beforeEach(() => {
  fetchMock = vi.fn().mockResolvedValue(jsonRes([]));
  vi.stubGlobal('fetch', fetchMock);
});
afterEach(() => vi.unstubAllGlobals());

// [servicio, metodo, argumentos, URL esperada, HTTP, usaToken, cuerpo]
const casos = [
  ['authService', 'login', [{ correo: 'a@b.cl', contrasena: 'x', extra: 1 }], '/api/auth/login', 'POST', false, { correo: 'a@b.cl', contrasena: 'x' }],
  ['authService', 'registro', [{ nombre: 'Ana' }], '/api/auth/registro', 'POST', false, { nombre: 'Ana' }],
  ['authService', 'getPerfil', [T], '/api/usuarios/perfil', undefined, true],

  ['blogService', 'getAll', [], '/api/v1/entities/blogs'],
  ['blogService', 'getById', [3], '/api/v1/entities/blogs/3'],
  ['blogService', 'create', [{ titulo: 't' }, T], '/api/v1/entities/blogs', 'POST', true, { titulo: 't' }],
  ['blogService', 'update', [3, { titulo: 'u' }, T], '/api/v1/entities/blogs/3', 'PUT', true, { titulo: 'u' }],
  ['blogService', 'delete', [3, T], '/api/v1/entities/blogs/3', 'DELETE', true],

  ['carritoService', 'get', [T], '/api/v1/entities/carrito', undefined, true],
  ['carritoService', 'get', [T, 'VERANO 10'], '/api/v1/entities/carrito?cupon=VERANO%2010', undefined, true],
  ['carritoService', 'add', [5, 2, T], '/api/v1/entities/carrito/agregar', 'POST', true, { productoId: 5, cantidad: 2 }],
  ['carritoService', 'update', [5, 4, T], '/api/v1/entities/carrito/actualizar/5?cantidad=4', 'PUT', true],
  ['carritoService', 'remove', [5, T], '/api/v1/entities/carrito/eliminar/5', 'DELETE', true],
  ['carritoService', 'clear', [T], '/api/v1/entities/carrito/vaciar', 'DELETE', true],

  ['categoriaService', 'getAll', [], '/api/v1/entities/categorias'],
  ['categoriaService', 'getById', [2], '/api/v1/entities/categorias/2'],
  ['categoriaService', 'create', [{ nombre: 'c' }, T], '/api/v1/entities/categorias', 'POST', true, { nombre: 'c' }],
  ['categoriaService', 'update', [2, { nombre: 'd' }, T], '/api/v1/entities/categorias/2', 'PUT', true, { nombre: 'd' }],
  ['categoriaService', 'delete', [2, T], '/api/v1/entities/categorias/2', 'DELETE', true],

  ['contactoService', 'create', [{ mensaje: 'hola' }], '/api/v1/entities/contacto', 'POST', false, { mensaje: 'hola' }],
  ['contactoService', 'create', [{ mensaje: 'hola' }, T], '/api/v1/entities/contacto', 'POST', true, { mensaje: 'hola' }],
  ['contactoService', 'getAll', [T], '/api/v1/entities/contacto', undefined, true],
  ['contactoService', 'markRead', [9, T], '/api/v1/entities/contacto/9/leido', 'PATCH', true],

  ['cuponService', 'getAll', [T], '/api/v1/entities/cupones', undefined, true],
  ['cuponService', 'validate', ['A B/C'], '/api/v1/entities/cupones/validar/A%20B%2FC'],
  ['cuponService', 'create', [{ codigo: 'X' }, T], '/api/v1/entities/cupones', 'POST', true, { codigo: 'X' }],
  ['cuponService', 'delete', [4, T], '/api/v1/entities/cupones/4', 'DELETE', true],

  ['pedidoService', 'crearPedido', [{ direccion: 'd' }, T], '/api/v1/entities/pedidos/checkout', 'POST', true, { direccion: 'd' }],
  ['pedidoService', 'historial', [T], '/api/v1/entities/pedidos/mis-pedidos', undefined, true],
  ['pedidoService', 'getById', [8, T], '/api/v1/entities/pedidos/mis-pedidos/8', undefined, true],
  ['pedidoService', 'getAll', [T], '/api/v1/entities/pedidos', undefined, true],
  ['pedidoService', 'byStatus', ['EN CAMINO', T], '/api/v1/entities/pedidos/estado/EN%20CAMINO', undefined, true],
  ['pedidoService', 'updateStatus', [8, 'ENTREGADO', T], '/api/v1/entities/pedidos/8/estado?estado=ENTREGADO', 'PATCH', true],

  ['regionService', 'getAll', [], '/api/regiones'],
  ['regionService', 'getCommunes', ['Región Metropolitana'], '/api/regiones/Regi%C3%B3n%20Metropolitana/comunas'],
  ['regionService', 'getAllWithCommunes', [], '/api/regiones/todas'],

  ['reporteService', 'resumen', [T], '/api/v1/entities/reportes/resumen', undefined, true],
  ['reporteService', 'stockCritico', [T], '/api/v1/entities/reportes/stock-critico', undefined, true],
  ['reporteService', 'pedidosPorEstado', [T], '/api/v1/entities/reportes/pedidos-por-estado', undefined, true],
  ['reporteService', 'productosMasVendidos', [T], '/api/v1/entities/reportes/productos-mas-vendidos', undefined, true],

  ['usuarioService', 'getProfile', [T], '/api/usuarios/perfil', undefined, true],
  ['usuarioService', 'updateProfile', [{ nombre: 'n' }, T], '/api/usuarios/perfil', 'PUT', true, { nombre: 'n' }],
  ['usuarioService', 'changePassword', [{ actual: 'a', nueva: 'b' }, T], '/api/usuarios/perfil/contrasena', 'PUT', true, { actual: 'a', nueva: 'b' }],
  ['usuarioService', 'getAll', [T], '/api/admin/usuarios', undefined, true],
  ['usuarioService', 'getById', [6, T], '/api/admin/usuarios/6', undefined, true],
  ['usuarioService', 'create', [{ correo: 'c' }, T], '/api/admin/usuarios', 'POST', true, { correo: 'c' }],
  ['usuarioService', 'update', [6, { correo: 'd' }, T], '/api/admin/usuarios/6', 'PUT', true, { correo: 'd' }],
  ['usuarioService', 'toggleActive', [6, T], '/api/admin/usuarios/6/toggle-activo', 'PATCH', true],
  ['usuarioService', 'delete', [6, T], '/api/admin/usuarios/6', 'DELETE', true],
];

const servicios = {
  authService, blogService, carritoService, categoriaService, contactoService,
  cuponService, pedidoService, regionService, reporteService, usuarioService,
};

describe('servicios HTTP (fetch simulado)', () => {
  it.each(casos)('%s.%s(%j) -> %s', async (svc, metodo, args, url, http, token, body) => {
    await servicios[svc][metodo](...args);
    expect(fetchMock).toHaveBeenCalledTimes(1);
    const [calledUrl, opts] = fetchMock.mock.calls[0];
    expect(calledUrl).toBe(`${BASE}${url}`);
    expect(opts.method).toBe(http);
    expect(opts.headers['Content-Type']).toBe('application/json');
    if (token) expect(opts.headers.Authorization).toBe(`Bearer ${T}`);
    else expect(opts.headers.Authorization).toBeUndefined();
    if (body) expect(JSON.parse(opts.body)).toEqual(body);
    else expect(opts.body).toBeUndefined();
  });

  it('propaga el error del backend con mensaje y detalles', async () => {
    fetchMock.mockResolvedValue(jsonRes({ mensaje: 'Datos inválidos', detalles: ['a', 'b'] }, false, 400));
    await expect(blogService.getAll()).rejects.toThrow('Datos inválidos: a. b');
  });
});

describe('productoService', () => {
  const crudo = { id: 1, nombre: 'Tomate', categoriaNombre: 'Verduras', imagenUrl: '/t.png' };

  it('normalizeProduct completa campos alternativos y valores por defecto', () => {
    expect(normalizeProduct({ id: 1, categoria: 'Frutas', imagen: '/f.png' })).toMatchObject({
      categoria: 'Frutas', categoriaNombre: 'Frutas', imagen: '/f.png', imagenUrl: '/f.png',
    });
    expect(normalizeProduct(crudo)).toMatchObject({ categoria: 'Verduras', imagen: '/t.png' });
    expect(normalizeProduct({ id: 2 })).toMatchObject({
      categoria: 'Sin categoría', categoriaNombre: 'Sin categoría', imagen: '', imagenUrl: '',
    });
  });

  it('getAll / search / byCategory / criticalStock normalizan listas', async () => {
    fetchMock.mockResolvedValue(jsonRes([crudo]));
    expect((await productoService.getAll())[0].categoria).toBe('Verduras');
    expect((await productoService.search('to mate'))[0].imagen).toBe('/t.png');
    expect((await productoService.byCategory(4))[0].categoria).toBe('Verduras');
    expect((await productoService.criticalStock(T))[0].categoria).toBe('Verduras');
    const calls = fetchMock.mock.calls.map(([u, o]) => [u, o.headers.Authorization]);
    expect(calls).toEqual([
      [`${BASE}/api/v1/entities/productos`, undefined],
      [`${BASE}/api/v1/entities/productos/buscar?q=to%20mate`, undefined],
      [`${BASE}/api/v1/entities/productos/categoria/4`, undefined],
      [`${BASE}/api/v1/entities/productos/stock-critico`, `Bearer ${T}`],
    ]);
  });

  it('getById normaliza un producto', async () => {
    fetchMock.mockResolvedValue(jsonRes(crudo));
    expect((await productoService.getById(1)).imagen).toBe('/t.png');
    expect(fetchMock.mock.calls[0][0]).toBe(`${BASE}/api/v1/entities/productos/1`);
  });

  it('create y update envían cuerpo JSON con token', async () => {
    fetchMock.mockResolvedValue(jsonRes(crudo));
    await productoService.create({ nombre: 'Tomate' }, T);
    await productoService.update(1, { nombre: 'Tomate 2' }, T);
    const [[u1, o1], [u2, o2]] = fetchMock.mock.calls;
    expect([u1, o1.method, o1.headers.Authorization, JSON.parse(o1.body)])
      .toEqual([`${BASE}/api/v1/entities/productos`, 'POST', `Bearer ${T}`, { nombre: 'Tomate' }]);
    expect([u2, o2.method, o2.headers.Authorization, JSON.parse(o2.body)])
      .toEqual([`${BASE}/api/v1/entities/productos/1`, 'PUT', `Bearer ${T}`, { nombre: 'Tomate 2' }]);
  });

  it('delete usa DELETE con token y devuelve true', async () => {
    fetchMock.mockResolvedValue(jsonRes(''));
    expect(await productoService.delete(1, T)).toBe(true);
    const [u, o] = fetchMock.mock.calls[0];
    expect([u, o.method, o.headers.Authorization]).toEqual([`${BASE}/api/v1/entities/productos/1`, 'DELETE', `Bearer ${T}`]);
  });

  it('exporta alias y default', () => {
    expect(getProductos).toBe(productoService.getAll);
    expect(agregarProducto).toBe(productoService.create);
    expect(eliminarProducto).toBe(productoService.delete);
    expect(productoServiceDefault).toBe(productoService);
  });
});

describe('httpClient', () => {
  it('getHeaders mezcla cabeceras extra y token', () => {
    expect(getHeaders()).toEqual({ 'Content-Type': 'application/json' });
    expect(getHeaders(T, { 'X-A': '1' })).toEqual({
      'Content-Type': 'application/json', 'X-A': '1', Authorization: `Bearer ${T}`,
    });
  });

  it('parseResponse devuelve texto si no es JSON', async () => {
    const res = { ok: true, headers: { get: () => 'text/plain' }, text: async () => 'ok' };
    expect(await parseResponse(res)).toBe('ok');
  });

  it('parseResponse usa texto de error, mensaje genérico y mensaje de sesión', async () => {
    const txt = { ok: false, status: 500, headers: { get: () => null }, text: async () => 'boom' };
    await expect(parseResponse(txt)).rejects.toThrow('boom');
    await expect(parseResponse(jsonRes({}, false, 500))).rejects.toThrow('La solicitud no pudo completarse');
    await expect(parseResponse(jsonRes({}, false, 401))).rejects.toThrow('sesión expiró');
    await expect(parseResponse(jsonRes({ message: 'm' }, false, 400))).rejects.toThrow('m');
  });

  it('request admite cabeceras extra', async () => {
    await request('/x', { headers: { 'X-Y': '2' }, token: T });
    expect(fetchMock.mock.calls[0][1].headers).toMatchObject({ 'X-Y': '2', Authorization: `Bearer ${T}` });
  });
});

describe('httpClient con sesión expirada', () => {
  const escuchar = () => {
    const oyente = vi.fn();
    window.addEventListener(EVENTO_SESION_EXPIRADA, oyente);
    return { oyente, quitar: () => window.removeEventListener(EVENTO_SESION_EXPIRADA, oyente) };
  };

  it('un 401 con token emite el evento sesion-expirada y rechaza la petición', async () => {
    const { oyente, quitar } = escuchar();
    fetchMock.mockResolvedValue(jsonRes({ status: 401, mensaje: 'Debes iniciar sesión para acceder a este recurso' }, false, 401));
    await expect(request('/api/usuarios/perfil', { token: T })).rejects.toThrow('Debes iniciar sesión');
    expect(EVENTO_SESION_EXPIRADA).toBe('sesion-expirada');
    expect(oyente).toHaveBeenCalledTimes(1);
    quitar();
  });

  it('un 401 sin token (login fallido) no emite el evento', async () => {
    const { oyente, quitar } = escuchar();
    fetchMock.mockResolvedValue(jsonRes({ mensaje: 'Credenciales inválidas' }, false, 401));
    await expect(request('/api/auth/login', { method: 'POST' })).rejects.toThrow('Credenciales inválidas');
    expect(oyente).not.toHaveBeenCalled();
    quitar();
  });

  it('un 403 con token (falta de rol) no cierra la sesión', async () => {
    const { oyente, quitar } = escuchar();
    fetchMock.mockResolvedValue(jsonRes({ status: 403, mensaje: 'Sin permiso' }, false, 403));
    await expect(request('/api/admin/usuarios', { token: T })).rejects.toThrow('Sin permiso');
    expect(oyente).not.toHaveBeenCalled();
    quitar();
  });
});

describe('httpClient sin conexión con el backend', () => {
  it('convierte el fallo de red en un ConexionError con un mensaje claro', async () => {
    const { request, ConexionError, MENSAJE_SIN_CONEXION } = await import('./httpClient');
    globalThis.fetch = vi.fn().mockRejectedValue(new TypeError('Failed to fetch'));
    const error = await request('/api/v1/entities/productos').catch((e) => e);
    expect(error).toBeInstanceOf(ConexionError);
    expect(error.message).toBe(MENSAJE_SIN_CONEXION);
  });
});
