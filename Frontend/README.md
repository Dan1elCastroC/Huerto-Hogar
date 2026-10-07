# Huerto-Hogar - Frontend

Frontend realizado siguiendo la estructura del ejemplo entregado por el profesor.

## Tecnologías
- Spring Boot
- Thymeleaf
- Bootstrap
- RestTemplate
- Java 17

## Arquitectura
Controller -> Service -> ApiClient -> Backend REST

El Backend original NO está incluido ni modificado.

## Ejecutar
1. Levantar primero el Backend en `http://localhost:8080`.
2. Desde esta carpeta ejecutar:
   `mvn spring-boot:run`
3. Abrir:
   `http://localhost:7777`

## Módulos
- Productos
- Categorías
- Login / Registro
- Carrito
- Checkout
- Pedidos
- Perfil
- Blog
- Contacto
- Administración
- Reportes/resumen

## Nota
El Frontend se está planteando siguiendo la misma estructura del ejemplo que dejó el profesor: un proyecto Spring Boot separado del Backend, utilizando Thymeleaf para las vistas y RestTemplate para comunicarse con las APIs del Backend.

La idea de la estructura es:

Controller → Service → RestTemplate → Backend REST

En el README.md dejé explicada la estructura general, las tecnologías utilizadas, cómo levantar el proyecto y los módulos que debería manejar el Frontend.

El pom.xml contiene las dependencias necesarias para trabajar con:

Spring Boot
Thymeleaf
Spring Web MVC
Testing
Java 17

Importante: esto corresponde solamente al Frontend. No se modificó el Backend.

El Frontend debería ejecutarse en el puerto 7777 y consumir el Backend desde http://localhost:8080.

Los módulos que se están considerando son:

Productos
Categorías
Login y Registro
Carrito
Checkout
Pedidos
Perfil de usuario
Blog
Contacto
Administración
Usuarios
Reportes

Por ahora subí principalmente la base del proyecto (pom.xml) y la documentación (README.md) para que quede establecido cómo vamos a trabajar y podamos seguir desarrollando sobre la misma estructura.

Cuando continúes, sería bueno mantener esta arquitectura y no modificar el Backend, ya que la idea es que el Frontend simplemente consuma las rutas REST que ya existen.
