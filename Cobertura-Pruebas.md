# Cobertura y pruebas de Huerto Hogar

Actualización documental: **29 de octubre de 2026**.

La cobertura funcional describe comportamientos ejercitados. Una función implementada no se considera probada sin evidencia; aprobar casos tampoco determina la cobertura de líneas o ramas.
Se conserva Vitest/Vite, conforme a la excepción del usuario a Jasmine/Karma de la pauta.

- [ERS principal en Word](ERS-V2-Huerto-Hogar.docx), conforme a la plantilla solicitada.
- [Resumen de ERS V2](ERS-V2-Huerto-Hogar.md).
- [Criterios, pruebas exactas y evidencias](Criterios-y-tests.md).
- [Matriz de requisitos](Plantilla_Gestion_Requerimientos_Scrum_HuertoHogar.xlsx).

## Resultados registrados

- **Arranque integrado, 29/10/2026:** `iniciar-demo.ps1` inició XAMPP (Apache/MySQL), backend y frontend; aprobaron 65 pruebas backend, 60 unitarias frontend y 2 HTTP reales. Los registros están en `%TEMP%\HuertoHogar-demo\<ejecución>`.
- **Backend:** 65 pruebas aprobadas tanto el 26/10 como en la verificación integrada del 29/10/2026.
- **Frontend aislado, 29/10/2026:** 60 pruebas aprobadas en 17 suites con el backend apagado; utilizan mocks.
- **Integración HTTP:** 2 pruebas aprobadas con backend activo el 26/10 y el 29/10/2026; las mismas 2 fallaron el 29/10 con `ECONNREFUSED` al estar apagado.
- **API real de cupones, 26/10/2026:** 27 comprobaciones satisfactorias, incluidos vencimiento, permisos, normalización y rechazo de checkout sin alterar stock/pedidos.
- **Demostración negativa, 29/10/2026:** 2 fallos intencionales usando una URL temporal sin backend; se verificó la restauración de `VITE_API_URL`.
- **Aceptación:** 84 de 450 criterios acreditados individualmente. Solo esos textos de la columna I están en rojo; la columna M y [Criterios-y-tests](Criterios-y-tests.md) justifican cada decisión. Esta relación no mide cobertura de código.

Los resultados de integración correctos y los fallos esperados corresponden a condiciones y fechas diferentes; los fallos de conexión no representan pruebas funcionales aprobadas.

## Ejecución y ubicación

Desde `Backend`, con Java 17 y Maven en `PATH`:

```powershell
mvn test
mvn clean verify
```

Las pruebas backend usan mocks y H2 en memoria con el [perfil test](Backend/src/test/resources/application-test.properties); no necesitan iniciar la API ni MySQL/XAMPP.
Sus 13 archivos Java y 65 métodos `@Test` están en [Backend/src/test/java/com/huerto/hogar](Backend/src/test/java/com/huerto/hogar).

Desde `Frontend`:

```powershell
npm test
npm run test:integration
.\demo-fallo-integracion.ps1
```

La integración real requiere la API en `http://localhost:8087` o `VITE_API_URL` y la conexión del servidor a MySQL.
El [script de demostración](Frontend/demo-fallo-integracion.ps1) provoca el fallo de conexión y restaura la variable en `finally`; no inicia/detiene servidores ni modifica la base.
Hay 18 archivos de pruebas frontend: 17 aislados y [1 archivo HTTP con 2 casos](Frontend/src/services/api.integration.test.js). Sumados a los 13 Java, son **31 archivos de pruebas**.
Los nombres de cada caso y sus líneas se encuentran en [Criterios-y-tests.md](Criterios-y-tests.md).

## Comportamientos comprobados

