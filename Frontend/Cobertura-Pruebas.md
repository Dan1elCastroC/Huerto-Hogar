# Cobertura funcional de pruebas Huerto Hogar

26 de octubre de 2026

## Alcance y criterio

Este registro relaciona los comportamientos comprobados con las pruebas ejecutadas. Describe cobertura funcional por casos; no informa un porcentaje de cobertura de líneas, ramas ni código.

Se mantiene Vitest con Vite conforme a la instrucción del usuario, sin agregar Jasmine ni Karma.

## Resultados ejecutados

- Backend: 65 pruebas, cero fallos.
- Frontend: 60 pruebas distribuidas en 17 suites, cero fallos.
- Validaciones de la API real de cupones: 27 comprobaciones satisfactorias.
- ESLint: aprobado.
- Compilación de producción con Vite: aprobada.
Revisión responsive realizada en Chrome con anchos CSS de 320, 360, 768, 1024 y 1440 px: catálogo, checkout y dashboard sin desbordamiento horizontal. También se verificó productos de administración a 320, 768 y 1440 px.

## Casos cubiertos

### Cuenta y autorización

Registro y validaciones de usuario, inicio de sesión, consulta y edición del perfil, cambio de contraseña y cierre de sesión. La regresión de cierre verifica que la cuenta desaparezca de la interfaz y continúe desconectada al volver a cargarla. El backend comprueba permisos para las operaciones protegidas.

### Catálogo y navegación pública

Carga de productos desde la API, error de conexión sin catálogo ficticio, búsqueda y filtro por categoría, navegación a nosotros y categorías, apertura del detalle de producto, incorporación al carrito y regreso al listado filtrado. Se comprueba la apertura y el regreso desde el detalle de una noticia.

### Carrito y checkout

Cantidades, eliminación, totales, sincronización y confirmación del pedido. Se verifica el envío de datos de entrega y el resultado real de la operación. Las pruebas del backend cubren las reglas de stock y el registro de pedidos.

### Cupones y ofertas

Consulta de validación pública, descuento válido, rechazo de cupón vencido o inactivo y validaciones del porcentaje. La vista de ofertas requiere validación del backend. La estimación visual no sustituye el cálculo definitivo del pedido.

### Administración y persistencia

Servicios de productos, categorías, usuarios, pedidos, cupones, noticias, contactos y carrito; persistencia e integración del proyecto. Las pruebas de interfaz comprueban la navegación administrativa y las operaciones implementadas, incluidas las actualizaciones de categorías en los formularios dependientes.

## Límites

No se integra ni se prueba una pasarela bancaria externa. Los resultados de pruebas automatizadas no equivalen a la revisión visual de todos los tamaños de pantalla. Los casos y cifras anteriores no acreditan publicación en GitHub ni ejecución de pruebas de carga.

Referencia: [ERS V2](ERS-V2-Huerto-Hogar.md).

