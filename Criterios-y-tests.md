# Criterios de aceptación y ubicación de las pruebas

Revisión del 29 de octubre de 2026.

Se evaluaron 450 criterios de 150 historias. 84 tienen evidencia de cumplimiento y prueba. En el Excel solo esos textos están en rojo, en Historias de Usuario, columna I. La columna M explica cada decisión y localiza sus pruebas. Los títulos y las épicas ya no usan rojo como marca de cobertura parcial.

NO ACREDITADO significa que falta evidencia completa para ese criterio concreto. Puede tratarse de una función pendiente, una parte no probada o un criterio genérico que no corresponde al módulo. No equivale a una prueba automatizada fallida.

## Qué comprueba cada ejecución

- `npm test` (desde Frontend): pruebas unitarias y de componentes. Simulan servicios con mocks. El 29/10/2026 pasaron 60 pruebas en 17 suites con el backend apagado.
- `npm run test:integration` (desde Frontend): consulta HTTP real a productos y categorías en `http://localhost:8087`, o `VITE_API_URL`. El 29/10/2026, con el backend apagado, las 2 pruebas fallaron con `ECONNREFUSED`, como corresponde. Con el backend activo se habían verificado 2 aprobadas en la revisión del 26/10/2026.
- Backend: se conserva el resultado previo de 65 pruebas sin fallos del 26/10/2026. No se volvió a arrancar el servidor ni se repitieron esas pruebas en esta edición documental.
- API de cupones: 27 comprobaciones reales previas. Las capturas y la revisión responsive son pruebas manuales; no son Vitest.

El frontend sí informa errores de conexión, sin devolver productos inventados. Las pruebas que lo cubren están en `Frontend/src/services/api.test.js` y `Frontend/src/App.test.jsx`. Pasar las pruebas unitarias no acredita que un backend esté disponible en ese momento.

## Cómo provocar un fallo de integración

Desde la carpeta Frontend, ejecutar en PowerShell:

```powershell
.\demo-fallo-integracion.ps1
```

El script usa temporalmente una URL en un puerto local libre sin backend. Ejecuta las dos pruebas reales de integración, que deben terminar con 2 failed y código de salida 1 por ECONNREFUSED. Restaura VITE_API_URL en finally, incluso si ocurre un error. La demostración se verificó el 29/10/2026 y se comprobó la restauración de la variable.

[Script de demostración](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/demo-fallo-integracion.ps1:1>)

Si el puerto 65534 está ocupado se puede elegir otro: `.\demo-fallo-integracion.ps1 -Puerto 65533`. El script no inicia ni detiene el backend y no escribe datos en la base de datos.

Las pruebas negativas habituales también están en el código: por ejemplo, Registro.test.jsx:35 comprueba que contraseñas distintas bloquean el registro, y Login.test.jsx:38 comprueba credenciales rechazadas. Esas pruebas deben pasar cuando el rechazo funciona. La demostración anterior produce fallos reales del test por conexión ausente.

## Archivos y casos exactos

### Backend/src/test/java/com/huerto/hogar/AdminUsuariosIntegrationTest.java

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/AdminUsuariosIntegrationTest.java>)

- [Línea 34: editarSinContrasenaConservaElHash](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/AdminUsuariosIntegrationTest.java:34>)
- [Línea 45: editarRechazaContrasenaFueraDeLongitudSinAlterarHash](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/AdminUsuariosIntegrationTest.java:45>)
- [Línea 57: registroSigueExigiendoContrasena](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/AdminUsuariosIntegrationTest.java:57>)
- [Línea 66: altaAdministrativaSigueExigiendoContrasena](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/AdminUsuariosIntegrationTest.java:66>)

### Backend/src/test/java/com/huerto/hogar/BlogServiceTest.java

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/BlogServiceTest.java>)

- [Línea 23: findByIdDevuelveBlogActivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/BlogServiceTest.java:23>)
- [Línea 29: findByIdNoDevuelveBlogDesactivado](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/BlogServiceTest.java:29>)
- [Línea 35: saveActivaBlog](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/BlogServiceTest.java:35>)
- [Línea 43: deleteDesactivaBlog](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/BlogServiceTest.java:43>)

### Backend/src/test/java/com/huerto/hogar/CarritoServiceTest.java

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CarritoServiceTest.java>)

- [Línea 41: agregarRechazaUsuarioInexistente](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CarritoServiceTest.java:41>)
- [Línea 46: agregarRechazaProductoInactivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CarritoServiceTest.java:46>)
- [Línea 53: agregarRechazaStockInsuficiente](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CarritoServiceTest.java:53>)
- [Línea 60: agregarCreaItemNuevo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CarritoServiceTest.java:60>)
- [Línea 71: actualizarConCantidadCeroEliminaItem](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CarritoServiceTest.java:71>)
- [Línea 81: carritoCalculaDescuentoConRedondeoMonetario](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CarritoServiceTest.java:81>)
- [Línea 92: carritoRechazaCuponInvalidoEnLugarDeOcultarlo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CarritoServiceTest.java:92>)
- [Línea 99: carritoSinCuponConservaTotalCompleto](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CarritoServiceTest.java:99>)

### Backend/src/test/java/com/huerto/hogar/CategoriaServiceTest.java

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CategoriaServiceTest.java>)

- [Línea 25: findAllDevuelveCategoriasActivas](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CategoriaServiceTest.java:25>)
- [Línea 32: findByIdInexistenteLanzaExcepcion](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CategoriaServiceTest.java:32>)
- [Línea 37: saveNoPermiteNombreRepetido](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CategoriaServiceTest.java:37>)
- [Línea 43: deleteDesactivaCategoria](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CategoriaServiceTest.java:43>)

### Backend/src/test/java/com/huerto/hogar/ContactoServiceTest.java

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/ContactoServiceTest.java>)

- [Línea 24: saveNormalizaCorreoYNombra](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/ContactoServiceTest.java:24>)
- [Línea 35: saveRechazaCorreoNoPermitido](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/ContactoServiceTest.java:35>)
- [Línea 41: marcarLeidoCambiaEstado](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/ContactoServiceTest.java:41>)
- [Línea 50: marcarLeidoInexistenteLanzaExcepcion](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/ContactoServiceTest.java:50>)

### Backend/src/test/java/com/huerto/hogar/CuponControllerTest.java

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CuponControllerTest.java>)

- [Línea 26: saveAceptaFechaVaciaComoSinVencimiento](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CuponControllerTest.java:26>)
- [Línea 35: porcentajeSoloAceptaValoresEntreUnoYCien](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CuponControllerTest.java:35>)

### Backend/src/test/java/com/huerto/hogar/CuponServiceTest.java

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CuponServiceTest.java>)

- [Línea 26: findByIdInexistenteLanzaExcepcion](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CuponServiceTest.java:26>)
- [Línea 31: findByCodigoAceptaCodigoActivoNoExpirado](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CuponServiceTest.java:31>)
- [Línea 37: findByCodigoRechazaCuponExpirado](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CuponServiceTest.java:37>)
- [Línea 43: saveConvierteCodigoAMayusculas](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CuponServiceTest.java:43>)
- [Línea 52: saveNoPermiteCodigoRepetido](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CuponServiceTest.java:52>)
- [Línea 59: findByCodigoAceptaFechaDeHoy](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CuponServiceTest.java:59>)
- [Línea 66: findByCodigoAceptaSinVencimiento](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CuponServiceTest.java:66>)
- [Línea 72: findByCodigoRechazaInexistenteOInactivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CuponServiceTest.java:72>)
- [Línea 81: findByCodigoIgnoraEspaciosExteriores](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CuponServiceTest.java:81>)
- [Línea 88: saveNormalizaEspaciosYMayusculas](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CuponServiceTest.java:88>)
- [Línea 95: saveNoDuplicaCodigoConEspacios](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CuponServiceTest.java:95>)

### Backend/src/test/java/com/huerto/hogar/HogarApplicationTests.java

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/HogarApplicationTests.java>)

- [Línea 11: contextLoads](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/HogarApplicationTests.java:11>)

### Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java>)

- [Línea 45: checkoutRechazaCarritoVacio](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:45>)
- [Línea 51: checkoutRechazaStockInsuficiente](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:51>)
- [Línea 59: checkoutCreaPedidoYVacíaCarrito](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:59>)
- [Línea 71: findByEstadoRechazaEstadoInvalido](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:71>)
- [Línea 75: cambiarEstadoActualizaPedido](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:75>)
- [Línea 86: cambiarEstadoRechazaEstadoInvalido](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:86>)
- [Línea 95: findByIdInexistenteLanzaExcepcion](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:95>)
- [Línea 100: checkoutRevalidaCuponAntesDeModificarStockYGuardar](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:100>)
- [Línea 112: checkoutCalculaDescuentoConRedondeoMonetario](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:112>)
- [Línea 126: checkoutPermiteCuponCienPorCientoSinTotalNegativo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:126>)
- [Línea 136: respuestaIncluyeUsuarioIdParaHistorialAdministrativo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:136>)

### Backend/src/test/java/com/huerto/hogar/PersistenciaIntegrationTest.java

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PersistenciaIntegrationTest.java>)

- [Línea 39: catalogoInicialSePueblaSinDuplicarseAlReiniciar](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PersistenciaIntegrationTest.java:39>)
- [Línea 63: productoGuardadoSePuedeConsultarConSuCategoria](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PersistenciaIntegrationTest.java:63>)
- [Línea 76: compraGuardadaSePuedeConsultarConSusDetalles](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PersistenciaIntegrationTest.java:76>)

### Backend/src/test/java/com/huerto/hogar/ProductoServiceTest.java

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/ProductoServiceTest.java>)

- [Línea 48: findByIdDevuelveProducto](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/ProductoServiceTest.java:48>)
- [Línea 55: findByIdInexistenteLanzaExcepcion](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/ProductoServiceTest.java:55>)
- [Línea 60: saveNoPermiteCodigoRepetido](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/ProductoServiceTest.java:60>)
- [Línea 66: saveRechazaCategoriaInexistente](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/ProductoServiceTest.java:66>)
- [Línea 72: saveCreaProductoCorrectamente](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/ProductoServiceTest.java:72>)
- [Línea 82: deleteDesactivaProducto](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/ProductoServiceTest.java:82>)

### Backend/src/test/java/com/huerto/hogar/SwaggerOpenApiTest.java

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/SwaggerOpenApiTest.java>)

- [Línea 24: publicaDocumentoOpenApiConRutasDeLaTienda](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/SwaggerOpenApiTest.java:24>)
- [Línea 34: exponeLaInterfazSwagger](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/SwaggerOpenApiTest.java:34>)

### Backend/src/test/java/com/huerto/hogar/UsuarioServiceTest.java

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/UsuarioServiceTest.java>)

- [Línea 39: registrarRechazaContrasenasDiferentes](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/UsuarioServiceTest.java:39>)
- [Línea 45: registrarRechazaCorreoNoPermitido](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/UsuarioServiceTest.java:45>)
- [Línea 52: findByIdInexistenteLanzaExcepcion](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/UsuarioServiceTest.java:52>)
- [Línea 57: cambiarContrasenaRechazaContrasenaActualIncorrecta](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/UsuarioServiceTest.java:57>)
- [Línea 67: toggleActivoCambiaEstado](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/UsuarioServiceTest.java:67>)

### Frontend/src/App.test.jsx

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx>)

- [Línea 26: navega por nosotros, categorías y detalle sin perder el filtro al volver](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:26>)
- [Línea 48: abre una noticia real y permite regresar al listado](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:48>)
- [Línea 59: muestra ofertas sólo después de validar el cupón público](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:59>)
- [Línea 69: no anuncia descuentos de un cupón vencido](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:69>)
- [Línea 77: informa en el catálogo cuando falla la conexión al backend](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:77>)
- [Línea 86: cierra la sesión y mantiene al usuario desconectado al recargar](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:86>)
- [Línea 110: actualiza las categorías disponibles al crear y eliminar una categoría](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:110>)

### Frontend/src/components/admin/CategoryManager.test.jsx

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/admin/CategoryManager.test.jsx>)

- [Línea 8: editar categoría usa el endpoint existente y refleja el cambio](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/admin/CategoryManager.test.jsx:8>)

### Frontend/src/components/admin/OrderManager.test.jsx

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/admin/OrderManager.test.jsx>)

- [Línea 7: muestra la boleta persistida con productos, descuento y total](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/admin/OrderManager.test.jsx:7>)

### Frontend/src/components/admin/UserManager.test.jsx

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/admin/UserManager.test.jsx>)

- [Línea 12: permite crear un usuario administrador desde un formulario completo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/admin/UserManager.test.jsx:12>)
- [Línea 26: permite editar un usuario conservando su identificador](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/admin/UserManager.test.jsx:26>)
- [Línea 38: el historial del usuario muestra únicamente sus compras](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/admin/UserManager.test.jsx:38>)

### Frontend/src/components/blog/BlogList.test.jsx

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/blog/BlogList.test.jsx>)

- [Línea 6: muestra noticias publicadas](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/blog/BlogList.test.jsx:6>)
- [Línea 13: muestra sus estados de carga y vacío](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/blog/BlogList.test.jsx:13>)

### Frontend/src/components/Carrito.test.jsx

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx>)

- [Línea 29: muestra el total y permite modificar el carrito](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:29>)
- [Línea 42: confirma una compra sin realizar llamadas de red desde el componente](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:42>)
- [Línea 58: muestra el estado vacío cuando no hay productos](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:58>)
- [Línea 64: aplica un cupón validado y actualiza el descuento al cambiar cantidades](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:64>)
- [Línea 81: redondea a dos decimales como el backend para subtotal %s y porcentaje %s](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:81>)
- [Línea 96: quita el cupón y no lo envía al confirmar la compra](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:96>)
- [Línea 115: muestra el error %s y permite comprar sin cupón](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:115>)
- [Línea 132: no envía un código escrito que todavía no fue aplicado](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:132>)
- [Línea 144: envía el código canónico y muestra un vencimiento detectado al confirmar](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:144>)
- [Línea 160: muestra los datos reales de la cuenta y la dirección autocompletada](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:160>)
- [Línea 171: envía la fecha elegida al programar la entrega](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:171>)
- [Línea 184: limpia la fecha al cambiar a próxima disponibilidad y no la envía](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:184>)
- [Línea 200: rechaza una entrega programada sin fecha válida: %s](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:200>)
- [Línea 212: usa el día local al programar una entrega de noche](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:212>)
- [Línea 226: mantiene el acceso a iniciar sesión para un carrito anónimo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:226>)

### Frontend/src/components/contact/ContactoForm.test.jsx

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/contact/ContactoForm.test.jsx>)

- [Línea 17: envía el mensaje y muestra confirmación sin depender del backend](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/contact/ContactoForm.test.jsx:17>)

### Frontend/src/components/Login.test.jsx

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Login.test.jsx>)

- [Línea 18: envía las credenciales y entrega la sesión junto con el perfil](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Login.test.jsx:18>)
- [Línea 38: muestra un mensaje cuando las credenciales son rechazadas](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Login.test.jsx:38>)

### Frontend/src/components/MainPage.test.jsx

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/MainPage.test.jsx>)

- [Línea 9: editar producto persiste sus campos y actualiza el inventario](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/MainPage.test.jsx:9>)
- [Línea 20: filtra el inventario por stock crítico](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/MainPage.test.jsx:20>)

