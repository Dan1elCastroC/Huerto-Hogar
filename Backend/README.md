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

## Pruebas y compilación

En la misma terminal con Java 17:

```powershell
mvn test
mvn clean package
mvn clean verify
```

`test` ejecuta las pruebas; `package` también genera `target/hogar-0.0.1-SNAPSHOT.jar`.
`verify` añade la generación del informe JaCoCo en `target/site/jacoco/index.html`.
Las pruebas de contexto e integración usan H2 mediante el
[perfil test](src/test/resources/application-test.properties), sin requerir XAMPP.
Estos comandos no sustituyen las comprobaciones manuales ni las pruebas del frontend.
