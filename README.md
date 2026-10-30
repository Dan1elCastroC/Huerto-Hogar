# Huerto Hogar

Tienda web del proyecto FullStack2. El frontend React consume una API Spring Boot y los datos del negocio se guardan en MySQL/MariaDB de XAMPP.

## Requisitos

- Java **17** y Maven disponible en `PATH`. El repositorio no incluye Maven Wrapper (`mvnw`).
- Node.js `^22.22.2 || ^24.15.0 || >=26.0.0`, compatible con las versiones de Vitest y jsdom del lockfile, y npm.
- MySQL/MariaDB iniciado, por defecto en `localhost:3306`.

Se mantienen Spring Boot 2.7.18, React 19, Vite 8, Bootstrap 5 y Vitest. Las versiones concretas están en [Backend/pom.xml](Backend/pom.xml) y [Frontend/package-lock.json](Frontend/package-lock.json).

## Ejecutar en desarrollo

Para preparar la presentación desde la raíz, con las dependencias ya instaladas:

```powershell
.\iniciar-demo.ps1
```

El [script](iniciar-demo.ps1) inicia Apache y MySQL de XAMPP, espera al backend y al frontend y ejecuta `mvn test`, `npm test` y `npm run test:integration`. No instala dependencias ni ejecuta lint, empaquetado o cobertura. Reutiliza servicios existentes compatibles y deja los procesos activos. Código 0 significa que las tres ejecuciones aprobaron; código 1 indica error y hay que revisar los logs.

Requiere XAMPP en `C:\xampp`, JDK 17 y dependencias del frontend (`npm ci` una vez). Detecta Maven en PATH o en la caché local de Maven 3.9.12 y npm en PATH. Se pueden indicar otras instalaciones mediante `-XamppPath`, `-JavaHome`, `-MavenPath` y `-NpmPath`. No cambia las versiones instaladas. Los logs quedan en `%TEMP%\HuertoHogar-demo\<ejecución>`; la consola indica la ruta y `servicios.json` registra los PID. Si PowerShell bloquea el archivo, puede ejecutarse una sola vez con `powershell -NoProfile -ExecutionPolicy Bypass -File .\iniciar-demo.ps1`.

Para iniciar cada parte manualmente:

Ejecutar los comandos desde la raíz del repositorio y mantener abiertas las dos terminales.

1. Iniciar **MySQL** en XAMPP. El backend usa la base `huertodb`, el usuario `root` y contraseña vacía por defecto. Si la instalación tiene otras credenciales, configurar `DB_USER` y `DB_PASSWORD` en la terminal del backend.
2. Iniciar la API en la primera terminal:

   ```powershell
   cd Backend
   mvn spring-boot:run
   ```

3. Desde la raíz, iniciar la interfaz en una segunda terminal:

   ```powershell
   cd Frontend
   npm ci
   npm run dev -- --port 5173 --strictPort
   ```