### Frontend/src/components/orders/OrderList.test.jsx

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/orders/OrderList.test.jsx>)

- [Línea 6: permite abrir el detalle de un pedido](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/orders/OrderList.test.jsx:6>)
- [Línea 23: muestra el estado vacío](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/orders/OrderList.test.jsx:23>)

### Frontend/src/components/ProductList.test.jsx

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/ProductList.test.jsx>)

- [Línea 11: muestra los productos y permite agregarlos al carrito](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/ProductList.test.jsx:11>)
- [Línea 22: filtra por texto y categoría](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/ProductList.test.jsx:22>)
- [Línea 35: informa cuando los productos están cargando](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/ProductList.test.jsx:35>)
- [Línea 42: muestra el error de conexión sin ofrecer productos](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/ProductList.test.jsx:42>)

### Frontend/src/components/profile/PasswordForm.test.jsx

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/profile/PasswordForm.test.jsx>)

- [Línea 6: rechaza una confirmación de contraseña diferente](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/profile/PasswordForm.test.jsx:6>)
- [Línea 19: entrega las contraseñas cuando la confirmación es válida](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/profile/PasswordForm.test.jsx:19>)

### Frontend/src/components/profile/ProfileForm.test.jsx

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/profile/ProfileForm.test.jsx>)

- [Línea 6: permite editar los datos y guardarlos](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/profile/ProfileForm.test.jsx:6>)

### Frontend/src/components/Registro.test.jsx

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Registro.test.jsx>)

- [Línea 35: valida que las contraseñas coincidan antes de llamar al servicio](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Registro.test.jsx:35>)
- [Línea 45: registra los datos y muestra la confirmación](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Registro.test.jsx:45>)

### Frontend/src/pages/AdminPage.test.jsx

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/pages/AdminPage.test.jsx>)

- [Línea 9: abre el dashboard con indicadores del backend y accesos a tienda y sesión](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/pages/AdminPage.test.jsx:9>)

### Frontend/src/services/api.integration.test.js

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/services/api.integration.test.js>)

- [Línea 6: consulta productos reales del backend y no datos simulados](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/services/api.integration.test.js:6>)
- [Línea 15: consulta categorías reales del backend](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/services/api.integration.test.js:15>)

### Frontend/src/services/api.test.js

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/services/api.test.js>)

- [Línea 16: normalizes the backend product shape](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/services/api.test.js:16>)
- [Línea 39: reports network failures instead of returning invented products](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/services/api.test.js:39>)
- [Línea 45: reports backend errors instead of returning invented products](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/services/api.test.js:45>)
- [Línea 51: sends a token when creating a product and normalizes the response](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/services/api.test.js:51>)

### Frontend/src/services/cartUtils.test.js

[Abrir archivo](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/services/cartUtils.test.js>)

- [Línea 10: adds a new product with one unit without mutating the cart](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/services/cartUtils.test.js:10>)
- [Línea 20: increments an existing product but never exceeds stock](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/services/cartUtils.test.js:20>)
- [Línea 28: updates quantity and removes the product when quantity reaches zero](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/services/cartUtils.test.js:28>)
- [Línea 39: removes only the selected product](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/services/cartUtils.test.js:39>)
- [Línea 45: calculates the total using numeric and string prices](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/services/cartUtils.test.js:45>)

## Revisión de cada historia

### HU-001 — Acceso principal a registro e inicio de sesión de usuarios

Excel: Historias de Usuario, I2 y M2. Épica: EP-01: Registro e Inicio de Sesión de Usuarios.

- CA1: **NO ACREDITADO**. App prueba acceso a iniciar sesión, pero no el acceso a registro; el criterio conjunto queda parcial.
- CA2: **CUMPLIDO Y PROBADO**. Formularios etiquetados, envío de credenciales/datos y confirmación de registro comprobados.
  - [Frontend/src/components/Login.test.jsx:18](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Login.test.jsx:18>) — `envía las credenciales y entrega la sesión junto con el perfil`
  - [Frontend/src/components/Registro.test.jsx:45](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Registro.test.jsx:45>) — `registra los datos y muestra la confirmación`
- CA3: **NO ACREDITADO**. Sin prueba responsive específica de esta vista; Vitest no acredita móvil/escritorio.

### HU-002 — Validación de datos en registro e inicio de sesión de usuarios

Excel: Historias de Usuario, I3 y M3. Épica: EP-01: Registro e Inicio de Sesión de Usuarios.

- CA1: **NO ACREDITADO**. No se probaron vacíos para el conjunto de campos obligatorios del formulario.
- CA2: **NO ACREDITADO**. El error se presenta como alerta general; no junto al campo con problema.
- CA3: **CUMPLIDO Y PROBADO**. Registro bloquea contraseñas distintas y login rechaza credenciales inválidas.
  - [Frontend/src/components/Registro.test.jsx:35](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Registro.test.jsx:35>) — `valida que las contraseñas coincidan antes de llamar al servicio`
  - [Frontend/src/components/Login.test.jsx:38](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Login.test.jsx:38>) — `muestra un mensaje cuando las credenciales son rechazadas`

### HU-003 — Consulta y seguimiento de registro e inicio de sesión de usuarios

Excel: Historias de Usuario, I4 y M4. Épica: EP-01: Registro e Inicio de Sesión de Usuarios.

- CA1: **NO ACREDITADO**. No hay listado cronológico de registros o accesos probado.
- CA2: **NO ACREDITADO**. No hay buscador/filtro de registros o accesos.
- CA3: **CUMPLIDO Y PROBADO**. El estado de sesión y el nombre del usuario cambian inmediatamente al iniciar/cerrar sesión.
  - [Frontend/src/App.test.jsx:86](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:86>) — `cierra la sesión y mantiene al usuario desconectado al recargar`

### HU-004 — Acceso principal a recuperación y cambio de contraseña

Excel: Historias de Usuario, I5 y M5. Épica: EP-02: Recuperación y Cambio de Contraseña.

- CA1: **NO ACREDITADO**. No se prueba navegación al cambio de contraseña y falta recuperación por olvido.
- CA2: **NO ACREDITADO**. Se prueba el formulario de cambio; la recuperación por olvido no está implementada.
- CA3: **NO ACREDITADO**. Sin prueba responsive específica de esta vista; Vitest no acredita móvil/escritorio.

### HU-005 — Validación de datos en recuperación y cambio de contraseña

Excel: Historias de Usuario, I6 y M6. Épica: EP-02: Recuperación y Cambio de Contraseña.

- CA1: **NO ACREDITADO**. No se probaron vacíos para el conjunto de campos obligatorios del formulario.
- CA2: **NO ACREDITADO**. El error se presenta como alerta general; no junto al campo con problema.
- CA3: **CUMPLIDO Y PROBADO**. Una confirmación distinta y la contraseña actual incorrecta impiden guardar el cambio.
  - [Frontend/src/components/profile/PasswordForm.test.jsx:6](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/profile/PasswordForm.test.jsx:6>) — `rechaza una confirmación de contraseña diferente`
  - [Backend/src/test/java/com/huerto/hogar/UsuarioServiceTest.java:57](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/UsuarioServiceTest.java:57>) — `cambiarContrasenaRechazaContrasenaActualIncorrecta`

### HU-006 — Consulta y seguimiento de recuperación y cambio de contraseña

Excel: Historias de Usuario, I7 y M7. Épica: EP-02: Recuperación y Cambio de Contraseña.

- CA1: **NO ACREDITADO**. No hay historial cronológico de cambios/recuperaciones.
- CA2: **NO ACREDITADO**. La interfaz de esta funcionalidad no ofrece búsqueda ni filtro comprobados.
- CA3: **NO ACREDITADO**. No se comprobó reflejar cambios de datos en esta vista sin recargar la página.

### HU-007 — Acceso principal a perfil de usuario

Excel: Historias de Usuario, I8 y M8. Épica: EP-03: Perfil de Usuario.

- CA1: **NO ACREDITADO**. Existe implementación, pero no prueba de acceso a esta vista desde la navegación principal.
- CA2: **CUMPLIDO Y PROBADO**. El formulario renderiza los datos personales, permite editar el nombre y enviarlo.
  - [Frontend/src/components/profile/ProfileForm.test.jsx:6](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/profile/ProfileForm.test.jsx:6>) — `permite editar los datos y guardarlos`
- CA3: **NO ACREDITADO**. Sin prueba responsive específica de esta vista; Vitest no acredita móvil/escritorio.

### HU-008 — Validación de datos en perfil de usuario

Excel: Historias de Usuario, I9 y M9. Épica: EP-03: Perfil de Usuario.

- CA1: **NO ACREDITADO**. No se probaron vacíos para el conjunto de campos obligatorios del formulario.
- CA2: **NO ACREDITADO**. No hay errores específicos asociados a los campos del perfil.
- CA3: **NO ACREDITADO**. No existe prueba negativa que bloquee el guardado de un perfil inválido.

### HU-009 — Consulta y seguimiento de perfil de usuario

Excel: Historias de Usuario, I10 y M10. Épica: EP-03: Perfil de Usuario.

- CA1: **NO ACREDITADO**. El perfil no ofrece un historial cronológico de cambios.
- CA2: **NO ACREDITADO**. La interfaz de esta funcionalidad no ofrece búsqueda ni filtro comprobados.
- CA3: **NO ACREDITADO**. La prueba valida el callback del formulario; no la actualización de perfil desde la API en la vista.

### HU-010 — Acceso principal a roles y permisos del sistema

Excel: Historias de Usuario, I11 y M11. Épica: EP-04: Roles y Permisos del Sistema.

- CA1: **NO ACREDITADO**. No se prueba navegar desde el menú principal a la gestión de roles/permisos.
- CA2: **CUMPLIDO Y PROBADO**. El formulario permite seleccionar rol y crear un usuario administrador; la lista refleja al usuario.
  - [Frontend/src/components/admin/UserManager.test.jsx:12](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/admin/UserManager.test.jsx:12>) — `permite crear un usuario administrador desde un formulario completo`
- CA3: **NO ACREDITADO**. Sin prueba responsive específica de esta vista; Vitest no acredita móvil/escritorio.

### HU-011 — Validación de datos en roles y permisos del sistema

Excel: Historias de Usuario, I12 y M12. Épica: EP-04: Roles y Permisos del Sistema.

- CA1: **NO ACREDITADO**. No se probaron vacíos de todos los campos requeridos de gestión de usuarios/roles.
- CA2: **NO ACREDITADO**. El error se presenta como alerta general; no junto al campo con problema.
- CA3: **CUMPLIDO Y PROBADO**. Un cliente recibe 403 al intentar una acción restringida a administración.
  - [C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/cupones-api-real.json:120](<C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/cupones-api-real.json:120>) — `Cliente no crea cupones`

### HU-012 — Consulta y seguimiento de roles y permisos del sistema

Excel: Historias de Usuario, I13 y M13. Épica: EP-04: Roles y Permisos del Sistema.

- CA1: **NO ACREDITADO**. No existe prueba con varios registros y fechas que compruebe orden más reciente primero.
- CA2: **NO ACREDITADO**. No hay búsqueda/filtro de roles/permisos en la interfaz.
- CA3: **NO ACREDITADO**. La prueba crea un usuario, pero no comprueba un cambio de permisos reflejado en la interfaz.

### HU-013 — Acceso principal a página principal (home)

Excel: Historias de Usuario, I14 y M14. Épica: EP-05: Página Principal (Home).

- CA1: **NO ACREDITADO**. El enlace Home está implementado, pero no hay prueba de volver a Home desde la navegación.
- CA2: **NO ACREDITADO**. No hay aserciones específicas del contenido principal de Home ni revisión visual de esa vista.
- CA3: **NO ACREDITADO**. Sin prueba responsive específica de esta vista; Vitest no acredita móvil/escritorio.

### HU-014 — Validación de datos en página principal (home)

Excel: Historias de Usuario, I15 y M15. Épica: EP-05: Página Principal (Home).

- CA1: **NO ACREDITADO**. Home no tiene formulario de datos obligatorios cubierto por pruebas.
- CA2: **NO ACREDITADO**. No hay errores de campos de Home implementados y probados.
- CA3: **NO ACREDITADO**. No se prueba bloquear un proceso de Home ante datos inválidos.

### HU-015 — Consulta y seguimiento de página principal (home)

Excel: Historias de Usuario, I16 y M16. Épica: EP-05: Página Principal (Home).

- CA1: **NO ACREDITADO**. Los destacados usan el orden de la API; no hay orden reciente probado.
- CA2: **NO ACREDITADO**. Home no presenta búsqueda/filtro propios.
- CA3: **NO ACREDITADO**. No se prueban cambios de los destacados de Home sin recargar.

### HU-016 — Acceso principal a catálogo de productos

Excel: Historias de Usuario, I17 y M17. Épica: EP-06: Catálogo de Productos.

- CA1: **CUMPLIDO Y PROBADO**. El enlace Productos lleva al catálogo y muestra su estado de conexión.
  - [Frontend/src/App.test.jsx:77](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:77>) — `informa en el catálogo cuando falla la conexión al backend`
- CA2: **CUMPLIDO Y PROBADO**. El listado muestra productos y sus acciones; también se comprobaron carga y error.
  - [Frontend/src/components/ProductList.test.jsx:11](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/ProductList.test.jsx:11>) — `muestra los productos y permite agregarlos al carrito`
  - [Frontend/src/components/ProductList.test.jsx:35](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/ProductList.test.jsx:35>) — `informa cuando los productos están cargando`
  - [Frontend/src/components/ProductList.test.jsx:42](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/ProductList.test.jsx:42>) — `muestra el error de conexión sin ofrecer productos`
- CA3: **CUMPLIDO Y PROBADO**. Catálogo revisado en Chrome móvil/escritorio; capturas catálogo móvil 360 y escritorio 1440.
  - [C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto/verificacion-final.json:14](<C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto/verificacion-final.json:14>) — `Revisión manual Chrome: catálogo a 320, 360, 768, 1024 y 1440 px, sin desbordamiento horizontal`

### HU-017 — Validación de datos en catálogo de productos

Excel: Historias de Usuario, I18 y M18. Épica: EP-06: Catálogo de Productos.

- CA1: **NO ACREDITADO**. El catálogo público no tiene formulario de campos obligatorios cubierto por pruebas.
- CA2: **NO ACREDITADO**. No hay validación de campos del catálogo con mensaje junto a cada campo.
- CA3: **NO ACREDITADO**. El manejo de error de conexión no prueba validación de datos de formulario.

### HU-018 — Consulta y seguimiento de catálogo de productos

Excel: Historias de Usuario, I19 y M19. Épica: EP-06: Catálogo de Productos.

- CA1: **NO ACREDITADO**. El listado conserva el orden recibido y no se prueba orden cronológico reciente.
- CA2: **CUMPLIDO Y PROBADO**. Se prueban búsqueda de texto y filtro por categoría.
  - [Frontend/src/components/ProductList.test.jsx:22](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/ProductList.test.jsx:22>) — `filtra por texto y categoría`
- CA3: **CUMPLIDO Y PROBADO**. Cambiar búsqueda/categoría actualiza los productos visibles inmediatamente.
  - [Frontend/src/components/ProductList.test.jsx:22](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/ProductList.test.jsx:22>) — `filtra por texto y categoría`

