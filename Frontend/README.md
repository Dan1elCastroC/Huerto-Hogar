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

`npm test` ejecuta 60 casos de interfaz, lógica y servicios con Vitest y Testing Library, usando mocks donde corresponde; pasan sin backend. Se mantiene Vitest, sin Jasmine ni Karma. La compilación genera `dist/`.

Para comprobar HTTP real, inicia el backend y su base de datos con los datos iniciales y ejecuta:

```powershell
npm run test:integration
```

Son dos pruebas: productos y categorías. Fallan si la API no está disponible; la prueba de productos espera el código inicial `KIT-001`.

Demostración opcional del fallo de conexión, sin apagar ni modificar el backend:

```powershell
.\demo-fallo-integracion.ps1
```

El [script](./demo-fallo-integracion.ps1) usa temporalmente un puerto libre sin API, provoca dos fallos y restaura `VITE_API_URL` al terminar. Su código de salida distinto de cero es intencional.

Las pruebas están agrupadas en:

- [App.test.jsx](./src/App.test.jsx): navegación, categorías, ofertas, errores y cierre de sesión.
- `src/components/*.test.jsx` y subcarpetas `profile`, `orders`, `blog`, `contact`: cuenta, catálogo, carrito/checkout y contenido público.
- `src/components/admin/*.test.jsx` y [AdminPage.test.jsx](./src/pages/AdminPage.test.jsx): gestión y dashboard.
- `src/services/api.test.js` y `cartUtils.test.js`: servicios aislados y reglas del carrito.
- [api.integration.test.js](./src/services/api.integration.test.js): las dos consultas HTTP reales.

La relación por historia y criterio, con archivos y líneas de evidencia, está en [Criterios y tests](../Criterios-y-tests.md). Las pruebas automatizadas no acreditan por sí solas todos los criterios ni la revisión responsive de cada pantalla.

## Cuentas de demostración

El inicializador crea estas cuentas cuando no existen:

- Administrador: `admin@duoc.cl` / `admin123`.
- Cliente: `cliente@gmail.com` / `cli123`.
- Vendedor: `vendedor@duoc.cl` / `vend123`; no dispone del panel de administrador.
