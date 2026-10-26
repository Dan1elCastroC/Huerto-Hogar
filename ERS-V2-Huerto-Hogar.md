# ERS V2 Huerto Hogar

Versión 2 | 26 de octubre de 2026

## Objetivo y alcance

Huerto Hogar permite consultar productos, administrar un carrito y registrar pedidos. La interfaz React consume el backend real mediante HTTP y conserva los datos del negocio en MySQL de XAMPP. Este documento describe la implementación de la segunda entrega y la distribución pública y administrativa del proyecto.

El checkout registra pedidos del proyecto. No integra una pasarela de pagos externa ni acredita cobros bancarios.

## Usuarios y permisos

- Visitante: consulta la tienda, sus categorías, noticias, ofertas y detalles; puede preparar un carrito y registrarse.
- Cliente: inicia sesión, utiliza sus datos en el checkout, confirma pedidos y consulta su perfil e historial.
- Administrador: accede al panel, gestiona productos, categorías, usuarios, pedidos, noticias, contactos y cupones, y consulta reportes.
- Vendedor: rol definido en el backend; sus operaciones dependen de las autorizaciones de la API. El panel administrativo de la interfaz se reserva al administrador.

La API comprueba permisos y autenticación; ocultar controles en el navegador no sustituye esa validación.

## Requisitos funcionales

1. Presentar inicio, nosotros, catálogo, categorías, ofertas, noticias y contacto en la tienda pública.
2. Buscar y filtrar productos por categoría. Permitir abrir el detalle, volver al catálogo y agregar unidades disponibles al carrito.
3. Mostrar categorías reales y permitir acceder a sus productos. Mostrar listado y detalle de noticias obtenidas del backend.
4. Mostrar ofertas únicamente después de validar el cupón público `BIENVENIDA10`. Los precios informativos con descuento no sustituyen la validación del total en el checkout; los cupones inactivos o vencidos se rechazan.
5. Registrar usuarios, autenticar sesiones y mostrar el nombre de la cuenta. El cierre de sesión elimina la sesión almacenada y los controles de cuenta.
6. Permitir modificar cantidades, eliminar productos y vaciar el carrito. Respetar el stock y sincronizar las operaciones del usuario autenticado con el backend.
7. Solicitar los datos de entrega, completar la dirección disponible en el perfil y confirmar el pedido. Mostrar el resumen exitoso o el error real y conservar el carrito cuando falle la operación.
8. Permitir consultar pedidos y su detalle o boleta. Registrar el pedido y descontar stock mediante el backend.
9. Presentar un dashboard administrativo separado de la tienda, con indicadores reales y accesos a sus módulos.
10. Administrar productos y categorías mediante sus operaciones de creación, consulta, edición y eliminación.
11. Administrar usuarios y consultar su historial de compras. En la edición, la contraseña es opcional: dejarla vacía conserva la existente.
12. Consultar pedidos, boletas, reportes y stock crítico. Permitir gestionar el perfil y regresar a la tienda o cerrar sesión desde el panel.

## Restricciones y calidad

Se conserva la estructura del proyecto, Java 17 y las versiones existentes de sus dependencias. El backend atiende en `http://localhost:8087`; MySQL conserva las entidades y relaciones del negocio. Se mantienen React, Bootstrap, Vite y Vitest. Por instrucción del usuario, no se incorpora Jasmine ni Karma.

La interfaz debe adaptarse a móvil, tableta y escritorio. Verificación visual responsive pendiente de cierre.

## Validación y entrega

Las pruebas funcionales y sus resultados se describen en [Cobertura de pruebas](Cobertura-Pruebas.md). La entrega incluye este ERS actualizado, ese documento, el proyecto frontend comprimido y el enlace GitHub solicitado por la pauta. Este documento no acredita publicación ni cambios remotos.