### HU-019 — Acceso principal a detalle de producto

Excel: Historias de Usuario, I20 y M20. Épica: EP-07: Detalle de Producto.

- CA1: **CUMPLIDO Y PROBADO**. Desde Categorías se accede al producto y a su detalle, y se vuelve al catálogo.
  - [Frontend/src/App.test.jsx:26](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:26>) — `navega por nosotros, categorías y detalle sin perder el filtro al volver`
- CA2: **CUMPLIDO Y PROBADO**. Se comprueba la descripción del producto y el botón para añadirlo al carrito.
  - [Frontend/src/App.test.jsx:26](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:26>) — `navega por nosotros, categorías y detalle sin perder el filtro al volver`
- CA3: **NO ACREDITADO**. La revisión responsive del listado de catálogo no comprueba la vista de detalle.

### HU-020 — Validación de datos en detalle de producto

Excel: Historias de Usuario, I21 y M21. Épica: EP-07: Detalle de Producto.

- CA1: **NO ACREDITADO**. El detalle no tiene campos obligatorios de entrada probados.
- CA2: **NO ACREDITADO**. No hay mensajes de validación junto a campos del detalle.
- CA3: **NO ACREDITADO**. El botón sin stock está implementado, pero no se prueba ese bloqueo en la vista de detalle.

### HU-021 — Consulta y seguimiento de detalle de producto

Excel: Historias de Usuario, I22 y M22. Épica: EP-07: Detalle de Producto.

- CA1: **NO ACREDITADO**. El detalle individual no ofrece listado más reciente primero.
- CA2: **NO ACREDITADO**. No hay buscador/filtro dentro de la vista de detalle.
- CA3: **CUMPLIDO Y PROBADO**. Añadir desde el detalle actualiza el contador del carrito sin recargar.
  - [Frontend/src/App.test.jsx:26](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:26>) — `navega por nosotros, categorías y detalle sin perder el filtro al volver`

### HU-022 — Acceso principal a búsqueda de productos

Excel: Historias de Usuario, I23 y M23. Épica: EP-08: Búsqueda de Productos.

- CA1: **CUMPLIDO Y PROBADO**. El catálogo es accesible desde Productos y contiene el control de búsqueda probado.
  - [Frontend/src/App.test.jsx:77](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:77>) — `informa en el catálogo cuando falla la conexión al backend`
  - [Frontend/src/components/ProductList.test.jsx:22](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/ProductList.test.jsx:22>) — `filtra por texto y categoría`
- CA2: **CUMPLIDO Y PROBADO**. La entrada de texto muestra el producto coincidente y oculta el que no coincide.
  - [Frontend/src/components/ProductList.test.jsx:22](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/ProductList.test.jsx:22>) — `filtra por texto y categoría`
- CA3: **CUMPLIDO Y PROBADO**. El buscador está visible y cabe en el catálogo móvil revisado; captura catálogo móvil 360.
  - [C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto/verificacion-final.json:14](<C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto/verificacion-final.json:14>) — `Revisión manual Chrome: catálogo a 320, 360, 768, 1024 y 1440 px, sin desbordamiento horizontal`
  - [Frontend/src/components/ProductList.test.jsx:22](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/ProductList.test.jsx:22>) — `filtra por texto y categoría`

### HU-023 — Validación de datos en búsqueda de productos

Excel: Historias de Usuario, I24 y M24. Épica: EP-08: Búsqueda de Productos.

- CA1: **NO ACREDITADO**. La búsqueda admite texto vacío; no hay campos obligatorios del proceso probados.
- CA2: **NO ACREDITADO**. No se implementa error específico junto al buscador.
- CA3: **NO ACREDITADO**. No hay regla de error de búsqueda que bloquee continuar y esté probada.

### HU-024 — Consulta y seguimiento de búsqueda de productos

Excel: Historias de Usuario, I25 y M25. Épica: EP-08: Búsqueda de Productos.

- CA1: **NO ACREDITADO**. La búsqueda filtra pero no ordena por fecha reciente.
- CA2: **CUMPLIDO Y PROBADO**. Se encuentra un producto usando texto de su nombre.
  - [Frontend/src/components/ProductList.test.jsx:22](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/ProductList.test.jsx:22>) — `filtra por texto y categoría`
- CA3: **CUMPLIDO Y PROBADO**. Los resultados cambian al escribir y limpiar la búsqueda sin recargar.
  - [Frontend/src/components/ProductList.test.jsx:22](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/ProductList.test.jsx:22>) — `filtra por texto y categoría`

### HU-025 — Acceso principal a filtros y ordenamiento de catálogo

Excel: Historias de Usuario, I26 y M26. Épica: EP-09: Filtros y Ordenamiento de Catálogo.

- CA1: **NO ACREDITADO**. El filtro es accesible en catálogo, pero no existe control de ordenamiento; criterio conjunto parcial.
- CA2: **NO ACREDITADO**. Se prueba el filtro de categoría, pero falta interfaz de ordenamiento del catálogo.
- CA3: **NO ACREDITADO**. La revisión muestra el filtro, pero no acredita la funcionalidad conjunta con ordenamiento ausente.

### HU-026 — Validación de datos en filtros y ordenamiento de catálogo

Excel: Historias de Usuario, I27 y M27. Épica: EP-09: Filtros y Ordenamiento de Catálogo.

- CA1: **NO ACREDITADO**. Los filtros no tienen campos obligatorios cuya validación se haya probado.
- CA2: **NO ACREDITADO**. No hay mensajes de error junto a los filtros.
- CA3: **NO ACREDITADO**. No se prueba ningún bloqueo por filtro/ordenamiento inválido.

### HU-027 — Consulta y seguimiento de filtros y ordenamiento de catálogo

Excel: Historias de Usuario, I28 y M28. Épica: EP-09: Filtros y Ordenamiento de Catálogo.

- CA1: **NO ACREDITADO**. No hay ordenamiento de productos por fecha reciente implementado ni probado.
- CA2: **CUMPLIDO Y PROBADO**. El catálogo se filtra por categoría y texto.
  - [Frontend/src/components/ProductList.test.jsx:22](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/ProductList.test.jsx:22>) — `filtra por texto y categoría`
- CA3: **CUMPLIDO Y PROBADO**. El listado se actualiza al cambiar categoría sin recargar.
  - [Frontend/src/components/ProductList.test.jsx:22](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/ProductList.test.jsx:22>) — `filtra por texto y categoría`

### HU-028 — Acceso principal a categorías de productos

Excel: Historias de Usuario, I29 y M29. Épica: EP-10: Categorías de Productos.

- CA1: **CUMPLIDO Y PROBADO**. Se navega desde Categorías a los productos de Frutas.
  - [Frontend/src/App.test.jsx:26](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:26>) — `navega por nosotros, categorías y detalle sin perder el filtro al volver`
- CA2: **CUMPLIDO Y PROBADO**. La interfaz muestra la categoría seleccionable y los productos correspondientes.
  - [Frontend/src/App.test.jsx:26](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:26>) — `navega por nosotros, categorías y detalle sin perder el filtro al volver`
- CA3: **NO ACREDITADO**. No se revisó responsive la página de tarjetas de categorías.

### HU-029 — Validación de datos en categorías de productos

Excel: Historias de Usuario, I30 y M30. Épica: EP-10: Categorías de Productos.

- CA1: **NO ACREDITADO**. No hay prueba de vacíos para todos los campos obligatorios de categoría.
- CA2: **NO ACREDITADO**. La gestión de categorías muestra alerta general, sin error asociado a campo.
- CA3: **CUMPLIDO Y PROBADO**. El nombre duplicado impide guardar una categoría.
  - [Backend/src/test/java/com/huerto/hogar/CategoriaServiceTest.java:37](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CategoriaServiceTest.java:37>) — `saveNoPermiteNombreRepetido`

### HU-030 — Consulta y seguimiento de categorías de productos

Excel: Historias de Usuario, I31 y M31. Épica: EP-10: Categorías de Productos.

- CA1: **NO ACREDITADO**. Las categorías activas no están ordenadas por fecha reciente ni se prueba ese orden.
- CA2: **CUMPLIDO Y PROBADO**. Seleccionar Frutas filtra el catálogo y excluye Verduras.
  - [Frontend/src/App.test.jsx:26](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:26>) — `navega por nosotros, categorías y detalle sin perder el filtro al volver`
- CA3: **CUMPLIDO Y PROBADO**. Crear/eliminar una categoría actualiza las opciones de productos sin recargar.
  - [Frontend/src/App.test.jsx:110](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:110>) — `actualiza las categorías disponibles al crear y eliminar una categoría`

### HU-031 — Acceso principal a carrito de compras

Excel: Historias de Usuario, I32 y M32. Épica: EP-11: Carrito de Compras.

- CA1: **NO ACREDITADO**. App prueba el contador/enlace Carrito, pero no el click y la apertura desde navegación principal.
- CA2: **CUMPLIDO Y PROBADO**. Se comprueban productos, totales, cantidades, eliminación y estado vacío.
  - [Frontend/src/components/Carrito.test.jsx:29](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:29>) — `muestra el total y permite modificar el carrito`
  - [Frontend/src/components/Carrito.test.jsx:58](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:58>) — `muestra el estado vacío cuando no hay productos`
- CA3: **CUMPLIDO Y PROBADO**. El carrito visible dentro de checkout fue revisado en Chrome móvil/escritorio.
  - [C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto/verificacion-final.json:14](<C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto/verificacion-final.json:14>) — `Revisión manual Chrome: checkout a 320, 360, 768, 1024 y 1440 px, sin desbordamiento horizontal`

### HU-032 — Validación de datos en carrito de compras

Excel: Historias de Usuario, I33 y M33. Épica: EP-11: Carrito de Compras.

- CA1: **NO ACREDITADO**. No se prueba la validación del conjunto de datos obligatorios del carrito.
- CA2: **NO ACREDITADO**. No se muestran errores asociados a cada control de cantidad.
- CA3: **CUMPLIDO Y PROBADO**. Stock insuficiente impide agregar; el cálculo local tampoco excede stock.
  - [Backend/src/test/java/com/huerto/hogar/CarritoServiceTest.java:53](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/CarritoServiceTest.java:53>) — `agregarRechazaStockInsuficiente`
  - [Frontend/src/services/cartUtils.test.js:20](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/services/cartUtils.test.js:20>) — `increments an existing product but never exceeds stock`

### HU-033 — Consulta y seguimiento de carrito de compras

Excel: Historias de Usuario, I34 y M34. Épica: EP-11: Carrito de Compras.

- CA1: **NO ACREDITADO**. Los ítems conservan orden de inserción; no hay orden reciente probado.
- CA2: **NO ACREDITADO**. No existe buscador/filtro del carrito.
- CA3: **CUMPLIDO Y PROBADO**. El contador y los descuentos se actualizan ante cambios de productos/cantidades sin recargar.
  - [Frontend/src/App.test.jsx:26](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:26>) — `navega por nosotros, categorías y detalle sin perder el filtro al volver`
  - [Frontend/src/components/Carrito.test.jsx:64](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:64>) — `aplica un cupón validado y actualiza el descuento al cambiar cantidades`

### HU-034 — Acceso principal a persistencia del carrito (localstorage)

Excel: Historias de Usuario, I35 y M35. Épica: EP-12: Persistencia del Carrito (localStorage).

- CA1: **NO ACREDITADO**. La persistencia es interna; no se prueba acceso/recuperación del carrito desde el menú.
- CA2: **NO ACREDITADO**. No hay prueba de restauración visual del carrito guardado en localStorage.
- CA3: **NO ACREDITADO**. La revisión responsive de checkout no prueba persistencia del carrito al recargar.

### HU-035 — Validación de datos en persistencia del carrito (localstorage)

Excel: Historias de Usuario, I36 y M36. Épica: EP-12: Persistencia del Carrito (localStorage).

- CA1: **NO ACREDITADO**. No se prueba la validación de datos obligatorios del carrito almacenado.
- CA2: **NO ACREDITADO**. No hay errores junto a campos para datos almacenados inválidos.
- CA3: **NO ACREDITADO**. La recuperación de JSON corrupto está implementada, pero no probada.

### HU-036 — Consulta y seguimiento de persistencia del carrito (localstorage)

Excel: Historias de Usuario, I37 y M37. Épica: EP-12: Persistencia del Carrito (localStorage).

- CA1: **NO ACREDITADO**. No hay historial cronológico de versiones del carrito.
- CA2: **NO ACREDITADO**. No hay búsqueda/filtro de carritos almacenados.
- CA3: **NO ACREDITADO**. App escribe en localStorage, pero ninguna prueba afirma persistencia/restauración del carrito.

### HU-037 — Acceso principal a aplicación de cupones de descuento

Excel: Historias de Usuario, I38 y M38. Épica: EP-13: Aplicación de Cupones de Descuento.

- CA1: **NO ACREDITADO**. Se monta el checkout en pruebas, pero no se comprueba acceso al cupón desde navegación principal.
- CA2: **CUMPLIDO Y PROBADO**. Aplicar/quitar el código muestra descuento y total correctos.
  - [Frontend/src/components/Carrito.test.jsx:64](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:64>) — `aplica un cupón validado y actualiza el descuento al cambiar cantidades`
  - [Frontend/src/components/Carrito.test.jsx:96](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:96>) — `quita el cupón y no lo envía al confirmar la compra`
- CA3: **CUMPLIDO Y PROBADO**. Cupón, descuento y controles visibles en móvil; checkout revisado también en escritorio.
  - [C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto/verificacion-final.json:14](<C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto/verificacion-final.json:14>) — `Revisión manual Chrome: checkout a 320, 360, 768, 1024 y 1440 px, sin desbordamiento horizontal`

### HU-038 — Validación de datos en aplicación de cupones de descuento

Excel: Historias de Usuario, I39 y M39. Épica: EP-13: Aplicación de Cupones de Descuento.

- CA1: **NO ACREDITADO**. El cupón es opcional y no se prueba aplicar con código vacío.
- CA2: **CUMPLIDO Y PROBADO**. El error específico del cupón se renderiza junto al campo y enlazado por aria-describedby.
  - [Frontend/src/components/Carrito.test.jsx:116](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:116>) — `muestra el error %s y permite comprar sin cupón`
- CA3: **CUMPLIDO Y PROBADO**. El cupón vencido impide confirmar con ese cupón y conserva stock/pedidos.
  - [Frontend/src/components/Carrito.test.jsx:144](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:144>) — `envía el código canónico y muestra un vencimiento detectado al confirmar`
  - [Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:100](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:100>) — `checkoutRevalidaCuponAntesDeModificarStockYGuardar`

### HU-039 — Consulta y seguimiento de aplicación de cupones de descuento

Excel: Historias de Usuario, I40 y M40. Épica: EP-13: Aplicación de Cupones de Descuento.

- CA1: **NO ACREDITADO**. No hay historial de aplicación de cupones ordenado por fecha reciente.
- CA2: **NO ACREDITADO**. El campo de código valida un cupón; no es búsqueda/filtro de un listado.
- CA3: **CUMPLIDO Y PROBADO**. Aplicar/quitar cupón y cambiar cantidades recalcula descuento/total sin recargar.
  - [Frontend/src/components/Carrito.test.jsx:64](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:64>) — `aplica un cupón validado y actualiza el descuento al cambiar cantidades`
  - [Frontend/src/components/Carrito.test.jsx:96](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:96>) — `quita el cupón y no lo envía al confirmar la compra`

