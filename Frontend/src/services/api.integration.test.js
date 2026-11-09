// Pruebas de integración REAL front ↔ back: usan los mismos servicios que la interfaz React
// y hablan por HTTP con la API Spring Boot (sin mocks). Requieren el backend activo
// (ver Frontend/README.md → «Pruebas de integración») con los datos iniciales cargados.
import { beforeAll, describe, expect, it } from 'vitest';
import { authService } from './authService';
import { blogService } from './blogService';
import { carritoService } from './carritoService';
import { categoriaService } from './categoriaService';
import { contactoService } from './contactoService';
import { cuponService } from './cuponService';
import { pedidoService } from './pedidoService';
import { productoService } from './productoService';
import { regionService } from './regionService';
import { reporteService } from './reporteService';
import { usuarioService } from './usuarioService';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8087';
const CUENTAS = {
  admin: { correo: 'admin@duoc.cl', contrasena: 'admin123' },
  vendedor: { correo: 'vendedor@duoc.cl', contrasena: 'vend123' },
  cliente: { correo: 'cliente@gmail.com', contrasena: 'cli123' },
};
const sufijo = Date.now().toString().slice(-7);

// RUN chileno con dígito verificador válido (módulo 11) a partir del cuerpo numérico.
const runValido = (cuerpo) => {
  const suma = [...cuerpo].reverse().reduce((acc, d, i) => acc + Number(d) * ((i % 6) + 2), 0);
  const dv = 11 - (suma % 11);
  return `${cuerpo}${dv === 11 ? '0' : dv === 10 ? 'K' : dv}`;
};

const rechaza = async (promesa) => {
  try {
    await promesa;
  } catch (error) {
    return error;
  }
  throw new Error('Se esperaba que el backend rechazara la solicitud');
};

const sesiones = {};

beforeAll(async () => {
  const respuesta = await fetch(`${API_URL}/api/v1/entities/categorias`).catch(() => null);
  if (!respuesta?.ok) {
    throw new Error(`Backend no disponible en ${API_URL}. Inícialo antes de ejecutar las pruebas de integración.`);
  }
  for (const [rol, credenciales] of Object.entries(CUENTAS)) {
    sesiones[rol] = await authService.login(credenciales);
  }
});

describe('Autenticación (authService ↔ /api/auth)', () => {
  it.each([
    ['admin', 'ADMINISTRADOR'],
    ['vendedor', 'VENDEDOR'],
    ['cliente', 'CLIENTE'],
  ])('la cuenta de %s recibe un token JWT con el rol %s', (cuenta, rol) => {
    expect(sesiones[cuenta].token).toMatch(/^[\w-]+\.[\w-]+\.[\w-]+$/);
    expect(sesiones[cuenta].rol).toBe(rol);
    expect(sesiones[cuenta].correo).toBe(CUENTAS[cuenta].correo);
  });

  it('rechaza una contraseña incorrecta', async () => {
    const error = await rechaza(authService.login({ correo: CUENTAS.cliente.correo, contrasena: 'incorrecta' }));
    expect(error.message).toMatch(/credenciales/i);
  });

  it('devuelve el perfil del usuario autenticado y rechaza el acceso sin token', async () => {
    const perfil = await authService.getPerfil(sesiones.cliente.token);
    expect(perfil.correo).toBe(CUENTAS.cliente.correo);
    expect(perfil).not.toHaveProperty('contrasena');
    await rechaza(authService.getPerfil());
  });

  it('registra una cuenta nueva y permite iniciar sesión con ella', async () => {
    const correo = `it.${sufijo}@gmail.com`;
    await authService.registro({
      run: runValido(`1${sufijo}`),
      nombre: 'Integración',
      apellidos: 'Vitest',
      correo,
      contrasena: 'clave1',
      confirmarContrasena: 'clave1',
      direccion: 'Calle Falsa 123',
      region: 'Biobío',
      comuna: 'Concepción',
    });
    const sesion = await authService.login({ correo, contrasena: 'clave1' });
    expect(sesion.rol).toBe('CLIENTE');
  });
});