- **Cuenta:** registro, confirmación de contraseña, acceso/rechazo de credenciales y cierre de sesión persistente; edición del formulario de perfil y entrega de sus datos al callback.
- **Catálogo:** lectura real de productos/categorías, errores de conexión, búsqueda, filtro por categoría, detalle de producto y regreso al listado; apertura y regreso desde una noticia.
- **Carrito y checkout:** agregar/modificar/eliminar, límites de stock, totales, dirección autocompletada, entrega programada y validación de fecha, confirmación y rechazo del pedido.
- **Cupones:** validación pública, aplicación/retiro, porcentajes, redondeo monetario, códigos normalizados y vencidos/inactivos; el backend vuelve a validar al confirmar.
- **Administración:** indicadores del dashboard, edición de productos, filtro de stock crítico, categorías, alta/edición de usuarios, conservación del hash al omitir contraseña, historial por usuario y detalle de boleta.
- **Persistencia:** las pruebas H2 ejercitan inicialización idempotente, consultas de productos con categoría y compra con historial/detalles, descuento de stock y vaciado del carrito.

Estos casos no acreditan por sí solos todas las pantallas, reglas ni criterios de cada módulo.

## Pauta PDF, página 11: React

- **Renderizado:** [ProductList](Frontend/src/components/ProductList.jsx) genera productos y estados de carga/error; [sus pruebas](Frontend/src/components/ProductList.test.jsx) comprueban la información visible.
- **Props:** `productos`, `categorias` y callbacks conectan el catálogo con sus acciones; [App.test.jsx](Frontend/src/App.test.jsx) ejercita navegación, detalle y adición al carrito.
- **Estado:** [Carrito](Frontend/src/components/Carrito.jsx) usa `useState` para checkout, entrega y cupón; [Carrito.test.jsx](Frontend/src/components/Carrito.test.jsx) comprueba cambios de modalidad, fechas y descuento.
- **Eventos:** `onChange`, `onClick` y `onSubmit` conectan controles y guardado; [MainPage.test.jsx](Frontend/src/components/MainPage.test.jsx) y [CategoryManager.test.jsx](Frontend/src/components/admin/CategoryManager.test.jsx) ejercitan edición y actualización.

Los ejemplos permiten localizar la aplicación de estos conceptos; no sustituyen las verificaciones de aceptación por criterio.

## Alcance visual registrado

Revisión manual en Chrome: catálogo, checkout y dashboard a **320, 360, 768, 1024 y 1440 px**, sin desbordamiento horizontal en esas muestras.
Productos de administración se revisó a **320, 768 y 1440 px**. Esto no acredita automáticamente otras vistas, todos los dispositivos ni accesibilidad completa.

## Cobertura de código

El [pom.xml](Backend/pom.xml) configura **JaCoCo 0.8.15**, con instrumentación y generación del informe en la fase `verify`.
En la revisión documental no existe `Backend/target/site/jacoco`; no hay counters HTML/XML ni porcentaje medido que informar.
Para generarlo: `mvn clean verify` desde `Backend`. Salidas esperadas: `target/site/jacoco/index.html` y `target/site/jacoco/jacoco.xml`.

El [frontend](Frontend/package.json) no tiene proveedor ni script de cobertura configurados. No se agregaron dependencias para medirla ni se publica un porcentaje.
La cantidad de casos aprobados y los 84 criterios acreditados no se convierten en porcentajes de líneas, ramas o instrucciones.

## Límites de la evidencia

- `ReportManager` y otras vistas administrativas carecen de pruebas específicas de interfaz; código existente no equivale a comportamiento probado.
- La navegación a Nosotros comprueba su encabezado, no todo su contenido; no hay aserción dedicada del texto/color del badge de estado del pedido.
- No hay comprobación dedicada completa de errores junto a cada campo ni de restauración del carrito desde `localStorage` tras recargar.
- No existe una pasarela bancaria integrada y probada; checkout registra el pedido local del proyecto.
- No se acreditan pruebas de carga, concurrencia, auditoría integral de seguridad, matriz completa de permisos ni publicación en GitHub.

Las decisiones concretas sobre cada criterio, incluidos los pendientes, están en [Criterios-y-tests.md](Criterios-y-tests.md).