### HU-040 — Acceso principal a proceso de checkout

Excel: Historias de Usuario, I41 y M41. Épica: EP-14: Proceso de Checkout.

- CA1: **NO ACREDITADO**. Las pruebas abren checkout montando Carrito, sin verificar su acceso desde navegación principal.
- CA2: **CUMPLIDO Y PROBADO**. Se comprueban dirección, datos de cuenta, total y confirmación del pedido.
  - [Frontend/src/components/Carrito.test.jsx:42](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:42>) — `confirma una compra sin realizar llamadas de red desde el componente`
  - [Frontend/src/components/Carrito.test.jsx:160](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:160>) — `muestra los datos reales de la cuenta y la dirección autocompletada`
- CA3: **CUMPLIDO Y PROBADO**. Checkout revisado en Chrome a cinco anchos de móvil a escritorio.
  - [C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto/verificacion-final.json:14](<C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto/verificacion-final.json:14>) — `Revisión manual Chrome: checkout a 320, 360, 768, 1024 y 1440 px, sin desbordamiento horizontal`

### HU-041 — Validación de datos en proceso de checkout

Excel: Historias de Usuario, I42 y M42. Épica: EP-14: Proceso de Checkout.

- CA1: **NO ACREDITADO**. No se probó dirección vacía ni el conjunto completo de obligatorios del checkout.
- CA2: **NO ACREDITADO**. Los errores de checkout/fecha se muestran en alerta general.
- CA3: **CUMPLIDO Y PROBADO**. Fecha inválida, carrito vacío, stock insuficiente y cupón vencido bloquean la compra.
  - [Frontend/src/components/Carrito.test.jsx:200](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:200>) — `rechaza una entrega programada sin fecha válida: %s`
  - [Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:45](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:45>) — `checkoutRechazaCarritoVacio`
  - [Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:51](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:51>) — `checkoutRechazaStockInsuficiente`
  - [Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:100](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:100>) — `checkoutRevalidaCuponAntesDeModificarStockYGuardar`

### HU-042 — Consulta y seguimiento de proceso de checkout

Excel: Historias de Usuario, I43 y M43. Épica: EP-14: Proceso de Checkout.

- CA1: **NO ACREDITADO**. Checkout no presenta listado de pedidos más reciente primero.
- CA2: **NO ACREDITADO**. No hay búsqueda/filtro dentro de checkout.
- CA3: **CUMPLIDO Y PROBADO**. Confirmar reemplaza el formulario por el resultado de compra sin recargar.
  - [Frontend/src/components/Carrito.test.jsx:42](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:42>) — `confirma una compra sin realizar llamadas de red desde el componente`

### HU-043 — Acceso principal a selección de dirección de envío

Excel: Historias de Usuario, I44 y M44. Épica: EP-15: Selección de Dirección de Envío.

- CA1: **NO ACREDITADO**. No se prueba llegar desde navegación principal a la selección de dirección.
- CA2: **CUMPLIDO Y PROBADO**. La dirección se autocompleta y puede modificarse antes de confirmar.
  - [Frontend/src/components/Carrito.test.jsx:160](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:160>) — `muestra los datos reales de la cuenta y la dirección autocompletada`
  - [Frontend/src/components/Carrito.test.jsx:42](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:42>) — `confirma una compra sin realizar llamadas de red desde el componente`
- CA3: **CUMPLIDO Y PROBADO**. El campo Dirección de entrega aparece correctamente en checkout móvil y tablet revisados.
  - [C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto/verificacion-final.json:14](<C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto/verificacion-final.json:14>) — `Revisión manual Chrome: checkout a 320, 360, 768, 1024 y 1440 px, sin desbordamiento horizontal`

### HU-044 — Validación de datos en selección de dirección de envío

Excel: Historias de Usuario, I45 y M45. Épica: EP-15: Selección de Dirección de Envío.

- CA1: **NO ACREDITADO**. required está implementado en la dirección, pero no se probó dirección vacía.
- CA2: **NO ACREDITADO**. No hay mensaje específico junto al campo de dirección.
- CA3: **NO ACREDITADO**. No hay prueba negativa de dirección inválida que impida enviar el pedido.

### HU-045 — Consulta y seguimiento de selección de dirección de envío

Excel: Historias de Usuario, I46 y M46. Épica: EP-15: Selección de Dirección de Envío.

- CA1: **NO ACREDITADO**. Existe un único campo de dirección; no listado cronológico de direcciones.
- CA2: **NO ACREDITADO**. No existe buscador/filtro de direcciones de envío.
- CA3: **CUMPLIDO Y PROBADO**. La dirección editada se utiliza al confirmar sin recargar.
  - [Frontend/src/components/Carrito.test.jsx:42](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:42>) — `confirma una compra sin realizar llamadas de red desde el componente`

### HU-046 — Acceso principal a selección de fecha de entrega preferida

Excel: Historias de Usuario, I47 y M47. Épica: EP-16: Selección de Fecha de Entrega Preferida.

- CA1: **NO ACREDITADO**. No se prueba el acceso desde navegación principal a programación de fecha.
- CA2: **CUMPLIDO Y PROBADO**. Seleccionar modalidad programada muestra fecha requerida y envía la fecha elegida.
  - [Frontend/src/components/Carrito.test.jsx:171](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:171>) — `envía la fecha elegida al programar la entrega`
- CA3: **NO ACREDITADO**. La captura tablet muestra fecha programada; no se acredita ese estado específico en móvil/escritorio.

### HU-047 — Validación de datos en selección de fecha de entrega preferida

Excel: Historias de Usuario, I48 y M48. Épica: EP-16: Selección de Fecha de Entrega Preferida.

- CA1: **CUMPLIDO Y PROBADO**. La fecha es obligatoria al programar; fecha vacía se rechaza sin llamar a compra.
  - [Frontend/src/components/Carrito.test.jsx:171](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:171>) — `envía la fecha elegida al programar la entrega`
  - [Frontend/src/components/Carrito.test.jsx:200](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:200>) — `rechaza una entrega programada sin fecha válida: %s`
- CA2: **NO ACREDITADO**. El mensaje de fecha inválida está en alerta general del checkout, separado del campo.
- CA3: **CUMPLIDO Y PROBADO**. Fecha vacía/pasada bloquea la confirmación; se aplica mínimo con fecha local.
  - [Frontend/src/components/Carrito.test.jsx:200](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:200>) — `rechaza una entrega programada sin fecha válida: %s`
  - [Frontend/src/components/Carrito.test.jsx:212](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:212>) — `usa el día local al programar una entrega de noche`

### HU-048 — Consulta y seguimiento de selección de fecha de entrega preferida

Excel: Historias de Usuario, I49 y M49. Épica: EP-16: Selección de Fecha de Entrega Preferida.

- CA1: **NO ACREDITADO**. No existe historial cronológico de fechas de entrega seleccionadas.
- CA2: **NO ACREDITADO**. El selector de fecha no es un buscador/filtro de información listada.
- CA3: **CUMPLIDO Y PROBADO**. Cambiar modalidad borra la fecha de inmediato y evita enviarla sin recargar.
  - [Frontend/src/components/Carrito.test.jsx:184](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:184>) — `limpia la fecha al cambiar a próxima disponibilidad y no la envía`

### HU-049 — Acceso principal a métodos de pago

Excel: Historias de Usuario, I50 y M50. Épica: EP-17: Métodos de Pago.

- CA1: **NO ACREDITADO**. No existe selección de métodos de pago ni integración/prueba de pago bancario.
- CA2: **NO ACREDITADO**. No existe selección de métodos de pago ni integración/prueba de pago bancario.
- CA3: **NO ACREDITADO**. No existe selección de métodos de pago ni integración/prueba de pago bancario.

### HU-050 — Validación de datos en métodos de pago

Excel: Historias de Usuario, I51 y M51. Épica: EP-17: Métodos de Pago.

- CA1: **NO ACREDITADO**. No existe selección de métodos de pago ni integración/prueba de pago bancario.
- CA2: **NO ACREDITADO**. No existe selección de métodos de pago ni integración/prueba de pago bancario.
- CA3: **NO ACREDITADO**. No existe selección de métodos de pago ni integración/prueba de pago bancario.

### HU-051 — Consulta y seguimiento de métodos de pago

Excel: Historias de Usuario, I52 y M52. Épica: EP-17: Métodos de Pago.

- CA1: **NO ACREDITADO**. No existe selección de métodos de pago ni integración/prueba de pago bancario.
- CA2: **NO ACREDITADO**. No existe selección de métodos de pago ni integración/prueba de pago bancario.
- CA3: **NO ACREDITADO**. No existe selección de métodos de pago ni integración/prueba de pago bancario.

### HU-052 — Acceso principal a confirmación y resumen de pedido

Excel: Historias de Usuario, I53 y M53. Épica: EP-18: Confirmación y Resumen de Pedido.

- CA1: **NO ACREDITADO**. Se prueba confirmación dentro de Carrito montado, sin recorrido desde navegación principal.
- CA2: **CUMPLIDO Y PROBADO**. Se muestra confirmación exitosa y está implementado el resumen con número, dirección y total.
  - [Frontend/src/components/Carrito.test.jsx:42](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:42>) — `confirma una compra sin realizar llamadas de red desde el componente`
  - [C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto/verificacion-final.json:20](<C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto/verificacion-final.json:20>) — `Compra UI con cupón QA10: subtotal 15990, descuento 1599, total 14391, pedido 2`
- CA3: **NO ACREDITADO**. La revisión de checkout no acredita la pantalla de confirmación en todos los tamaños.

### HU-053 — Validación de datos en confirmación y resumen de pedido

Excel: Historias de Usuario, I54 y M54. Épica: EP-18: Confirmación y Resumen de Pedido.

- CA1: **NO ACREDITADO**. La pantalla de confirmación no tiene formulario de obligatorios probado.
- CA2: **NO ACREDITADO**. El fallo se presenta como alerta general del checkout.
- CA3: **CUMPLIDO Y PROBADO**. Si el backend rechaza un cupón vencido no se muestra confirmación de compra exitosa.
  - [Frontend/src/components/Carrito.test.jsx:144](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:144>) — `envía el código canónico y muestra un vencimiento detectado al confirmar`

### HU-054 — Consulta y seguimiento de confirmación y resumen de pedido

Excel: Historias de Usuario, I55 y M55. Épica: EP-18: Confirmación y Resumen de Pedido.

- CA1: **NO ACREDITADO**. El resumen individual no ofrece lista de pedidos más reciente primero.
- CA2: **NO ACREDITADO**. No hay búsqueda/filtro en la confirmación y resumen.
- CA3: **CUMPLIDO Y PROBADO**. La confirmación exitosa aparece inmediatamente tras resolver el pedido sin recargar.
  - [Frontend/src/components/Carrito.test.jsx:42](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Carrito.test.jsx:42>) — `confirma una compra sin realizar llamadas de red desde el componente`

### HU-055 — Acceso principal a historial de pedidos del cliente

Excel: Historias de Usuario, I56 y M56. Épica: EP-19: Historial de Pedidos del Cliente.

- CA1: **NO ACREDITADO**. No existe prueba de click en Mis pedidos desde la navegación principal.
- CA2: **CUMPLIDO Y PROBADO**. El historial permite abrir el detalle y comprobar sus productos; vacío también probado.
  - [Frontend/src/components/orders/OrderList.test.jsx:6](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/orders/OrderList.test.jsx:6>) — `permite abrir el detalle de un pedido`
  - [Frontend/src/components/orders/OrderList.test.jsx:23](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/orders/OrderList.test.jsx:23>) — `muestra el estado vacío`
- CA3: **NO ACREDITADO**. Sin prueba responsive específica de esta vista; Vitest no acredita móvil/escritorio.

### HU-056 — Validación de datos en historial de pedidos del cliente

Excel: Historias de Usuario, I57 y M57. Épica: EP-19: Historial de Pedidos del Cliente.

- CA1: **NO ACREDITADO**. No hay campos obligatorios de historial cubiertos por prueba de vacíos.
- CA2: **NO ACREDITADO**. No hay errores de campo asociados en historial.
- CA3: **NO ACREDITADO**. No hay prueba negativa de un formulario de historial inválido que bloquee continuar.

### HU-057 — Consulta y seguimiento de historial de pedidos del cliente

Excel: Historias de Usuario, I58 y M58. Épica: EP-19: Historial de Pedidos del Cliente.

- CA1: **NO ACREDITADO**. La consulta usa orden descendente en backend, pero no se prueba con pedidos de distintas fechas.
- CA2: **NO ACREDITADO**. No existe búsqueda/filtro en el historial del cliente.
- CA3: **NO ACREDITADO**. El historial carga una vez; abrir el detalle no prueba actualización de datos tras un cambio.

### HU-058 — Acceso principal a seguimiento de estado del pedido

Excel: Historias de Usuario, I59 y M59. Épica: EP-20: Seguimiento de Estado del Pedido.

- CA1: **NO ACREDITADO**. No se prueba acceso desde navegación principal al seguimiento del pedido.
- CA2: **NO ACREDITADO**. La prueba contiene un pedido ENVIADO pero no comprueba que su estado se muestre en pantalla.
- CA3: **NO ACREDITADO**. Sin prueba responsive específica de esta vista; Vitest no acredita móvil/escritorio.

### HU-059 — Validación de datos en seguimiento de estado del pedido

Excel: Historias de Usuario, I60 y M60. Épica: EP-20: Seguimiento de Estado del Pedido.

- CA1: **NO ACREDITADO**. No se prueba el conjunto de obligatorios de seguimiento/estado.
- CA2: **NO ACREDITADO**. El cliente no dispone de errores de estado asociados a un campo.
- CA3: **CUMPLIDO Y PROBADO**. Un estado inexistente impide actualizar el pedido.
  - [Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:86](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/PedidoServiceTest.java:86>) — `cambiarEstadoRechazaEstadoInvalido`

### HU-060 — Consulta y seguimiento de seguimiento de estado del pedido

Excel: Historias de Usuario, I61 y M61. Épica: EP-20: Seguimiento de Estado del Pedido.

- CA1: **NO ACREDITADO**. El orden descendente está implementado en backend, sin prueba cronológica de varios pedidos.
- CA2: **NO ACREDITADO**. El seguimiento del cliente no dispone de filtro por estado.
- CA3: **NO ACREDITADO**. El backend actualiza estado, pero no hay refresco de datos ni prueba de actualización en la vista del cliente.

### HU-061 — Acceso principal a lista de deseos (wishlist)

Excel: Historias de Usuario, I62 y M62. Épica: EP-21: Lista de Deseos (Wishlist).

- CA1: **NO ACREDITADO**. No se encontró implementación ni pruebas de lista de deseos.
- CA2: **NO ACREDITADO**. No se encontró implementación ni pruebas de lista de deseos.
- CA3: **NO ACREDITADO**. No se encontró implementación ni pruebas de lista de deseos.

### HU-062 — Validación de datos en lista de deseos (wishlist)

Excel: Historias de Usuario, I63 y M63. Épica: EP-21: Lista de Deseos (Wishlist).

