# ERS V2 Huerto Hogar

Actualizado el 29 de octubre de 2026.

El **ERS principal** es [ERS-V2-Huerto-Hogar.docx](ERS-V2-Huerto-Hogar.docx), completado sobre la plantilla DSY1104 Parcial 1 Anexo 4. Conserva portada, índice, ficha, introducción, descripción, interfaces y requisitos. Este Markdown es su resumen de consulta.

## Alcance y pauta

El Word especifica **14 requisitos funcionales y 6 no funcionales**, con actores, comportamiento, estado y evidencia. Se contrastó con la pauta de Parcial 2: React/persistencia/Bootstrap/interactividad (pp. 1–2), vistas públicas y administrativas (pp. 3–10), pruebas de renderizado/props/state/eventos (p. 11) y entregables/presentación (p. 12).

Se declaran dos diferencias: **API real con MySQL en lugar de CRUD simulado en JavaScript**, y **Vitest/Testing Library en lugar de Jasmine/Karma**, según las decisiones del proyecto. No se presenta cumplimiento literal de esas herramientas del PDF.

## Implementado y probado

- Catálogo, búsqueda/filtro, categorías, detalle y regreso conservando filtro: componentes, App y consultas HTTP reales.
- Cuenta, nombre, perfil, contraseña y logout persistente: casos ejecutados; validaciones y navegación completas con cobertura parcial.
- Carrito, stock, dirección autocompletada, entrega y resultado de compra: casos unitarios/backend y compra UI previa con cupón. Entrega programada comprobada unitariamente, sin compra UI persistida acreditada en esa modalidad.
- Cupones: normalización, duplicados, porcentajes, actividad/vencimiento, descuento y revalidación; rechazo sin alterar stock/pedidos. Ofertas depende de `BIENVENIDA10` vigente.
- Noticias/detalle y contacto: lectura, navegación, envío y estados; formularios parcialmente acreditados.
- Administración: indicadores, productos/stock, categorías, usuarios, edición sin cambiar contraseña, historial por usuario y boleta. Otros formularios, reportes y permisos completos requieren ampliar pruebas.

La revisión acreditó **84 de 450 criterios** de la planilla. Los demás no se declaran completos por existir código. Es un backlog ampliado, no 450 exigencias literales del PDF ni porcentaje de cobertura del código.

## Resultados y cobertura

El 29/10 se verificó `iniciar-demo.ps1`: Apache/MySQL, backend y frontend, con **65 pruebas backend, 60 unitarias frontend y 2 HTTP reales aprobadas**. Las 27 comprobaciones API de cupones y la revisión visual corresponden al 26/10.

Con backend apagado se verificaron 60 unitarias aprobadas y 2 HTTP fallidas por `ECONNREFUSED`. `Frontend/demo-fallo-integracion.ps1` reproduce dos fallos con URL temporal y la restaura. Un test negativo debe pasar cuando verifica un rechazo correcto.

Responsive acreditado: catálogo, checkout y dashboard a 320–1440 px; productos administrativos a 320, 768 y 1440 px. No se extrapola a otras vistas/dispositivos.

JaCoCo 0.8.15 está configurado; `mvn clean verify` genera el HTML/XML, pendiente de generar. Frontend no tiene proveedor/script de cobertura. No se informan porcentajes de líneas/ramas sin medición.

## Pendiente o fuera de alcance

- Ampliar validaciones por campo, orden/filtros, pruebas de reportes y formularios, persistencia local entre recargas y revisión visual restante.
- Sin pasarela bancaria, recuperación por correo, wishlist, reseñas, comparador, devoluciones, proveedores ni notificaciones acreditadas.
- Sin pruebas de carga/concurrencia ni auditoría integral de seguridad/accesibilidad.
- El ZIP previo es una captura del 26/10 y requiere regeneración para incluir cambios posteriores. El enlace público GitHub y la entrega al docente quedan por confirmar.

## Operación y evidencia

- [Guía de presentación](README.md#guía-de-presentación-de-15-minutos) y [script de arranque y tests](iniciar-demo.ps1).
- [Backend](Backend/README.md) y [frontend](Frontend/README.md): comandos y ubicación de módulos.
- [Cobertura](Cobertura-Pruebas.md): casos, resultados, límites y medición de código.
- [Tests por criterio](Criterios-y-tests.md): archivo, línea y nombre del caso.
- [Planilla](Plantilla_Gestion_Requerimientos_Scrum_HuertoHogar.xlsx): rojo por criterio acreditado en I; evidencia en M.
