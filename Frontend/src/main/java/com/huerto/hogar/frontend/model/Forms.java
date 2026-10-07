package com.huerto.hogar.frontend.model;

public class Forms {
    public static class Login {
        public String correo;
        public String contrasena;
    }
    public static class Producto {
        public String codigo;
        public String nombre;
        public String descripcion;
        public String precio;
        public String stock;
        public String stockCritico;
        public String imagenUrl;
        public Long categoriaId;
    }
    public static class Categoria {
        public String nombre;
        public String descripcion;
    }
    public static class Blog {
        public String titulo;
        public String descripcion;
        public String imagenUrl;
        public Boolean activo = true;
    }
    public static class Contacto {
        public String nombre;
        public String correo;
        public String comentario;
    }
    public static class Carrito {
        public Long productoId;
        public Integer cantidad;
    }
    public static class Checkout {
        public String direccionEntrega;
        public String fechaEntregaDeseada;
        public String codigoCupon;
    }
    public static class Registro {
        public String run, nombre, apellidos, correo, contrasena, confirmarContrasena;
        public String telefono, fechaNacimiento, direccion, region, comuna, rol;
    }
}