- CA1: **NO ACREDITADO**. No se encontró implementación ni pruebas de lista de deseos.
- CA2: **NO ACREDITADO**. No se encontró implementación ni pruebas de lista de deseos.
- CA3: **NO ACREDITADO**. No se encontró implementación ni pruebas de lista de deseos.

### HU-063 — Consulta y seguimiento de lista de deseos (wishlist)

Excel: Historias de Usuario, I64 y M64. Épica: EP-21: Lista de Deseos (Wishlist).

- CA1: **NO ACREDITADO**. No se encontró implementación ni pruebas de lista de deseos.
- CA2: **NO ACREDITADO**. No se encontró implementación ni pruebas de lista de deseos.
- CA3: **NO ACREDITADO**. No se encontró implementación ni pruebas de lista de deseos.

### HU-064 — Acceso principal a valoraciones y reseñas de productos

Excel: Historias de Usuario, I65 y M65. Épica: EP-22: Valoraciones y Reseñas de Productos.

- CA1: **NO ACREDITADO**. No se encontró implementación ni pruebas de valoraciones/reseñas.
- CA2: **NO ACREDITADO**. No se encontró implementación ni pruebas de valoraciones/reseñas.
- CA3: **NO ACREDITADO**. No se encontró implementación ni pruebas de valoraciones/reseñas.

### HU-065 — Validación de datos en valoraciones y reseñas de productos

Excel: Historias de Usuario, I66 y M66. Épica: EP-22: Valoraciones y Reseñas de Productos.

- CA1: **NO ACREDITADO**. No se encontró implementación ni pruebas de valoraciones/reseñas.
- CA2: **NO ACREDITADO**. No se encontró implementación ni pruebas de valoraciones/reseñas.
- CA3: **NO ACREDITADO**. No se encontró implementación ni pruebas de valoraciones/reseñas.

### HU-066 — Consulta y seguimiento de valoraciones y reseñas de productos

Excel: Historias de Usuario, I67 y M67. Épica: EP-22: Valoraciones y Reseñas de Productos.

- CA1: **NO ACREDITADO**. No se encontró implementación ni pruebas de valoraciones/reseñas.
- CA2: **NO ACREDITADO**. No se encontró implementación ni pruebas de valoraciones/reseñas.
- CA3: **NO ACREDITADO**. No se encontró implementación ni pruebas de valoraciones/reseñas.

### HU-067 — Acceso principal a comparador de productos

Excel: Historias de Usuario, I68 y M68. Épica: EP-23: Comparador de Productos.

- CA1: **NO ACREDITADO**. No se encontró implementación ni pruebas de comparador de productos.
- CA2: **NO ACREDITADO**. No se encontró implementación ni pruebas de comparador de productos.
- CA3: **NO ACREDITADO**. No se encontró implementación ni pruebas de comparador de productos.

### HU-068 — Validación de datos en comparador de productos

Excel: Historias de Usuario, I69 y M69. Épica: EP-23: Comparador de Productos.

- CA1: **NO ACREDITADO**. No se encontró implementación ni pruebas de comparador de productos.
- CA2: **NO ACREDITADO**. No se encontró implementación ni pruebas de comparador de productos.
- CA3: **NO ACREDITADO**. No se encontró implementación ni pruebas de comparador de productos.

### HU-069 — Consulta y seguimiento de comparador de productos

Excel: Historias de Usuario, I70 y M70. Épica: EP-23: Comparador de Productos.

- CA1: **NO ACREDITADO**. No se encontró implementación ni pruebas de comparador de productos.
- CA2: **NO ACREDITADO**. No se encontró implementación ni pruebas de comparador de productos.
- CA3: **NO ACREDITADO**. No se encontró implementación ni pruebas de comparador de productos.

### HU-070 — Acceso principal a devoluciones y reembolsos

Excel: Historias de Usuario, I71 y M71. Épica: EP-24: Devoluciones y Reembolsos.

- CA1: **NO ACREDITADO**. No se encontró implementación ni pruebas de devoluciones/reembolsos.
- CA2: **NO ACREDITADO**. No se encontró implementación ni pruebas de devoluciones/reembolsos.
- CA3: **NO ACREDITADO**. No se encontró implementación ni pruebas de devoluciones/reembolsos.

### HU-071 — Validación de datos en devoluciones y reembolsos

Excel: Historias de Usuario, I72 y M72. Épica: EP-24: Devoluciones y Reembolsos.

- CA1: **NO ACREDITADO**. No se encontró implementación ni pruebas de devoluciones/reembolsos.
- CA2: **NO ACREDITADO**. No se encontró implementación ni pruebas de devoluciones/reembolsos.
- CA3: **NO ACREDITADO**. No se encontró implementación ni pruebas de devoluciones/reembolsos.

### HU-072 — Consulta y seguimiento de devoluciones y reembolsos

Excel: Historias de Usuario, I73 y M73. Épica: EP-24: Devoluciones y Reembolsos.

- CA1: **NO ACREDITADO**. No se encontró implementación ni pruebas de devoluciones/reembolsos.
- CA2: **NO ACREDITADO**. No se encontró implementación ni pruebas de devoluciones/reembolsos.
- CA3: **NO ACREDITADO**. No se encontró implementación ni pruebas de devoluciones/reembolsos.

### HU-073 — Acceso principal a página nosotros

Excel: Historias de Usuario, I74 y M74. Épica: EP-25: Página Nosotros.

- CA1: **CUMPLIDO Y PROBADO**. Se abre Nosotros desde su enlace de navegación principal.
  - [Frontend/src/App.test.jsx:26](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:26>) — `navega por nosotros, categorías y detalle sin perder el filtro al volver`
- CA2: **NO ACREDITADO**. La prueba solo comprueba el encabezado Nosotros, no el contenido explicativo completo.
- CA3: **NO ACREDITADO**. No se revisó responsive la página Nosotros.

### HU-074 — Validación de datos en página nosotros

Excel: Historias de Usuario, I75 y M75. Épica: EP-25: Página Nosotros.

- CA1: **NO ACREDITADO**. Nosotros es contenido estático y no tiene formulario de obligatorios probado.
- CA2: **NO ACREDITADO**. No existen campos con errores de validación en Nosotros.
- CA3: **NO ACREDITADO**. No hay proceso de datos de Nosotros cuyo bloqueo esté probado.

### HU-075 — Consulta y seguimiento de página nosotros

Excel: Historias de Usuario, I76 y M76. Épica: EP-25: Página Nosotros.

- CA1: **NO ACREDITADO**. Nosotros no presenta información en un listado cronológico.
- CA2: **NO ACREDITADO**. No existe búsqueda/filtro en Nosotros.
- CA3: **NO ACREDITADO**. No se comprueban cambios de contenido de Nosotros sin recargar.

### HU-076 — Acceso principal a blog de la tienda

Excel: Historias de Usuario, I77 y M77. Épica: EP-26: Blog de la Tienda.

- CA1: **CUMPLIDO Y PROBADO**. El enlace Noticias abre el listado de artículos.
  - [Frontend/src/App.test.jsx:48](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:48>) — `abre una noticia real y permite regresar al listado`
- CA2: **CUMPLIDO Y PROBADO**. Se muestran título, descripción y estados de carga/vacío del blog.
  - [Frontend/src/components/blog/BlogList.test.jsx:6](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/blog/BlogList.test.jsx:6>) — `muestra noticias publicadas`
  - [Frontend/src/components/blog/BlogList.test.jsx:13](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/blog/BlogList.test.jsx:13>) — `muestra sus estados de carga y vacío`
- CA3: **NO ACREDITADO**. El blog no fue incluido en la revisión responsive.

### HU-077 — Validación de datos en blog de la tienda

Excel: Historias de Usuario, I78 y M78. Épica: EP-26: Blog de la Tienda.

- CA1: **NO ACREDITADO**. El blog público no tiene campos obligatorios de entrada probados.
- CA2: **NO ACREDITADO**. No hay mensajes específicos junto a campos del blog público.
- CA3: **NO ACREDITADO**. No se prueba un bloqueo por datos inválidos del blog público.

### HU-078 — Consulta y seguimiento de blog de la tienda

Excel: Historias de Usuario, I79 y M79. Épica: EP-26: Blog de la Tienda.

- CA1: **NO ACREDITADO**. El repositorio ordena noticias por fecha, pero no hay prueba que contraste varias fechas.
- CA2: **NO ACREDITADO**. No existe buscador/filtro de noticias del blog público.
- CA3: **NO ACREDITADO**. El listado se carga al montar; no hay prueba de reflejar altas/ediciones publicadas sin recargar.

### HU-079 — Acceso principal a detalle de artículo de blog

Excel: Historias de Usuario, I80 y M80. Épica: EP-27: Detalle de Artículo de Blog.

- CA1: **CUMPLIDO Y PROBADO**. Desde Noticias se abre el artículo y se regresa al listado.
  - [Frontend/src/App.test.jsx:48](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:48>) — `abre una noticia real y permite regresar al listado`
- CA2: **CUMPLIDO Y PROBADO**. Se comprueban encabezado y contenido del artículo abierto.
  - [Frontend/src/App.test.jsx:48](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:48>) — `abre una noticia real y permite regresar al listado`
- CA3: **NO ACREDITADO**. No se revisó responsive el detalle de artículo.

### HU-080 — Validación de datos en detalle de artículo de blog

Excel: Historias de Usuario, I81 y M81. Épica: EP-27: Detalle de Artículo de Blog.

- CA1: **NO ACREDITADO**. El detalle del artículo no contiene formulario de obligatorios probado.
- CA2: **NO ACREDITADO**. No hay campos con mensajes específicos de error en el detalle.
- CA3: **NO ACREDITADO**. No hay bloqueo por datos inválidos del detalle de artículo probado.

### HU-081 — Consulta y seguimiento de detalle de artículo de blog

Excel: Historias de Usuario, I82 y M82. Épica: EP-27: Detalle de Artículo de Blog.

- CA1: **NO ACREDITADO**. El detalle muestra un artículo individual, no una lista más reciente primero.
- CA2: **NO ACREDITADO**. No existe búsqueda/filtro dentro del detalle de artículo.
- CA3: **NO ACREDITADO**. Abrir y regresar no prueba reflejar cambios del artículo sin recargar.

### HU-082 — Acceso principal a formulario de contacto

Excel: Historias de Usuario, I83 y M83. Épica: EP-28: Formulario de Contacto.

- CA1: **NO ACREDITADO**. El enlace Contacto está implementado, pero no se prueba abrirlo desde navegación principal.
- CA2: **CUMPLIDO Y PROBADO**. Se comprueban campos etiquetados, envío y confirmación visible del mensaje.
  - [Frontend/src/components/contact/ContactoForm.test.jsx:17](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/contact/ContactoForm.test.jsx:17>) — `envía el mensaje y muestra confirmación sin depender del backend`
- CA3: **NO ACREDITADO**. El formulario de contacto no fue incluido en la revisión responsive.

### HU-083 — Validación de datos en formulario de contacto

Excel: Historias de Usuario, I84 y M84. Épica: EP-28: Formulario de Contacto.

- CA1: **NO ACREDITADO**. No hay prueba de vacíos para nombre, correo y mensaje como conjunto de obligatorios.
- CA2: **NO ACREDITADO**. Los errores del servicio se muestran como alerta general, sin asociarlos al campo.
- CA3: **CUMPLIDO Y PROBADO**. Un correo de dominio no permitido impide guardar el mensaje.
  - [Backend/src/test/java/com/huerto/hogar/ContactoServiceTest.java:35](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Backend/src/test/java/com/huerto/hogar/ContactoServiceTest.java:35>) — `saveRechazaCorreoNoPermitido`

### HU-084 — Consulta y seguimiento de formulario de contacto

Excel: Historias de Usuario, I85 y M85. Épica: EP-28: Formulario de Contacto.

- CA1: **NO ACREDITADO**. El formulario del cliente no muestra historial cronológico de mensajes.
- CA2: **NO ACREDITADO**. El cliente no dispone de búsqueda/filtro de mensajes de contacto.
- CA3: **CUMPLIDO Y PROBADO**. Después de enviar, la confirmación y el callback se actualizan sin recargar.
  - [Frontend/src/components/contact/ContactoForm.test.jsx:17](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/contact/ContactoForm.test.jsx:17>) — `envía el mensaje y muestra confirmación sin depender del backend`

### HU-085 — Acceso principal a suscripción a newsletter

Excel: Historias de Usuario, I86 y M86. Épica: EP-29: Suscripción a Newsletter.

- CA1: **NO ACREDITADO**. No se encontró implementación ni pruebas de suscripción a newsletter.
- CA2: **NO ACREDITADO**. No se encontró implementación ni pruebas de suscripción a newsletter.
- CA3: **NO ACREDITADO**. No se encontró implementación ni pruebas de suscripción a newsletter.

### HU-086 — Validación de datos en suscripción a newsletter

Excel: Historias de Usuario, I87 y M87. Épica: EP-29: Suscripción a Newsletter.

- CA1: **NO ACREDITADO**. No se encontró implementación ni pruebas de suscripción a newsletter.
- CA2: **NO ACREDITADO**. No se encontró implementación ni pruebas de suscripción a newsletter.
- CA3: **NO ACREDITADO**. No se encontró implementación ni pruebas de suscripción a newsletter.

### HU-087 — Consulta y seguimiento de suscripción a newsletter

Excel: Historias de Usuario, I88 y M88. Épica: EP-29: Suscripción a Newsletter.

- CA1: **NO ACREDITADO**. No se encontró implementación ni pruebas de suscripción a newsletter.
- CA2: **NO ACREDITADO**. No se encontró implementación ni pruebas de suscripción a newsletter.
- CA3: **NO ACREDITADO**. No se encontró implementación ni pruebas de suscripción a newsletter.

### HU-088 — Acceso principal a notificaciones al cliente

Excel: Historias de Usuario, I89 y M89. Épica: EP-30: Notificaciones al Cliente.

- CA1: **NO ACREDITADO**. No se encontró implementación ni pruebas de centro/notificaciones al cliente.
- CA2: **NO ACREDITADO**. No se encontró implementación ni pruebas de centro/notificaciones al cliente.
- CA3: **NO ACREDITADO**. No se encontró implementación ni pruebas de centro/notificaciones al cliente.

### HU-089 — Validación de datos en notificaciones al cliente

Excel: Historias de Usuario, I90 y M90. Épica: EP-30: Notificaciones al Cliente.

- CA1: **NO ACREDITADO**. No se encontró implementación ni pruebas de centro/notificaciones al cliente.
- CA2: **NO ACREDITADO**. No se encontró implementación ni pruebas de centro/notificaciones al cliente.
- CA3: **NO ACREDITADO**. No se encontró implementación ni pruebas de centro/notificaciones al cliente.

### HU-090 — Consulta y seguimiento de notificaciones al cliente

Excel: Historias de Usuario, I91 y M91. Épica: EP-30: Notificaciones al Cliente.

- CA1: **NO ACREDITADO**. No se encontró implementación ni pruebas de centro/notificaciones al cliente.
- CA2: **NO ACREDITADO**. No se encontró implementación ni pruebas de centro/notificaciones al cliente.
- CA3: **NO ACREDITADO**. No se encontró implementación ni pruebas de centro/notificaciones al cliente.

### HU-091 — Acceso principal a dashboard del administrador