describe('Catálogo público (productoService, categoriaService, regionService, blogService)', () => {
  it('lista productos reales, incluido KIT-001, con su categoría', async () => {
    const productos = await productoService.getAll();
    expect(productos.length).toBeGreaterThan(0);
    const kit = productos.find((p) => p.codigo === 'KIT-001');
    expect(kit).toBeDefined();
    expect(kit.categoria).toBeTruthy();
    expect(kit.precio).toBeGreaterThan(0);
  });

  it('obtiene un producto por id y lo encuentra al buscar por nombre', async () => {
    const [primero] = await productoService.getAll();
    const detalle = await productoService.getById(primero.id);
    expect(detalle.codigo).toBe(primero.codigo);
    const resultados = await productoService.search(primero.nombre.split(' ')[0]);
    expect(resultados.some((p) => p.id === primero.id)).toBe(true);
  });

  it('filtra productos por categoría', async () => {
    const categorias = await categoriaService.getAll();
    expect(categorias.length).toBeGreaterThan(0);
    const productos = await productoService.byCategory(categorias[0].id);
    expect(Array.isArray(productos)).toBe(true);
  });

  it('informa un error cuando el producto no existe', async () => {
    await rechaza(productoService.getById(999999));
  });

  it('entrega regiones con sus comunas y noticias', async () => {
    const regiones = await regionService.getAll();
    expect(regiones.length).toBeGreaterThan(0);
    const comunas = await regionService.getCommunes(regiones[0].nombre ?? regiones[0]);
    expect(comunas.length).toBeGreaterThan(0);
    expect(Array.isArray(await blogService.getAll())).toBe(true);
  });

  it('valida el cupón BIENVENIDA10 y rechaza uno inexistente', async () => {
    const cupon = await cuponService.validate('BIENVENIDA10');
    expect(cupon.codigo).toBe('BIENVENIDA10');
    await rechaza(cuponService.validate('NO-EXISTE-XYZ'));
  });

  it('acepta un mensaje del formulario de contacto sin sesión', async () => {
    const respuesta = await contactoService.create({
      nombre: 'Visitante',
      correo: 'visita@gmail.com',
      comentario: 'Mensaje automatizado de Vitest.',
    });
    expect(respuesta).toBeTruthy();
  });
});

describe('Permisos por rol (el backend aplica JWT + @PreAuthorize)', () => {
  it('el cliente no puede listar usuarios ni crear productos', async () => {
    await rechaza(usuarioService.getAll(sesiones.cliente.token));
    await rechaza(productoService.create({ codigo: 'X-1', nombre: 'No', precio: 1, stock: 1 }, sesiones.cliente.token));
  });

  it('el administrador lista usuarios y el vendedor consulta reportes y stock crítico', async () => {
    const usuarios = await usuarioService.getAll(sesiones.admin.token);
    expect(usuarios.map((u) => u.correo)).toEqual(expect.arrayContaining(Object.values(CUENTAS).map((c) => c.correo)));
    expect(await reporteService.resumen(sesiones.vendedor.token)).toBeTruthy();
    expect(Array.isArray(await productoService.criticalStock(sesiones.vendedor.token))).toBe(true);
    expect(Array.isArray(await contactoService.getAll(sesiones.admin.token))).toBe(true);
  });
});

describe('Administración de productos (CRUD completo con rol ADMINISTRADOR)', () => {
  it('crea, actualiza y elimina un producto', async () => {
    const [categoria] = await categoriaService.getAll();
    const token = sesiones.admin.token;
    const base = {
      codigo: `IT-${sufijo}`,
      nombre: 'Producto de integración',
      descripcion: 'Creado por Vitest',
      precio: 1990,
      stock: 10,
      stockCritico: 2,
      imagenUrl: '/img/productos/tomates.jpg',
      categoriaId: categoria.id,
    };
    const creado = await productoService.create(base, token);
    expect(creado.id).toBeDefined();

    const actualizado = await productoService.update(creado.id, { ...base, nombre: 'Producto editado', precio: 2490, stock: 8 }, token);
    expect(actualizado.precio).toBe(2490);
    expect((await productoService.getById(creado.id)).nombre).toBe('Producto editado');

    expect(await productoService.delete(creado.id, token)).toBe(true);
    // El borrado es lógico: el producto deja de aparecer en el catálogo público.
    expect((await productoService.getAll()).some((p) => p.id === creado.id)).toBe(false);
  });
});

describe('Flujo de compra del cliente: carrito → cupón → checkout → historial', () => {
  it('compra un producto, descuenta stock y registra el pedido', async () => {
    const { token } = sesiones.cliente;
    const productos = await productoService.getAll();
    const producto = productos.find((p) => p.stock >= 3);
    expect(producto, 'se necesita al menos un producto con stock').toBeDefined();

    await carritoService.clear(token);
    await carritoService.add(producto.id, 2, token);
    await carritoService.update(producto.id, 1, token);
    const carrito = await carritoService.get(token, 'BIENVENIDA10');
    expect(JSON.stringify(carrito)).toContain(producto.nombre);

    const pedido = await pedidoService.crearPedido({
      direccionEntrega: 'Av. Siempre Viva 742, Concepción',
      codigoCupon: 'BIENVENIDA10',
    }, token);
    expect(pedido.id).toBeDefined();

    const historial = await pedidoService.historial(token);
    expect(historial.some((p) => p.id === pedido.id)).toBe(true);
    expect((await pedidoService.getById(pedido.id, token)).id).toBe(pedido.id);
    expect((await productoService.getById(producto.id)).stock).toBe(producto.stock - 1);

    const todos = await pedidoService.getAll(sesiones.admin.token);
    expect(todos.some((p) => p.id === pedido.id)).toBe(true);
  });

  it('no permite confirmar un pedido con el carrito vacío', async () => {
    await carritoService.clear(sesiones.cliente.token);
    await rechaza(pedidoService.crearPedido({ direccionEntrega: 'Calle 1' }, sesiones.cliente.token));
  });
});
