# Backend de Huerto Hogar

API REST con Java 17, Spring Boot 2.7.18, Spring Security/JWT y JPA.
Consulta la [guía general](../README.md) y la [revisión de criterios y pruebas](../Criterios-y-tests.md).

## Requisitos

- JDK 17 y Maven disponible en `PATH`, según [pom.xml](pom.xml).
- MySQL de XAMPP activo en `localhost:3306` para ejecutar la aplicación.
- Este directorio no incluye `mvnw.cmd` ni Maven Wrapper: los comandos usan `mvn`.

## Configuración y arranque en PowerShell

Abre una terminal en `Backend`. Ajusta la ruta del JDK si tu instalación está en otro lugar:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
mvn -version
mvn spring-boot:run
```

La selección de Java solo afecta esta terminal. Detén el servidor con `Ctrl+C`.
La API escucha en `http://localhost:8087`; `SERVER_PORT` permite cambiar ese puerto.

La conexión está en [application.properties](src/main/resources/application.properties):

- Base `huertodb`, usuario mediante `DB_USER` (por defecto `root`) y contraseña mediante `DB_PASSWORD` (por defecto vacía para XAMPP local).
- `createDatabaseIfNotExist=true` permite crear la base si el usuario tiene permiso; si no lo tiene, crea `huertodb` previamente con una cuenta autorizada.
- Hibernate usa `ddl-auto=update` para crear/actualizar las tablas; arrancar no elimina los datos existentes.
- Configura la clave JWT localmente mediante `JWT_SECRET` y no compartas su valor. La expiración configurada es de 24 horas.
- Los orígenes CORS locales permitidos figuran en ese mismo archivo; el frontend habitual usa `http://localhost:5173`.

## Datos iniciales

Hay dos inicializadores activos: [DataInitializer](src/main/java/com/huerto/hogar/config/DataInitializer.java)
y [DatosIniciales](src/main/java/com/huerto/hogar/config/DatosIniciales.java).
Ambos comprueban registros existentes antes de insertarlos; comparten cuatro códigos de producto.
No sobrescriben los productos que ya existen ni duplican los registros al reiniciar.

En una base vacía se cargan tres usuarios de demostración, ocho productos y ocho categorías.
También se crea una noticia si no hay ninguna y el cupón `BIENVENIDA10` (10 %) si no existe.
Las cuentas demo son `admin@duoc.cl` (ADMINISTRADOR), `vendedor@duoc.cl` (VENDEDOR)
y `cliente@gmail.com` (CLIENTE); sus credenciales iniciales están definidas en `DataInitializer`.
Pedidos, detalles, carritos y contactos se generan al utilizar sus funcionalidades.

## API y permisos

- `POST /api/auth/registro` y `POST /api/auth/login`: registro y acceso públicos.
- `GET /api/v1/entities/productos`, `GET /api/v1/entities/categorias`, `GET /api/v1/entities/blogs` y `GET /api/regiones`: consulta pública.
- `POST /api/v1/entities/contacto` y `GET /api/v1/entities/cupones/validar/{codigo}`: públicos.
- Perfil (`/api/usuarios/perfil`), carrito y pedidos propios: requieren sesión.
- `/api/admin/usuarios` y gestión de productos, categorías, blogs y cupones: administración.
- Reportes y consulta general de pedidos: ADMINISTRADOR o VENDEDOR; cambiar estados de pedidos requiere ADMINISTRADOR.

Para rutas protegidas envía `Authorization: Bearer <token>` con el token obtenido al iniciar sesión.
Los contratos y campos completos se consultan con el servidor activo en
[Swagger UI](http://localhost:8087/swagger-ui.html) y [OpenAPI JSON](http://localhost:8087/api-docs).

## Pruebas, compilación y cobertura

En la misma terminal con Java 17:

```powershell
mvn test
mvn clean package
mvn clean verify
```

`test` ejecuta las pruebas; `package` también genera `target/hogar-0.0.1-SNAPSHOT.jar`.
Hay 65 métodos `@Test` en 13 clases de [pruebas Java](src/test/java/com/huerto/hogar).
Usan mocks y H2 mediante el [perfil test](src/test/resources/application-test.properties); no necesitan iniciar la API ni MySQL/XAMPP.
El número de casos no es un porcentaje de cobertura.

JaCoCo **0.8.15** está configurado en `pom.xml`; `mvn clean verify` genera el informe.
Al revisar esta documentación no existe `target/site/jacoco`: la medición está pendiente de generar.
HTML esperado: `target/site/jacoco/index.html`; XML: `target/site/jacoco/jacoco.xml`. Sin ese informe no se publica un porcentaje ni se supone cobertura total.

Desde `Frontend`, `npm run test:integration` ejecuta dos consultas HTTP reales; requiere la API activa en el puerto 8087 y su conexión a MySQL.
Con el backend apagado se espera un fallo de conexión; las pruebas aisladas del backend siguen siendo independientes.

## Explicación para la presentación

El [script raíz](../iniciar-demo.ps1) inicia XAMPP, API y frontend y ejecuta únicamente pruebas. El [README principal](../README.md#guía-de-presentación-de-15-minutos) contiene el recorrido completo.

Desde `src/main/java/com/huerto/hogar`, seguir una operación a través de estas capas:

1. **Autenticación:** `auth/AuthController.java` recibe login; `security` maneja JWT y `config/SecurityConfig.java` aplica accesos por rol. El registro y el perfil están en `usuario/controller/UsuarioController.java`.
2. **Catálogo:** `producto/controller/ProductoController.java` recibe HTTP; `producto/service/ProductoService.java` aplica reglas y su repositorio persiste mediante JPA. Mostrar `ProductoServiceTest.java` y `PersistenciaIntegrationTest.java`.
3. **Cupones:** `cupon/service/CuponService.java` comprueba código, actividad y vencimiento. Mostrar pruebas de fecha vencida/inactiva en `CuponServiceTest.java`.
4. **Compra:** `pedido/service/PedidoService.java` vuelve a validar el cupón, verifica stock y registra pedido/detalles. `PedidoServiceTest.java` comprueba compra válida y rechazos que no alteran stock/pedidos.
5. **Usuario y boleta:** `usuario/service/UsuarioService.java` conserva la contraseña si se omite al editar. `AdminUsuariosIntegrationTest.java` comprueba el hash y los obligatorios; `pedido/controller/PedidoController.java` expone historial/detalle.
6. **Datos y reportes:** `config/DataInitializer.java` y `DatosIniciales.java` cargan registros faltantes. Los módulos `categoria`, `blog`, `contacto` y `reporte` atienden sus vistas administrativas. `application.properties` define conexión, puerto y CORS.

Los tests fuente están en `src/test/java/com/huerto/hogar`; sus resultados no demuestran por sí solos que el proceso API esté activo. Para eso se ejecutan las dos consultas HTTP del frontend. Mostrar Swagger con el servidor encendido y explicar que JaCoCo es una medición aparte del número de tests aprobados.