Excel: Historias de Usuario, I92 y M92. Épica: EP-31: Dashboard del Administrador.

- CA1: **CUMPLIDO Y PROBADO**. La prueba de App entra por Panel principal y usa el menú administrativo; AdminPage prueba los accesos Tienda y Cerrar sesión. La funcionalidad es accesible desde navegación probada.
  - [Frontend/src/App.test.jsx:110](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:110>) — `actualiza las categorías disponibles al crear y eliminar una categoría`
  - [Frontend/src/pages/AdminPage.test.jsx:9](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/pages/AdminPage.test.jsx:9>) — `abre el dashboard con indicadores del backend y accesos a tienda y sesión`
- CA2: **CUMPLIDO Y PROBADO**. Se verifican en pantalla los tres indicadores devueltos por el servicio: compras 17, productos 23 y usuarios 9; el componente los presenta con etiquetas identificables.
  - [Frontend/src/pages/AdminPage.test.jsx:9](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/pages/AdminPage.test.jsx:9>) — `abre el dashboard con indicadores del backend y accesos a tienda y sesión`
- CA3: **CUMPLIDO Y PROBADO**. La vista dashboard se revisó en Chrome en móvil y escritorio sin desbordamiento horizontal.
  - [C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto/verificacion-final.json:12](<C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto/verificacion-final.json:12>) — `Revisión manual Chrome de dashboard: móvil y escritorio`

### HU-092 — Validación de datos en dashboard del administrador

Excel: Historias de Usuario, I93 y M93. Épica: EP-31: Dashboard del Administrador.

- CA1: **NO ACREDITADO**. Las pruebas ejercitan algunas reglas o un campo obligatorio, pero no todos los campos obligatorios vacíos de este formulario. La implementación de required/@NotBlank no es por sí sola una prueba completa.
- CA2: **NO ACREDITADO**. No hay prueba de mensaje específico junto al campo con problema. Las excepciones de servicio, respuestas HTTP y alertas generales no acreditan la ubicación exigida.
- CA3: **NO ACREDITADO**. Hay rechazos de algunas entradas o reglas, pero no una prueba del formulario/proceso que demuestre que impide continuar hasta corregir el conjunto de errores. Cobertura parcial no se marca como criterio completo.

### HU-093 — Consulta y seguimiento de dashboard del administrador

Excel: Historias de Usuario, I94 y M94. Épica: EP-31: Dashboard del Administrador.

- CA1: **NO ACREDITADO**. No existe prueba que liste al menos dos registros con fechas distintas y compruebe más reciente primero. El nombre de un método OrderBy o listar un único registro no acredita el orden.
- CA2: **NO ACREDITADO**. No hay opción de búsqueda/filtro de esta consulta ejercitada por una prueba de interfaz. Una ruta de consulta backend, selector para editar un estado o navegación entre secciones no prueba un filtro del listado.
- CA3: **NO ACREDITADO**. No se verificó un cambio de datos visible en esta consulta sin recargar la página. Un callback mock, un cambio de filtro, abrir/cerrar detalle o una actualización de servicio no acredita por sí solo ese comportamiento de UI.

### HU-094 — Acceso principal a gestión de productos (crud)

Excel: Historias de Usuario, I95 y M95. Épica: EP-32: Gestión de Productos (CRUD).

- CA1: **CUMPLIDO Y PROBADO**. La prueba navega desde Panel principal a Productos y verifica que aparece el formulario con las categorías disponibles.
  - [Frontend/src/App.test.jsx:110](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:110>) — `actualiza las categorías disponibles al crear y eliminar una categoría`
- CA2: **CUMPLIDO Y PROBADO**. Las pruebas ejercitan controles identificados de edición, cargan los datos existentes y verifican los nombres de los productos mostrados en el inventario.
  - [Frontend/src/components/MainPage.test.jsx:9](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/MainPage.test.jsx:9>) — `editar producto persiste sus campos y actualiza el inventario`
  - [Frontend/src/components/MainPage.test.jsx:20](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/MainPage.test.jsx:20>) — `filtra el inventario por stock crítico`
- CA3: **CUMPLIDO Y PROBADO**. La vista productos de administración se revisó en Chrome en móvil y escritorio sin desbordamiento horizontal.
  - [C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto/verificacion-final.json:15](<C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto/verificacion-final.json:15>) — `Revisión manual Chrome de productos de administración: móvil y escritorio`

### HU-095 — Validación de datos en gestión de productos (crud)

Excel: Historias de Usuario, I96 y M96. Épica: EP-32: Gestión de Productos (CRUD).

- CA1: **NO ACREDITADO**. Las pruebas ejercitan algunas reglas o un campo obligatorio, pero no todos los campos obligatorios vacíos de este formulario. La implementación de required/@NotBlank no es por sí sola una prueba completa.
- CA2: **NO ACREDITADO**. No hay prueba de mensaje específico junto al campo con problema. Las excepciones de servicio, respuestas HTTP y alertas generales no acreditan la ubicación exigida.
- CA3: **NO ACREDITADO**. Hay rechazos de algunas entradas o reglas, pero no una prueba del formulario/proceso que demuestre que impide continuar hasta corregir el conjunto de errores. Cobertura parcial no se marca como criterio completo.

### HU-096 — Consulta y seguimiento de gestión de productos (crud)

Excel: Historias de Usuario, I97 y M97. Épica: EP-32: Gestión de Productos (CRUD).

- CA1: **NO ACREDITADO**. No existe prueba que liste al menos dos registros con fechas distintas y compruebe más reciente primero. El nombre de un método OrderBy o listar un único registro no acredita el orden.
- CA2: **CUMPLIDO Y PROBADO**. En el inventario de productos existe y se prueba un filtro mediante Ver stock crítico, que excluye los productos que no cumplen el umbral.
  - [Frontend/src/components/MainPage.test.jsx:20](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/MainPage.test.jsx:20>) — `filtra el inventario por stock crítico`
- CA3: **NO ACREDITADO**. No se verificó un cambio de datos visible en esta consulta sin recargar la página. Un callback mock, un cambio de filtro, abrir/cerrar detalle o una actualización de servicio no acredita por sí solo ese comportamiento de UI.

### HU-097 — Acceso principal a gestión de stock e inventario

Excel: Historias de Usuario, I98 y M98. Épica: EP-33: Gestión de Stock e Inventario.

- CA1: **CUMPLIDO Y PROBADO**. El inventario está en la misma vista Productos alcanzada desde el menú en App.test; su control de stock crítico se acciona en MainPage.test.
  - [Frontend/src/App.test.jsx:110](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:110>) — `actualiza las categorías disponibles al crear y eliminar una categoría`
  - [Frontend/src/components/MainPage.test.jsx:20](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/MainPage.test.jsx:20>) — `filtra el inventario por stock crítico`
- CA2: **CUMPLIDO Y PROBADO**. El inventario y la edición muestran productos y stock: se verifica el stock cargado 2 en el envío de edición y la visualización del producto que satisface el umbral crítico.
  - [Frontend/src/components/MainPage.test.jsx:9](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/MainPage.test.jsx:9>) — `editar producto persiste sus campos y actualiza el inventario`
  - [Frontend/src/components/MainPage.test.jsx:20](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/MainPage.test.jsx:20>) — `filtra el inventario por stock crítico`
- CA3: **NO ACREDITADO**. No hay pruebas ejecutadas de esta funcionalidad en ambos tamaños móvil y escritorio; clases Bootstrap o una prueba de componente no acreditan responsive.

### HU-098 — Validación de datos en gestión de stock e inventario

Excel: Historias de Usuario, I99 y M99. Épica: EP-33: Gestión de Stock e Inventario.

- CA1: **NO ACREDITADO**. Las pruebas ejercitan algunas reglas o un campo obligatorio, pero no todos los campos obligatorios vacíos de este formulario. La implementación de required/@NotBlank no es por sí sola una prueba completa.
- CA2: **NO ACREDITADO**. No hay prueba de mensaje específico junto al campo con problema. Las excepciones de servicio, respuestas HTTP y alertas generales no acreditan la ubicación exigida.
- CA3: **NO ACREDITADO**. Hay rechazos de algunas entradas o reglas, pero no una prueba del formulario/proceso que demuestre que impide continuar hasta corregir el conjunto de errores. Cobertura parcial no se marca como criterio completo.

### HU-099 — Consulta y seguimiento de gestión de stock e inventario

Excel: Historias de Usuario, I100 y M100. Épica: EP-33: Gestión de Stock e Inventario.

- CA1: **NO ACREDITADO**. No existe prueba que liste al menos dos registros con fechas distintas y compruebe más reciente primero. El nombre de un método OrderBy o listar un único registro no acredita el orden.
- CA2: **CUMPLIDO Y PROBADO**. La vista de inventario tiene una opción de filtro de stock crítico probada con un producto incluido y otro excluido.
  - [Frontend/src/components/MainPage.test.jsx:20](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/MainPage.test.jsx:20>) — `filtra el inventario por stock crítico`
- CA3: **NO ACREDITADO**. No se verificó un cambio de datos visible en esta consulta sin recargar la página. Un callback mock, un cambio de filtro, abrir/cerrar detalle o una actualización de servicio no acredita por sí solo ese comportamiento de UI.

### HU-100 — Acceso principal a alertas de stock crítico

Excel: Historias de Usuario, I101 y M101. Épica: EP-34: Alertas de Stock Crítico.

- CA1: **CUMPLIDO Y PROBADO**. Productos se alcanza por el menú probado y en esa vista se acciona el botón Ver stock crítico; se verifica el resultado de la acción.
  - [Frontend/src/App.test.jsx:110](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:110>) — `actualiza las categorías disponibles al crear y eliminar una categoría`
  - [Frontend/src/components/MainPage.test.jsx:20](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/MainPage.test.jsx:20>) — `filtra el inventario por stock crítico`
- CA2: **CUMPLIDO Y PROBADO**. Al activar stock crítico se muestra Manzanas (2 <= 3) y desaparece Tomates (20 > 3); la lista presenta claramente qué producto requiere atención.
  - [Frontend/src/components/MainPage.test.jsx:20](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/MainPage.test.jsx:20>) — `filtra el inventario por stock crítico`
- CA3: **NO ACREDITADO**. No hay pruebas ejecutadas de esta funcionalidad en ambos tamaños móvil y escritorio; clases Bootstrap o una prueba de componente no acreditan responsive.

### HU-101 — Validación de datos en alertas de stock crítico

Excel: Historias de Usuario, I102 y M102. Épica: EP-34: Alertas de Stock Crítico.

- CA1: **NO ACREDITADO**. Las pruebas ejercitan algunas reglas o un campo obligatorio, pero no todos los campos obligatorios vacíos de este formulario. La implementación de required/@NotBlank no es por sí sola una prueba completa.
- CA2: **NO ACREDITADO**. No hay prueba de mensaje específico junto al campo con problema. Las excepciones de servicio, respuestas HTTP y alertas generales no acreditan la ubicación exigida.
- CA3: **NO ACREDITADO**. Hay rechazos de algunas entradas o reglas, pero no una prueba del formulario/proceso que demuestre que impide continuar hasta corregir el conjunto de errores. Cobertura parcial no se marca como criterio completo.

### HU-102 — Consulta y seguimiento de alertas de stock crítico

Excel: Historias de Usuario, I103 y M103. Épica: EP-34: Alertas de Stock Crítico.

- CA1: **NO ACREDITADO**. No existe prueba que liste al menos dos registros con fechas distintas y compruebe más reciente primero. El nombre de un método OrderBy o listar un único registro no acredita el orden.
- CA2: **CUMPLIDO Y PROBADO**. Se acciona y verifica la opción de filtrar por stock crítico; este criterio exige búsqueda O filtro, por lo que el filtro ejercitado lo satisface.
  - [Frontend/src/components/MainPage.test.jsx:20](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/MainPage.test.jsx:20>) — `filtra el inventario por stock crítico`
- CA3: **NO ACREDITADO**. No se verificó un cambio de datos visible en esta consulta sin recargar la página. Un callback mock, un cambio de filtro, abrir/cerrar detalle o una actualización de servicio no acredita por sí solo ese comportamiento de UI.

### HU-103 — Acceso principal a gestión de categorías (admin)

Excel: Historias de Usuario, I104 y M104. Épica: EP-35: Gestión de Categorías (Admin).

- CA1: **CUMPLIDO Y PROBADO**. La prueba entra a Panel principal y selecciona Categorías antes de completar el formulario administrativo.
  - [Frontend/src/App.test.jsx:110](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:110>) — `actualiza las categorías disponibles al crear y eliminar una categoría`
- CA2: **CUMPLIDO Y PROBADO**. Se acciona Editar Frutas mediante su nombre accesible, se usa el campo Nombre y se verifica Frutas frescas visible en la tabla tras guardar.
  - [Frontend/src/components/admin/CategoryManager.test.jsx:8](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/admin/CategoryManager.test.jsx:8>) — `editar categoría usa el endpoint existente y refleja el cambio`
- CA3: **NO ACREDITADO**. No hay pruebas ejecutadas de esta funcionalidad en ambos tamaños móvil y escritorio; clases Bootstrap o una prueba de componente no acreditan responsive.

### HU-104 — Validación de datos en gestión de categorías (admin)

Excel: Historias de Usuario, I105 y M105. Épica: EP-35: Gestión de Categorías (Admin).

- CA1: **NO ACREDITADO**. Las pruebas ejercitan algunas reglas o un campo obligatorio, pero no todos los campos obligatorios vacíos de este formulario. La implementación de required/@NotBlank no es por sí sola una prueba completa.
- CA2: **NO ACREDITADO**. No hay prueba de mensaje específico junto al campo con problema. Las excepciones de servicio, respuestas HTTP y alertas generales no acreditan la ubicación exigida.
- CA3: **NO ACREDITADO**. Hay rechazos de algunas entradas o reglas, pero no una prueba del formulario/proceso que demuestre que impide continuar hasta corregir el conjunto de errores. Cobertura parcial no se marca como criterio completo.

### HU-105 — Consulta y seguimiento de gestión de categorías (admin)

Excel: Historias de Usuario, I106 y M106. Épica: EP-35: Gestión de Categorías (Admin).

- CA1: **NO ACREDITADO**. No existe prueba que liste al menos dos registros con fechas distintas y compruebe más reciente primero. El nombre de un método OrderBy o listar un único registro no acredita el orden.
- CA2: **NO ACREDITADO**. No hay opción de búsqueda/filtro de esta consulta ejercitada por una prueba de interfaz. Una ruta de consulta backend, selector para editar un estado o navegación entre secciones no prueba un filtro del listado.
- CA3: **CUMPLIDO Y PROBADO**. Tras actualizar la categoría aparece el nuevo nombre en el mismo render. App.test también comprueba altas y bajas reflejadas en los selectores sin recargar la página.
  - [Frontend/src/components/admin/CategoryManager.test.jsx:8](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/admin/CategoryManager.test.jsx:8>) — `editar categoría usa el endpoint existente y refleja el cambio`
  - [Frontend/src/App.test.jsx:110](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:110>) — `actualiza las categorías disponibles al crear y eliminar una categoría`

### HU-106 — Acceso principal a gestión de usuarios (admin)

Excel: Historias de Usuario, I107 y M107. Épica: EP-36: Gestión de Usuarios (Admin).

