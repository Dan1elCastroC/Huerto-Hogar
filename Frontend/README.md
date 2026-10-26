# Huerto Hogar

Frontend de la tienda online Huerto Hogar construido con React, Vite y Bootstrap.

## Ejecutar el proyecto

1. Inicia MySQL y configura la base de datos según `Backend/src/main/resources/application.properties`.
2. Ejecuta el backend Spring Boot en `http://localhost:8087`.
3. En esta carpeta instala las dependencias y levanta React:

```bash
npm install
npm run dev
```

Si el backend no está disponible, el catálogo utiliza datos de respaldo para que la interfaz pueda revisarse de todas formas. Para confirmar una compra se requiere iniciar sesión con el backend activo.

## Funcionalidades

- Catálogo responsive con búsqueda y filtro por categoría.
- Carrito persistente en el navegador con incremento, disminución, eliminación y vaciado.
- Registro e inicio de sesión con JWT.
- Checkout conectado a `POST /api/v1/entities/pedidos/checkout`, con dirección, fecha opcional y cupón.
- Pantallas de compra exitosa y error de pago.
- Panel de administración protegido por rol para crear y eliminar productos.

## Calidad

```bash
npm run lint
npm run build
npm test
```

El frontend se valida con ESLint, una compilación de producción de Vite y pruebas unitarias con Vitest.

## Usuarios de demostración

El `DataInitializer` del backend crea usuarios de prueba al iniciar por primera vez:

- Administrador: `admin@duoc.cl` / `admin123`
- Cliente: `cliente@gmail.com` / `cli123`
- Vendedor: `vendedor@duoc.cl` / `vend123`
