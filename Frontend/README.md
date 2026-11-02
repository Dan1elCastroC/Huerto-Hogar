# Huerto Hogar · Frontend

Interfaz de usuario de la tienda **Huerto Hogar** construida con React, Vite y Bootstrap.

---

## Requisitos

- **Node.js** v18 o superior (se recomienda la LTS actual)
- **npm** incluido con Node.js
- El backend corriendo en `http://localhost:8087` (Spring Boot)

---

## Instalación y ejecución

Desde la carpeta `Frontend`:

```bash
npm install
npm run dev
```

Abre la URL que indique Vite en la terminal (por defecto `http://localhost:5173`).

### Cambiar la URL del backend

Si el backend corre en otro puerto, crea el archivo `Frontend/.env.local`:

```dotenv
VITE_API_URL=http://localhost:8087
```

Reinicia Vite después de modificar esa variable. Si el backend está apagado, el catálogo muestra un aviso de error de conexión; no se generan datos ficticios.

---

## Funcionalidades

### Tienda (pública)
- **Home** — landing con productos destacados y accesos rápidos.
- **Catálogo** — listado con búsqueda y filtro por categoría.
- **Detalle de producto** — imagen, descripción, precio, stock y botón agregar al carrito.
- **Categorías** — vista separada por categoría con acceso directo al catálogo filtrado.
- **Ofertas** — productos con cupón `BIENVENIDA10` vigente.
- **Nosotros** — página informativa del equipo.
- **Noticias / Blog** — listado de entradas y vista de detalle.
- **Contacto** — formulario de contacto.

### Cuenta
- Registro e inicio de sesión con JWT.
- Perfil editable y cambio de contraseña.
- Historial de pedidos con detalle de estado.

### Carrito y compra
- Cantidades limitadas por stock disponible.
- Eliminación de ítems y vaciado completo.
- El carrito se guarda en `localStorage`; con sesión activa los cambios se sincronizan con el backend.
- **Checkout** — formulario con datos del cliente, dirección de entrega y cupón de descuento opcional.
- Vista de **compra exitosa** con resumen del pedido.
- Vista de **pago con error** con opción de reintentar.

### Panel de administración
Disponible solo para el rol `ADMINISTRADOR`. Incluye sidebar colapsable con:

| Sección | Funciones |
|---|---|
| **Dashboard** | Métricas en tiempo real (compras, productos, usuarios) y accesos rápidos a cada módulo |
| **Órdenes** | Listado de pedidos, cambio de estado y vista de boleta por pedido |
| **Productos** | Crear, editar, listar y filtrar por stock crítico |
| **Categorías** | Crear y editar categorías |
| **Usuarios** | Crear, editar, activar/desactivar y ver historial de compras por usuario |
| **Reportes** | Stock crítico y productos más vendidos |
| **Noticias** | Gestión de entradas del blog |
| **Cupones** | Gestión de cupones de descuento |
| **Contactos** | Mensajes recibidos desde el formulario de contacto |
| **Perfil** | Datos personales del administrador |

---

## Cuentas de demostración

El backend crea estas cuentas al iniciar si no existen:

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | `admin@duoc.cl` | `admin123` |
| Cliente | `cliente@gmail.com` | `cli123` |
| Vendedor | `vendedor@duoc.cl` | `vend123` |

> El rol Vendedor no tiene acceso al panel de administración.

---

## Scripts disponibles

```bash
npm run dev       # Inicia el servidor de desarrollo
npm run build     # Compila el proyecto en dist/
npm run lint      # Revisa el código con ESLint
npm test          # Ejecuta los 56 tests unitarios (sin backend)
npm run test:integration  # Ejecuta 2 tests HTTP reales (requiere backend activo)
```

---

## Tests

El proyecto cuenta con **56 tests** ejecutados con Vitest y Testing Library. Todos pasan sin necesidad de backend activo gracias a mocks.

### Distribución por archivo

| Archivo | Tests | Qué cubre |
|---|---|---|
| `Carrito.test.jsx` | 12 | Checkout, cupones, cantidades, errores de pago |
| `App.test.jsx` | 7 | Navegación, categorías, ofertas, logout |
| `cartUtils.test.js` | 5 | Lógica local del carrito (agregar, actualizar, eliminar) |
| `AdminPage.test.jsx` | 5 | Dashboard, indicadores, sidebar, navegación admin |
| `api.test.js` | 4 | Servicios HTTP con fetch simulado |
| `ProductList.test.jsx` | 4 | Renderizado de catálogo, búsqueda y filtros |
| `UserManager.test.jsx` | 3 | Crear, editar y historial de compras de usuario |
| `Registro.test.jsx` | 2 | Formulario de registro y validación |
| `PasswordForm.test.jsx` | 2 | Cambio de contraseña |
| `OrderList.test.jsx` | 2 | Historial de pedidos del cliente |
| `MainPage.test.jsx` | 2 | Gestión de productos en admin |
| `Login.test.jsx` | 2 | Inicio de sesión y manejo de errores |
| `BlogList.test.jsx` | 2 | Listado de noticias |
| `ProfileForm.test.jsx` | 1 | Edición de perfil |
| `OrderManager.test.jsx` | 1 | Boleta en el panel admin |
| `ContactoForm.test.jsx` | 1 | Formulario de contacto |
| `CategoryManager.test.jsx` | 1 | Gestión de categorías en admin |

### Tests de integración (requieren backend)

```bash
npm run test:integration
```

Ejecuta 2 pruebas HTTP reales contra el backend: listado de productos y categorías.
Con el backend apagado ambas fallan con `ECONNREFUSED`.

---

## Estructura del proyecto

```
Frontend/
├── src/
│   ├── App.jsx                  # Raíz: estado global, rutas y lógica de sesión
│   ├── components/
│   │   ├── Navbar.jsx           # Barra de navegación
│   │   ├── Carrito.jsx          # Carrito + checkout + resultado de pago
│   │   ├── Login.jsx / Registro.jsx
│   │   ├── ProductList.jsx      # Catálogo con búsqueda y filtros
│   │   ├── admin/               # Managers del panel de administración
│   │   ├── blog/                # Lista y tarjeta de noticias
│   │   ├── contact/             # Formulario de contacto
│   │   ├── orders/              # Historial de pedidos
│   │   └── profile/             # Perfil y cambio de contraseña
│   ├── pages/                   # Una página por vista principal
│   ├── services/                # Comunicación con el backend (fetch + helpers)
│   └── index.css                # Estilos globales y panel admin
└── vite.config.js
```

---

## Notas importantes

- Confirmar una compra requiere **sesión activa y backend disponible**. No existe pasarela de pago real.
- Las pruebas automatizadas no validan el diseño responsive visualmente; eso se verifica redimensionando el navegador.
- No se ha configurado un reporte de cobertura de líneas. Los 56 tests y los criterios de aceptación son la métrica de calidad del frontend.