- CA1: **NO ACREDITADO**. El menú o componente existe, pero ninguna prueba disponible navega desde la navegación principal hasta esta funcionalidad concreta. Un render directo o prueba de API/servicio no acredita navegación.
- CA2: **CUMPLIDO Y PROBADO**. Se verifican campos etiquetados de alta/edición, nombre del usuario visible y compras que pertenecen al usuario seleccionado, excluyendo las ajenas.
  - [Frontend/src/components/admin/UserManager.test.jsx:12](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/admin/UserManager.test.jsx:12>) — `permite crear un usuario administrador desde un formulario completo`
  - [Frontend/src/components/admin/UserManager.test.jsx:26](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/admin/UserManager.test.jsx:26>) — `permite editar un usuario conservando su identificador`
  - [Frontend/src/components/admin/UserManager.test.jsx:38](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/admin/UserManager.test.jsx:38>) — `el historial del usuario muestra únicamente sus compras`
- CA3: **NO ACREDITADO**. No hay pruebas ejecutadas de esta funcionalidad en ambos tamaños móvil y escritorio; clases Bootstrap o una prueba de componente no acreditan responsive.

### HU-107 — Validación de datos en gestión de usuarios (admin)

Excel: Historias de Usuario, I108 y M108. Épica: EP-36: Gestión de Usuarios (Admin).

- CA1: **NO ACREDITADO**. Las pruebas ejercitan algunas reglas o un campo obligatorio, pero no todos los campos obligatorios vacíos de este formulario. La implementación de required/@NotBlank no es por sí sola una prueba completa.
- CA2: **NO ACREDITADO**. No hay prueba de mensaje específico junto al campo con problema. Las excepciones de servicio, respuestas HTTP y alertas generales no acreditan la ubicación exigida.
- CA3: **NO ACREDITADO**. Hay rechazos de algunas entradas o reglas, pero no una prueba del formulario/proceso que demuestre que impide continuar hasta corregir el conjunto de errores. Cobertura parcial no se marca como criterio completo.

### HU-108 — Consulta y seguimiento de gestión de usuarios (admin)

Excel: Historias de Usuario, I109 y M109. Épica: EP-36: Gestión de Usuarios (Admin).

- CA1: **NO ACREDITADO**. No existe prueba que liste al menos dos registros con fechas distintas y compruebe más reciente primero. El nombre de un método OrderBy o listar un único registro no acredita el orden.
- CA2: **CUMPLIDO Y PROBADO**. La opción Historial de Ana Pérez filtra las compras por usuario: muestra Pedido #10 y excluye Pedido #20 de otro usuario.
  - [Frontend/src/components/admin/UserManager.test.jsx:38](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/admin/UserManager.test.jsx:38>) — `el historial del usuario muestra únicamente sus compras`
- CA3: **CUMPLIDO Y PROBADO**. Las respuestas de creación y edición actualizan el listado en el mismo render: aparecen Luis Pérez y Anita Pérez después de guardar, sin recarga.
  - [Frontend/src/components/admin/UserManager.test.jsx:26](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/admin/UserManager.test.jsx:26>) — `permite editar un usuario conservando su identificador`
  - [Frontend/src/components/admin/UserManager.test.jsx:12](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/admin/UserManager.test.jsx:12>) — `permite crear un usuario administrador desde un formulario completo`

### HU-109 — Acceso principal a gestión de roles de usuario (admin)

Excel: Historias de Usuario, I110 y M110. Épica: EP-37: Gestión de Roles de Usuario (Admin).

- CA1: **NO ACREDITADO**. El menú o componente existe, pero ninguna prueba disponible navega desde la navegación principal hasta esta funcionalidad concreta. Un render directo o prueba de API/servicio no acredita navegación.
- CA2: **CUMPLIDO Y PROBADO**. El selector etiquetado Rol permite elegir ADMINISTRADOR y la prueba comprueba que ese valor se envía al guardar; la información del rol es identificable en el formulario.
  - [Frontend/src/components/admin/UserManager.test.jsx:12](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/admin/UserManager.test.jsx:12>) — `permite crear un usuario administrador desde un formulario completo`
- CA3: **NO ACREDITADO**. No hay pruebas ejecutadas de esta funcionalidad en ambos tamaños móvil y escritorio; clases Bootstrap o una prueba de componente no acreditan responsive.

### HU-110 — Validación de datos en gestión de roles de usuario (admin)

Excel: Historias de Usuario, I111 y M111. Épica: EP-37: Gestión de Roles de Usuario (Admin).

- CA1: **NO ACREDITADO**. Las pruebas ejercitan algunas reglas o un campo obligatorio, pero no todos los campos obligatorios vacíos de este formulario. La implementación de required/@NotBlank no es por sí sola una prueba completa.
- CA2: **NO ACREDITADO**. No hay prueba de mensaje específico junto al campo con problema. Las excepciones de servicio, respuestas HTTP y alertas generales no acreditan la ubicación exigida.
- CA3: **NO ACREDITADO**. Hay rechazos de algunas entradas o reglas, pero no una prueba del formulario/proceso que demuestre que impide continuar hasta corregir el conjunto de errores. Cobertura parcial no se marca como criterio completo.

### HU-111 — Consulta y seguimiento de gestión de roles de usuario (admin)

Excel: Historias de Usuario, I112 y M112. Épica: EP-37: Gestión de Roles de Usuario (Admin).

- CA1: **NO ACREDITADO**. No existe prueba que liste al menos dos registros con fechas distintas y compruebe más reciente primero. El nombre de un método OrderBy o listar un único registro no acredita el orden.
- CA2: **NO ACREDITADO**. No hay opción de búsqueda/filtro de esta consulta ejercitada por una prueba de interfaz. Una ruta de consulta backend, selector para editar un estado o navegación entre secciones no prueba un filtro del listado.
- CA3: **NO ACREDITADO**. No se verificó un cambio de datos visible en esta consulta sin recargar la página. Un callback mock, un cambio de filtro, abrir/cerrar detalle o una actualización de servicio no acredita por sí solo ese comportamiento de UI.

### HU-112 — Acceso principal a gestión de pedidos (admin)

Excel: Historias de Usuario, I113 y M113. Épica: EP-38: Gestión de Pedidos (Admin).

- CA1: **NO ACREDITADO**. El menú o componente existe, pero ninguna prueba disponible navega desde la navegación principal hasta esta funcionalidad concreta. Un render directo o prueba de API/servicio no acredita navegación.
- CA2: **CUMPLIDO Y PROBADO**. La prueba abre Boleta #7 y comprueba encabezado, producto Semillas, cupón QA10 y etiqueta Descuento; también comprueba cierre de la boleta.
  - [Frontend/src/components/admin/OrderManager.test.jsx:7](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/admin/OrderManager.test.jsx:7>) — `muestra la boleta persistida con productos, descuento y total`
- CA3: **NO ACREDITADO**. No hay pruebas ejecutadas de esta funcionalidad en ambos tamaños móvil y escritorio; clases Bootstrap o una prueba de componente no acreditan responsive.

### HU-113 — Validación de datos en gestión de pedidos (admin)

Excel: Historias de Usuario, I114 y M114. Épica: EP-38: Gestión de Pedidos (Admin).

- CA1: **NO ACREDITADO**. Las pruebas ejercitan algunas reglas o un campo obligatorio, pero no todos los campos obligatorios vacíos de este formulario. La implementación de required/@NotBlank no es por sí sola una prueba completa.
- CA2: **NO ACREDITADO**. No hay prueba de mensaje específico junto al campo con problema. Las excepciones de servicio, respuestas HTTP y alertas generales no acreditan la ubicación exigida.
- CA3: **NO ACREDITADO**. Hay rechazos de algunas entradas o reglas, pero no una prueba del formulario/proceso que demuestre que impide continuar hasta corregir el conjunto de errores. Cobertura parcial no se marca como criterio completo.

### HU-114 — Consulta y seguimiento de gestión de pedidos (admin)

Excel: Historias de Usuario, I115 y M115. Épica: EP-38: Gestión de Pedidos (Admin).

- CA1: **NO ACREDITADO**. No existe prueba que liste al menos dos registros con fechas distintas y compruebe más reciente primero. El nombre de un método OrderBy o listar un único registro no acredita el orden.
- CA2: **NO ACREDITADO**. No hay opción de búsqueda/filtro de esta consulta ejercitada por una prueba de interfaz. Una ruta de consulta backend, selector para editar un estado o navegación entre secciones no prueba un filtro del listado.
- CA3: **NO ACREDITADO**. No se verificó un cambio de datos visible en esta consulta sin recargar la página. Un callback mock, un cambio de filtro, abrir/cerrar detalle o una actualización de servicio no acredita por sí solo ese comportamiento de UI.

### HU-115 — Acceso principal a reportes de ventas

Excel: Historias de Usuario, I116 y M116. Épica: EP-39: Reportes de Ventas.

- CA1: **NO ACREDITADO**. Existe ReportManager, pero no hay prueba ejecutada específica de su navegación, presentación, validaciones, orden, filtros o actualización que acredite este criterio. Los indicadores del dashboard no prueban este reporte.
- CA2: **NO ACREDITADO**. Existe ReportManager, pero no hay prueba ejecutada específica de su navegación, presentación, validaciones, orden, filtros o actualización que acredite este criterio. Los indicadores del dashboard no prueban este reporte.
- CA3: **NO ACREDITADO**. Existe ReportManager, pero no hay prueba ejecutada específica de su navegación, presentación, validaciones, orden, filtros o actualización que acredite este criterio. Los indicadores del dashboard no prueban este reporte.

### HU-116 — Validación de datos en reportes de ventas

Excel: Historias de Usuario, I117 y M117. Épica: EP-39: Reportes de Ventas.

- CA1: **NO ACREDITADO**. Existe ReportManager, pero no hay prueba ejecutada específica de su navegación, presentación, validaciones, orden, filtros o actualización que acredite este criterio. Los indicadores del dashboard no prueban este reporte.
- CA2: **NO ACREDITADO**. Existe ReportManager, pero no hay prueba ejecutada específica de su navegación, presentación, validaciones, orden, filtros o actualización que acredite este criterio. Los indicadores del dashboard no prueban este reporte.
- CA3: **NO ACREDITADO**. Existe ReportManager, pero no hay prueba ejecutada específica de su navegación, presentación, validaciones, orden, filtros o actualización que acredite este criterio. Los indicadores del dashboard no prueban este reporte.

### HU-117 — Consulta y seguimiento de reportes de ventas

Excel: Historias de Usuario, I118 y M118. Épica: EP-39: Reportes de Ventas.

- CA1: **NO ACREDITADO**. Existe ReportManager, pero no hay prueba ejecutada específica de su navegación, presentación, validaciones, orden, filtros o actualización que acredite este criterio. Los indicadores del dashboard no prueban este reporte.
- CA2: **NO ACREDITADO**. Existe ReportManager, pero no hay prueba ejecutada específica de su navegación, presentación, validaciones, orden, filtros o actualización que acredite este criterio. Los indicadores del dashboard no prueban este reporte.
- CA3: **NO ACREDITADO**. Existe ReportManager, pero no hay prueba ejecutada específica de su navegación, presentación, validaciones, orden, filtros o actualización que acredite este criterio. Los indicadores del dashboard no prueban este reporte.

### HU-118 — Acceso principal a reportes de inventario

Excel: Historias de Usuario, I119 y M119. Épica: EP-40: Reportes de Inventario.

- CA1: **NO ACREDITADO**. Existe ReportManager, pero no hay prueba ejecutada específica de su navegación, presentación, validaciones, orden, filtros o actualización que acredite este criterio. Los indicadores del dashboard no prueban este reporte.
- CA2: **NO ACREDITADO**. Existe ReportManager, pero no hay prueba ejecutada específica de su navegación, presentación, validaciones, orden, filtros o actualización que acredite este criterio. Los indicadores del dashboard no prueban este reporte.
- CA3: **NO ACREDITADO**. Existe ReportManager, pero no hay prueba ejecutada específica de su navegación, presentación, validaciones, orden, filtros o actualización que acredite este criterio. Los indicadores del dashboard no prueban este reporte.

### HU-119 — Validación de datos en reportes de inventario

Excel: Historias de Usuario, I120 y M120. Épica: EP-40: Reportes de Inventario.

- CA1: **NO ACREDITADO**. Existe ReportManager, pero no hay prueba ejecutada específica de su navegación, presentación, validaciones, orden, filtros o actualización que acredite este criterio. Los indicadores del dashboard no prueban este reporte.
- CA2: **NO ACREDITADO**. Existe ReportManager, pero no hay prueba ejecutada específica de su navegación, presentación, validaciones, orden, filtros o actualización que acredite este criterio. Los indicadores del dashboard no prueban este reporte.
- CA3: **NO ACREDITADO**. Existe ReportManager, pero no hay prueba ejecutada específica de su navegación, presentación, validaciones, orden, filtros o actualización que acredite este criterio. Los indicadores del dashboard no prueban este reporte.

### HU-120 — Consulta y seguimiento de reportes de inventario

Excel: Historias de Usuario, I121 y M121. Épica: EP-40: Reportes de Inventario.

- CA1: **NO ACREDITADO**. Existe ReportManager, pero no hay prueba ejecutada específica de su navegación, presentación, validaciones, orden, filtros o actualización que acredite este criterio. Los indicadores del dashboard no prueban este reporte.
- CA2: **NO ACREDITADO**. Existe ReportManager, pero no hay prueba ejecutada específica de su navegación, presentación, validaciones, orden, filtros o actualización que acredite este criterio. Los indicadores del dashboard no prueban este reporte.
- CA3: **NO ACREDITADO**. Existe ReportManager, pero no hay prueba ejecutada específica de su navegación, presentación, validaciones, orden, filtros o actualización que acredite este criterio. Los indicadores del dashboard no prueban este reporte.

### HU-121 — Acceso principal a gestión de proveedores

Excel: Historias de Usuario, I122 y M122. Épica: EP-41: Gestión de Proveedores.

- CA1: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA2: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA3: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.

### HU-122 — Validación de datos en gestión de proveedores

Excel: Historias de Usuario, I123 y M123. Épica: EP-41: Gestión de Proveedores.

- CA1: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA2: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA3: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.

### HU-123 — Consulta y seguimiento de gestión de proveedores

Excel: Historias de Usuario, I124 y M124. Épica: EP-41: Gestión de Proveedores.

- CA1: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA2: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA3: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.

### HU-124 — Acceso principal a gestión de promociones y descuentos (admin)

Excel: Historias de Usuario, I125 y M125. Épica: EP-42: Gestión de Promociones y Descuentos (Admin).

- CA1: **NO ACREDITADO**. El menú o componente existe, pero ninguna prueba disponible navega desde la navegación principal hasta esta funcionalidad concreta. Un render directo o prueba de API/servicio no acredita navegación.
- CA2: **NO ACREDITADO**. Hay cobertura de API/servicio o de una parte del módulo, pero no prueba ejecutada de la presentación de esta interfaz administrativa completa.
- CA3: **NO ACREDITADO**. No hay pruebas ejecutadas de esta funcionalidad en ambos tamaños móvil y escritorio; clases Bootstrap o una prueba de componente no acreditan responsive.

### HU-125 — Validación de datos en gestión de promociones y descuentos (admin)