Abrir [la tienda](http://localhost:5173). La API atiende en `http://localhost:8087`; su documentación está en [Swagger UI](http://localhost:8087/swagger-ui.html).

La conexión permite crear `huertodb` si el usuario de MySQL tiene permisos. Hibernate actualiza las tablas y los inicializadores agregan datos de demostración que faltan. Los pedidos, mensajes de contacto y carritos se generan al usar sus funcionalidades.

Si el backend está apagado, el catálogo informa el error de conexión. No se sustituyen los productos por datos ficticios. Registro, inicio de sesión, validación de cupones y confirmación de pedidos necesitan la API activa.

Para cambiar el destino de la API, configurar `VITE_API_URL` en `Frontend/.env.local` y reiniciar Vite. La configuración de la base de datos, el puerto y los orígenes CORS se explica en el [README del backend](Backend/README.md).

## Funcionalidades

- Tienda pública: inicio, nosotros, catálogo, búsqueda, filtros por categoría, detalles de productos, noticias, ofertas y contacto.
- Cuenta: registro, inicio de sesión, nombre de la cuenta, perfil, cambio de contraseña, cierre de sesión e historial de pedidos.
- Carrito: cantidades, eliminación, persistencia local y sincronización de operaciones con la API para usuarios autenticados.
- Checkout: dirección, entrega próxima o programada, validación de cupones, resumen y registro del pedido con descuento de stock. No se integra una pasarela bancaria.
- Administración: dashboard, productos, stock crítico, categorías, usuarios, pedidos y boletas, cupones, noticias, contactos y reportes.
- Interfaz con media queries para móvil, tablet y escritorio. El panel administrativo de la interfaz se reserva al rol `ADMINISTRADOR`.

## Cuentas de demostración

El inicializador crea estas cuentas cuando todavía no existen:

- Administrador: `admin@duoc.cl` / `admin123`.
- Vendedor: `vendedor@duoc.cl` / `vend123`.
- Cliente: `cliente@gmail.com` / `cli123`.

Si una contraseña fue modificada, reiniciar el backend no la restablece. Estas cuentas son para desarrollo y evaluación.

## Pruebas y compilación

Desde `Backend`, usando Java 17:

```powershell
mvn test
mvn clean verify
```

Las pruebas de contexto/persistencia usan el perfil de pruebas con H2. `verify` también genera el reporte JaCoCo en `Backend/target/site/jacoco/index.html`.

Desde `Frontend`:

```powershell
npm test
npm run test:integration
npm run lint
npm run build
```

- `npm test`: 60 pruebas unitarias y de componentes con Vitest y servicios simulados. Pasan sin backend.
- `npm run test:integration`: dos consultas HTTP reales de productos y categorías. Requieren backend y base de datos activos, con los datos iniciales (`KIT-001` incluido). Con la API apagada ambas fallan por conexión rechazada (`ECONNREFUSED`).
- `npm run lint` revisa el código y `npm run build` genera `Frontend/dist`.

Para mostrar un fallo de integración intencionado, ejecutar desde `Frontend` en PowerShell:

```powershell
.\demo-fallo-integracion.ps1
```

La demostración debe mostrar **2 pruebas fallidas** por conexión rechazada y devolver código de salida 1. Usa temporalmente un puerto local sin backend y restaura `VITE_API_URL`. Las pruebas negativas habituales, como rechazar un cupón vencido, deben pasar cuando el rechazo funciona correctamente.

Los tests están en [Backend/src/test/java](Backend/src/test/java), [Frontend/src/App.test.jsx](Frontend/src/App.test.jsx), [Frontend/src/components](Frontend/src/components), [Frontend/src/pages/AdminPage.test.jsx](Frontend/src/pages/AdminPage.test.jsx) y [Frontend/src/services](Frontend/src/services). La evidencia por criterio, con archivos y líneas, está en [Criterios y tests](Criterios-y-tests.md).

La [cobertura funcional](Cobertura-Pruebas.md) relaciona comportamientos y casos probados. El número de pruebas y la fracción de criterios de aceptación marcados en Excel no equivalen a cobertura del código. En frontend no se ha medido un porcentaje de líneas/ramas: no hay script `test:coverage`, proveedor instalado ni configuración de cobertura. Para backend, `mvn clean verify` genera [el reporte JaCoCo](Backend/target/site/jacoco/index.html); consulta su alcance y resultados en la guía de cobertura.

## Documentación del proyecto

- [Backend: configuración, API y pruebas](Backend/README.md).
- [Frontend: instalación, vistas y pruebas](Frontend/README.md).
- [ERS V2 principal en Word](ERS-V2-Huerto-Hogar.docx), elaborado sobre la plantilla adjunta; [resumen en Markdown](ERS-V2-Huerto-Hogar.md).
- [Cobertura funcional de pruebas](Cobertura-Pruebas.md).
- [Criterios de aceptación y ubicación exacta de los tests](Criterios-y-tests.md).
- [Planilla de requerimientos](Plantilla_Gestion_Requerimientos_Scrum_HuertoHogar.xlsx): hoja «Historias de Usuario», columna I para criterios y columna M para su evidencia. Rojo identifica cada criterio cumplido y probado, no una historia completa.

## Guía de presentación de 15 minutos

### Preparación antes de presentar

1. Ejecutar `iniciar-demo.ps1` y comprobar las tres ejecuciones: backend, frontend aislado e integración real. Abrir `http://localhost:5173` y Swagger en `http://localhost:8087/swagger-ui.html`.
2. Confirmar en Administración → Cupones que `BIENVENIDA10` esté activo y vigente. Preparar otro cupón con fecha anterior a hoy para mostrar el rechazo, reutilizándolo si ya existe. Conservar activo `KIT-001`: es un dato que espera la prueba de integración.
3. Tener abiertos el ERS Word, `Cobertura-Pruebas.md`, `Criterios-y-tests.md` y la planilla. El documento de criterios enlaza cada caso y línea de código.

### Minutos 0 a 3 Tienda y arquitectura

4. Explicar el flujo navegador → React → API HTTP → MySQL. Mostrar inicio, Nosotros, categorías, búsqueda/filtro, detalle y regreso al listado; abrir una noticia y volver. Código: `Frontend/src/App.jsx`, `pages`, `components/ProductList.jsx`; HTTP en `src/services/httpClient.js`. La base no se sustituye por productos ficticios si la API falla.
5. Mostrar que la tienda pública usa barra superior y que las vistas cambian con estado de React. Las rutas de código y tests de cada pantalla están detalladas en el [README del frontend](Frontend/README.md).

### Minutos 3 a 7 Cliente y compra

6. Entrar con `cliente@gmail.com` / `cli123`: señalar el nombre de la cuenta. Agregar un producto, cambiar cantidad y mostrar el límite de stock y los totales.
7. Proceder al checkout: mostrar nombre/correo/dirección autocompletados, dirección editable y entrega próxima o programada. Aplicar primero el cupón vencido para observar el error; quitarlo, aplicar `BIENVENIDA10` y comprobar descuento/total. Confirmar una compra y anotar su número.
8. Mostrar el pedido en el historial, su detalle y el perfil. Cerrar sesión y recargar: los controles de cuenta deben desaparecer. Código: `components/Carrito.jsx`, `components/profile`, `components/orders`; reglas definitivas en los servicios Java de `carrito`, `cupon` y `pedido`. La compra registra un pedido; no es un cobro bancario.

### Minutos 7 a 12 Administración

9. Entrar con `admin@duoc.cl` / `admin123`. Mostrar indicadores reales y menú lateral. En Productos, crear un producto de demostración con código único, editarlo, recargar y comprobar persistencia. Usar una imagen existente como `/img/productos/tomates.jpg` y mostrar el filtro de stock crítico.
10. En Categorías, crear/editar una categoría y comprobar que cambia en el formulario de productos. En Usuarios, editar un usuario dejando vacía la contraseña y abrir su historial; no se deben mezclar compras de otros usuarios.
11. En Pedidos, abrir la boleta de la compra reciente y señalar productos, cupón, descuento y total. Mostrar los estados disponibles. Recorrer Reportes, Cupones, Noticias, Contactos y Mi perfil; regresar a Tienda y cerrar sesión.
12. Ubicación: `pages/AdminPage.jsx`, `components/MainPage.jsx`, `components/admin/*Manager.jsx`. El [README del backend](Backend/README.md) explica controladores, servicios y permisos. Aclarar que las vistas de reportes y otros formularios tienen pruebas pendientes; no presentar todos los módulos como completamente probados.

### Minutos 12 a 15 Pruebas ERS y cobertura

13. Mostrar los logs de las tres ejecuciones. Explicar: los tests aislados usan mocks/H2 y no necesitan un backend HTTP; las dos consultas de integración sí lo necesitan. Como demostración separada ejecutar `Frontend/demo-fallo-integracion.ps1`: se esperan dos fallos por conexión y restauración de la URL.
14. Abrir los tests de catálogo, carrito/cupones y usuarios; mostrar el archivo/línea en `Criterios-y-tests.md`. Abrir la planilla: rojo significa criterio cumplido y probado, no todos los criterios de una historia.
15. Mostrar el ERS Word y `Cobertura-Pruebas.md`. La cobertura funcional no es porcentaje de código. JaCoCo requiere `mvn clean verify`; el frontend todavía no tiene un proveedor de cobertura de líneas/ramas. Cerrar con los límites y entregables pendientes, y reservar otros 5 minutos para preguntas.

Los servicios quedan activos al terminar el script. Para cerrar la demo, usar el panel XAMPP para Apache/MySQL y los PID de `servicios.json` para identificar backend/frontend en el Administrador de tareas. Si Windows no informa el PID, se registra vacío; comprobar el proceso por su ruta antes de cerrarlo.
