# Huerto Hogar · Frontend

Interfaz de la tienda con React, Vite y Bootstrap. Consulta la [guía general](../README.md) para iniciar la base de datos y el backend.

## Ejecutar

Requiere Node.js `^22.22.2 || ^24.15.0 || >=26.0.0`, compatible con las versiones de Vitest y jsdom del [lockfile](./package-lock.json), y npm.
Desde esta carpeta (`Frontend`):

```powershell
npm ci
npm run dev
```

Abre la URL que indique Vite en la terminal. La API se consulta en `http://localhost:8087` por defecto.
Solo si cambias la dirección del backend, crea `Frontend/.env.local` con su origen, sin agregar `/api`:

```dotenv
VITE_API_URL=http://localhost:8087
```

Reinicia Vite tras cambiar esa variable. Si la API está apagada, el catálogo muestra un error de conexión; no se generan productos ficticios.

## Funcionalidades actuales

- Tienda: Home, Nosotros, categorías, catálogo con búsqueda/filtro, detalle de producto, noticias y contacto.
- Cuenta: registro, inicio de sesión JWT, perfil, cambio de contraseña e historial de pedidos con detalle/estado.
- Carrito: cantidades limitadas por stock, eliminación, vaciado y totales. La vista se guarda en `localStorage`; con sesión, los cambios se envían al backend. Al iniciar sesión con productos locales, estos se sincronizan con el carrito del servidor. No se recupera automáticamente un carrito remoto desde otro navegador.
- Checkout: datos reales de la cuenta, dirección editable, próxima disponibilidad o entrega programada desde hoy, cupón opcional y confirmación del pedido.
- Cupones: aplicación y retirada, descuento recalculado y rechazo de códigos inválidos/vencidos; el backend valida el total definitivo. Ofertas consulta la vigencia de `BIENVENIDA10`.
- Administración: dashboard, productos, categorías, cupones, noticias, contactos, usuarios/roles, pedidos, reportes y perfil. El panel se muestra al rol administrador.

Confirmar la compra requiere sesión y backend activo. Se registra el pedido; no existe una pasarela bancaria ni se cobra una tarjeta.

## Calidad y pruebas

Ejecuta desde `Frontend`:

```powershell
npm run lint
npm run build
npm test
```

`npm test` ejecuta 60 casos de interfaz, lógica y servicios con Vitest y Testing Library, usando mocks donde corresponde; pasan sin backend. Este comando excluye explícitamente `src/services/api.integration.test.js`. Ejecutar Vitest directamente, desde una extensión o con `npm run test:watch` puede incluir esas dos pruebas HTTP reales y fallar sin backend. Se mantiene Vitest, sin Jasmine ni Karma. La compilación genera `dist/`.

Para comprobar HTTP real, inicia el backend y su base de datos con los datos iniciales y ejecuta:

```powershell
npm run test:integration
```

Son dos pruebas: productos y categorías. Con la API apagada ambas fallan por conexión rechazada (`ECONNREFUSED`); la prueba de productos espera el código inicial `KIT-001`.

Demostración opcional del fallo de conexión, sin apagar ni modificar el backend:

```powershell
.\demo-fallo-integracion.ps1
```

El [script](./demo-fallo-integracion.ps1) usa temporalmente un puerto libre sin API, provoca dos fallos y restaura `VITE_API_URL` al terminar. Su código de salida 1 es intencional.

Las pruebas están agrupadas en:

- [App.test.jsx](./src/App.test.jsx): navegación, categorías, ofertas, errores y cierre de sesión.
- `src/components/*.test.jsx` y subcarpetas `profile`, `orders`, `blog`, `contact`: cuenta, catálogo, carrito/checkout y contenido público.
- `src/components/admin/*.test.jsx` y [AdminPage.test.jsx](./src/pages/AdminPage.test.jsx): gestión y dashboard.
- `src/services/api.test.js` y `cartUtils.test.js`: servicios aislados y reglas del carrito.
- [api.integration.test.js](./src/services/api.integration.test.js): las dos consultas HTTP reales.

La relación por historia y criterio, con archivos y líneas de evidencia, está en [Criterios y tests](../Criterios-y-tests.md); los comportamientos comprobados se resumen en [Cobertura funcional](../Cobertura-Pruebas.md). Las pruebas automatizadas no acreditan por sí solas todos los criterios ni la revisión responsive de cada pantalla.

No se ha medido un porcentaje de líneas o ramas del frontend. [package.json](./package.json) no incluye `test:coverage`, el lockfile no incorpora un proveedor de cobertura y [vite.config.js](./vite.config.js) no configura cobertura. Las 60 pruebas y los criterios de aceptación marcados en Excel son métricas distintas de la cobertura del código. El reporte JaCoCo del backend se genera por separado; su alcance se explica en la guía de cobertura.

## Cuentas de demostración

El inicializador crea estas cuentas cuando no existen:

- Administrador: `admin@duoc.cl` / `admin123`.
- Cliente: `cliente@gmail.com` / `cli123`.
- Vendedor: `vendedor@duoc.cl` / `vend123`; no dispone del panel de administrador.

## Qué mostrar y dónde está

La [guía completa de presentación](../README.md#guía-de-presentación-de-15-minutos) organiza el recorrido y el arranque con `..\iniciar-demo.ps1`.

- **Navegación y sesión:** `src/App.jsx` y `src/components/Navbar.jsx`; `App.test.jsx` comprueba navegación, nombre y logout persistente.
- **Inicio, categorías y detalle:** `src/pages/HomePage.jsx`, `CategoriesPage.jsx`, `ProductDetailPage.jsx`; catálogo/búsqueda en `src/components/ProductList.jsx`, probado en `ProductList.test.jsx` y `App.test.jsx`.
- **Registro e ingreso:** `src/components/Registro.jsx`, `Login.jsx` y sus `.test.jsx`; perfil/contraseña en `src/components/profile` e historial en `src/components/orders`.
- **Compra y cupones:** `src/components/Carrito.jsx` y `Carrito.test.jsx`; reglas locales en `src/services/cartUtils.js` y sus tests, llamadas reales en `pedidoService.js`, `cuponService.js` y `carritoService.js`.
- **Ofertas, noticias y contacto:** `src/pages/OffersPage.jsx`, `BlogDetailPage.jsx`, `src/components/blog` y `src/components/contact`; tests en App y esos subdirectorios.
- **Administración:** `src/pages/AdminPage.jsx`, `src/components/MainPage.jsx` y `src/components/admin`; tests de dashboard, productos, categorías, usuarios y boleta en sus archivos `.test.jsx`.
- **Adaptación visual:** `src/index.css` y clases Bootstrap. Cambiar ancho de pantalla durante el recorrido; los tests jsdom no comprueban la distribución CSS real.
- **Comunicación:** `src/services/httpClient.js`; `api.test.js` simula fetch, `api.integration.test.js` consulta HTTP real. Mostrar esta diferencia al explicar resultados con el backend apagado.

Recorrer primero tienda y compra como cliente, después panel como administrador. Los tests y límites de cada criterio están en [Criterios-y-tests.md](../Criterios-y-tests.md), sin atribuir cobertura completa a todas las pantallas.