Excel: Historias de Usuario, I126 y M126. Épica: EP-42: Gestión de Promociones y Descuentos (Admin).

- CA1: **NO ACREDITADO**. Las pruebas ejercitan algunas reglas o un campo obligatorio, pero no todos los campos obligatorios vacíos de este formulario. La implementación de required/@NotBlank no es por sí sola una prueba completa.
- CA2: **NO ACREDITADO**. No hay prueba de mensaje específico junto al campo con problema. Las excepciones de servicio, respuestas HTTP y alertas generales no acreditan la ubicación exigida.
- CA3: **NO ACREDITADO**. Hay rechazos de algunas entradas o reglas, pero no una prueba del formulario/proceso que demuestre que impide continuar hasta corregir el conjunto de errores. Cobertura parcial no se marca como criterio completo.

### HU-126 — Consulta y seguimiento de gestión de promociones y descuentos (admin)

Excel: Historias de Usuario, I127 y M127. Épica: EP-42: Gestión de Promociones y Descuentos (Admin).

- CA1: **NO ACREDITADO**. No existe prueba que liste al menos dos registros con fechas distintas y compruebe más reciente primero. El nombre de un método OrderBy o listar un único registro no acredita el orden.
- CA2: **NO ACREDITADO**. No hay opción de búsqueda/filtro de esta consulta ejercitada por una prueba de interfaz. Una ruta de consulta backend, selector para editar un estado o navegación entre secciones no prueba un filtro del listado.
- CA3: **NO ACREDITADO**. No se verificó un cambio de datos visible en esta consulta sin recargar la página. Un callback mock, un cambio de filtro, abrir/cerrar detalle o una actualización de servicio no acredita por sí solo ese comportamiento de UI.

### HU-127 — Acceso principal a gestión de banners y contenido home

Excel: Historias de Usuario, I128 y M128. Épica: EP-43: Gestión de Banners y Contenido Home.

- CA1: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA2: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA3: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.

### HU-128 — Validación de datos en gestión de banners y contenido home

Excel: Historias de Usuario, I129 y M129. Épica: EP-43: Gestión de Banners y Contenido Home.

- CA1: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA2: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA3: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.

### HU-129 — Consulta y seguimiento de gestión de banners y contenido home

Excel: Historias de Usuario, I130 y M130. Épica: EP-43: Gestión de Banners y Contenido Home.

- CA1: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA2: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA3: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.

### HU-130 — Acceso principal a gestión de blog (admin)

Excel: Historias de Usuario, I131 y M131. Épica: EP-44: Gestión de Blog (Admin).

- CA1: **NO ACREDITADO**. El menú o componente existe, pero ninguna prueba disponible navega desde la navegación principal hasta esta funcionalidad concreta. Un render directo o prueba de API/servicio no acredita navegación.
- CA2: **NO ACREDITADO**. Hay cobertura de API/servicio o de una parte del módulo, pero no prueba ejecutada de la presentación de esta interfaz administrativa completa.
- CA3: **NO ACREDITADO**. No hay pruebas ejecutadas de esta funcionalidad en ambos tamaños móvil y escritorio; clases Bootstrap o una prueba de componente no acreditan responsive.

### HU-131 — Validación de datos en gestión de blog (admin)

Excel: Historias de Usuario, I132 y M132. Épica: EP-44: Gestión de Blog (Admin).

- CA1: **NO ACREDITADO**. Las pruebas ejercitan algunas reglas o un campo obligatorio, pero no todos los campos obligatorios vacíos de este formulario. La implementación de required/@NotBlank no es por sí sola una prueba completa.
- CA2: **NO ACREDITADO**. No hay prueba de mensaje específico junto al campo con problema. Las excepciones de servicio, respuestas HTTP y alertas generales no acreditan la ubicación exigida.
- CA3: **NO ACREDITADO**. Hay rechazos de algunas entradas o reglas, pero no una prueba del formulario/proceso que demuestre que impide continuar hasta corregir el conjunto de errores. Cobertura parcial no se marca como criterio completo.

### HU-132 — Consulta y seguimiento de gestión de blog (admin)

Excel: Historias de Usuario, I133 y M133. Épica: EP-44: Gestión de Blog (Admin).

- CA1: **NO ACREDITADO**. No existe prueba que liste al menos dos registros con fechas distintas y compruebe más reciente primero. El nombre de un método OrderBy o listar un único registro no acredita el orden.
- CA2: **NO ACREDITADO**. No hay opción de búsqueda/filtro de esta consulta ejercitada por una prueba de interfaz. Una ruta de consulta backend, selector para editar un estado o navegación entre secciones no prueba un filtro del listado.
- CA3: **NO ACREDITADO**. No se verificó un cambio de datos visible en esta consulta sin recargar la página. Un callback mock, un cambio de filtro, abrir/cerrar detalle o una actualización de servicio no acredita por sí solo ese comportamiento de UI.

### HU-133 — Acceso principal a moderación de reseñas (admin)

Excel: Historias de Usuario, I134 y M134. Épica: EP-45: Moderación de Reseñas (Admin).

- CA1: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA2: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA3: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.

### HU-134 — Validación de datos en moderación de reseñas (admin)

Excel: Historias de Usuario, I135 y M135. Épica: EP-45: Moderación de Reseñas (Admin).

- CA1: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA2: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA3: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.

### HU-135 — Consulta y seguimiento de moderación de reseñas (admin)

Excel: Historias de Usuario, I136 y M136. Épica: EP-45: Moderación de Reseñas (Admin).

- CA1: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA2: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA3: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.

### HU-136 — Acceso principal a auditoría de actividad del sistema

Excel: Historias de Usuario, I137 y M137. Épica: EP-46: Auditoría de Actividad del Sistema.

- CA1: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA2: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA3: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.

### HU-137 — Validación de datos en auditoría de actividad del sistema

Excel: Historias de Usuario, I138 y M138. Épica: EP-46: Auditoría de Actividad del Sistema.

- CA1: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA2: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA3: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.

### HU-138 — Consulta y seguimiento de auditoría de actividad del sistema

Excel: Historias de Usuario, I139 y M139. Épica: EP-46: Auditoría de Actividad del Sistema.

- CA1: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA2: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.
- CA3: **NO ACREDITADO**. No se encontró funcionalidad administrativa ni prueba ejecutada de este módulo que acredite el criterio.

### HU-139 — Acceso principal a seguridad y autenticación

Excel: Historias de Usuario, I140 y M140. Épica: EP-47: Seguridad y Autenticación.

- CA1: **CUMPLIDO Y PROBADO**. La prueba de App usa el enlace Iniciar sesión de la navegación principal, ingresa y accede a los enlaces de la sesión.
  - [Frontend/src/App.test.jsx:86](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:86>) — `cierra la sesión y mantiene al usuario desconectado al recargar`
- CA2: **CUMPLIDO Y PROBADO**. Se usan campos etiquetados de correo/contraseña, se muestra un error comprensible al rechazarlas y se comprueba el nombre del usuario y los enlaces de su sesión.
  - [Frontend/src/components/Login.test.jsx:18](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Login.test.jsx:18>) — `envía las credenciales y entrega la sesión junto con el perfil`
  - [Frontend/src/components/Login.test.jsx:38](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/components/Login.test.jsx:38>) — `muestra un mensaje cuando las credenciales son rechazadas`
  - [Frontend/src/App.test.jsx:86](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:86>) — `cierra la sesión y mantiene al usuario desconectado al recargar`
- CA3: **NO ACREDITADO**. No hay pruebas ejecutadas de esta funcionalidad en ambos tamaños móvil y escritorio; clases Bootstrap o una prueba de componente no acreditan responsive.

### HU-140 — Validación de datos en seguridad y autenticación

Excel: Historias de Usuario, I141 y M141. Épica: EP-47: Seguridad y Autenticación.

- CA1: **NO ACREDITADO**. Las pruebas ejercitan algunas reglas o un campo obligatorio, pero no todos los campos obligatorios vacíos de este formulario. La implementación de required/@NotBlank no es por sí sola una prueba completa.
- CA2: **NO ACREDITADO**. No hay prueba de mensaje específico junto al campo con problema. Las excepciones de servicio, respuestas HTTP y alertas generales no acreditan la ubicación exigida.
- CA3: **NO ACREDITADO**. Hay rechazos de algunas entradas o reglas, pero no una prueba del formulario/proceso que demuestre que impide continuar hasta corregir el conjunto de errores. Cobertura parcial no se marca como criterio completo.

### HU-141 — Consulta y seguimiento de seguridad y autenticación

Excel: Historias de Usuario, I142 y M142. Épica: EP-47: Seguridad y Autenticación.

- CA1: **NO ACREDITADO**. No existe prueba que liste al menos dos registros con fechas distintas y compruebe más reciente primero. El nombre de un método OrderBy o listar un único registro no acredita el orden.
- CA2: **NO ACREDITADO**. No hay opción de búsqueda/filtro de esta consulta ejercitada por una prueba de interfaz. Una ruta de consulta backend, selector para editar un estado o navegación entre secciones no prueba un filtro del listado.
- CA3: **CUMPLIDO Y PROBADO**. Al cerrar sesión desaparecen inmediatamente el nombre y el enlace Mi perfil en la misma instancia de App; después se comprueba también que siguen ausentes al remontar.
  - [Frontend/src/App.test.jsx:86](<D:/Respaldos/clases/U-Duoc/Semestre 4/FullStack2/huerto-hogar/huerto hogar/Frontend/src/App.test.jsx:86>) — `cierra la sesión y mantiene al usuario desconectado al recargar`

### HU-142 — Acceso principal a diseño responsivo multiplataforma

Excel: Historias de Usuario, I143 y M143. Épica: EP-48: Diseño Responsivo Multiplataforma.

- CA1: **NO ACREDITADO**. Criterio genérico: responsive no es una opción independiente de navegación; no hay prueba específica del acceso que describe esta historia.
- CA2: **NO ACREDITADO**. La revisión visual cubrió catálogo, checkout y dashboard, no la claridad de todas las vistas que abarca esta épica.
- CA3: **NO ACREDITADO**. Cobertura manual parcial a 320–1440 px para catálogo, checkout y dashboard; no acredita todas las vistas de la épica multiplataforma.

### HU-143 — Validación de datos en diseño responsivo multiplataforma

Excel: Historias de Usuario, I144 y M144. Épica: EP-48: Diseño Responsivo Multiplataforma.

- CA1: **NO ACREDITADO**. Criterio genérico de formulario: no existe un formulario propio de diseño responsive ni una prueba correspondiente.
- CA2: **NO ACREDITADO**. No hay errores de campos específicos del diseño responsive comprobados.
- CA3: **NO ACREDITADO**. No hay proceso de validación de datos propio de responsive comprobado.

### HU-144 — Consulta y seguimiento de diseño responsivo multiplataforma

Excel: Historias de Usuario, I145 y M145. Épica: EP-48: Diseño Responsivo Multiplataforma.

- CA1: **NO ACREDITADO**. No hay historial cronológico de diseño responsive implementado y probado.
- CA2: **NO ACREDITADO**. No hay búsqueda o filtro de diseño responsive implementado y probado.
- CA3: **NO ACREDITADO**. La revisión de tamaños de pantalla no acredita el seguimiento de cambios que describe este criterio genérico.

### HU-145 — Acceso principal a accesibilidad web

Excel: Historias de Usuario, I146 y M146. Épica: EP-49: Accesibilidad Web.

- CA1: **NO ACREDITADO**. Hay navegación semántica y controles ARIA, pero no una prueba de acceso a una funcionalidad propia de accesibilidad.
- CA2: **NO ACREDITADO**. No se ejecutó una verificación completa de accesibilidad/legibilidad de la interfaz.
- CA3: **NO ACREDITADO**. La revisión responsive parcial no prueba accesibilidad en móvil y escritorio.

### HU-146 — Validación de datos en accesibilidad web

Excel: Historias de Usuario, I147 y M147. Épica: EP-49: Accesibilidad Web.

- CA1: **NO ACREDITADO**. No hay formulario propio de accesibilidad ni pruebas de todos sus campos obligatorios.
- CA2: **NO ACREDITADO**. No se comprobaron mensajes asociados a cada campo desde el punto de vista de accesibilidad.
- CA3: **NO ACREDITADO**. No hay proceso de validación propio de esta historia comprobado.

### HU-147 — Consulta y seguimiento de accesibilidad web

Excel: Historias de Usuario, I148 y M148. Épica: EP-49: Accesibilidad Web.

- CA1: **NO ACREDITADO**. No existe un historial cronológico de accesibilidad implementado y probado.
- CA2: **NO ACREDITADO**. No existe búsqueda o filtro de accesibilidad implementado y probado.
- CA3: **NO ACREDITADO**. No existe un seguimiento dinámico de accesibilidad probado.

### HU-148 — Acceso principal a control de versiones y colaboración en github

Excel: Historias de Usuario, I149 y M149. Épica: EP-50: Control de Versiones y Colaboración en GitHub.

- CA1: **NO ACREDITADO**. GitHub es una herramienta externa, no una funcionalidad de la navegación principal de la tienda; falta una prueba correspondiente.
- CA2: **NO ACREDITADO**. No hay una interfaz propia de gestión GitHub evaluada con este criterio.
- CA3: **NO ACREDITADO**. No se comprobaron flujos de GitHub en móvil y escritorio.

### HU-149 — Validación de datos en control de versiones y colaboración en github

Excel: Historias de Usuario, I150 y M150. Épica: EP-50: Control de Versiones y Colaboración en GitHub.

- CA1: **NO ACREDITADO**. No hay formulario propio de GitHub en la tienda con prueba de campos obligatorios.
- CA2: **NO ACREDITADO**. No hay validación de campos GitHub con mensajes específicos probada.
- CA3: **NO ACREDITADO**. No hay proceso de validación GitHub dentro de la tienda probado.

### HU-150 — Consulta y seguimiento de control de versiones y colaboración en github

Excel: Historias de Usuario, I151 y M151. Épica: EP-50: Control de Versiones y Colaboración en GitHub.

- CA1: **NO ACREDITADO**. El historial Git existe, pero no acredita el listado funcional que describe esta historia ni una prueba de su criterio.
- CA2: **NO ACREDITADO**. No hay búsqueda/filtro GitHub propio de la tienda probado.
- CA3: **NO ACREDITADO**. No hay seguimiento dinámico de colaboración GitHub implementado y probado en la tienda.

## Evidencia de ejecución

- [Verificación previa y pruebas manuales](<C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto/verificacion-final.json>)
- [27 comprobaciones de cupones por API](<C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/cupones-api-real.json>)
- [Script de comprobación real de cupones](<C:/Users/danie/Documents/Codex/2026-10-26/es/work/probar-cupones.ps1>)
- [Resultado previo de 65 pruebas backend](<C:/Users/danie/Documents/Codex/2026-10-26/es/work/admin-backend-package.log>)
- [Resultado actual: 60 unitarias con backend apagado](<C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto-requerimientos/unitarias-backend-apagado.log>)
- [Resultado actual: 2 fallos de integración esperados con backend apagado](<C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto-requerimientos/integracion-backend-apagado.log>)
- [Demostración: 2 fallos provocados mediante URL temporal y configuración restaurada](<C:/Users/danie/Documents/Codex/2026-10-26/es/outputs/huerto-requerimientos/demo-fallo-provocado.log>)
