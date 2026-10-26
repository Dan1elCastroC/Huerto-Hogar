package com.huerto.hogar.config;

import com.huerto.hogar.categoria.entity.CategoriaEntity;
import com.huerto.hogar.categoria.repository.CategoriaRepository;
import com.huerto.hogar.producto.entity.ProductoEntity;
import com.huerto.hogar.producto.repository.ProductoRepository;
import com.huerto.hogar.usuario.entity.Rol;
import com.huerto.hogar.usuario.entity.UsuarioEntity;
import com.huerto.hogar.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepo;
    private final CategoriaRepository categoriaRepo;
    private final ProductoRepository productoRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsuarios();
        seedCategorias();
        seedProductos();
    }

    private void seedUsuarios() {
        if (!usuarioRepo.existsByCorreo("admin@duoc.cl")) {
            usuarioRepo.save(UsuarioEntity.builder()
                .run("11111111K").nombre("Admin").apellidos("Huerto Hogar")
                .correo("admin@duoc.cl")
                .contrasena(passwordEncoder.encode("admin123"))
                .direccion("Sede Central").region("Biobío").comuna("Concepción")
                .rol(Rol.ADMINISTRADOR).build());
            log.info(">>> Admin: admin@duoc.cl / admin123");
        }
        if (!usuarioRepo.existsByCorreo("vendedor@duoc.cl")) {
            usuarioRepo.save(UsuarioEntity.builder()
                .run("22222222K").nombre("Vendedor").apellidos("Demo")
                .correo("vendedor@duoc.cl")
                .contrasena(passwordEncoder.encode("vend123"))
                .direccion("Tienda Central").region("Biobío").comuna("Concepción")
                .rol(Rol.VENDEDOR).build());
            log.info(">>> Vendedor: vendedor@duoc.cl / vend123");
        }
        if (!usuarioRepo.existsByCorreo("cliente@gmail.com")) {
            usuarioRepo.save(UsuarioEntity.builder()
                .run("33333333K").nombre("Cliente").apellidos("Demo")
                .correo("cliente@gmail.com")
                .contrasena(passwordEncoder.encode("cli123"))
                .direccion("Av. Siempre Viva 123").region("Biobío").comuna("San Pedro de la Paz")
                .rol(Rol.CLIENTE).build());
            log.info(">>> Cliente: cliente@gmail.com / cli123");
        }
    }

    private void seedCategorias() {
        List.of("Frutas","Verduras","Hierbas","Orgánicos","Semillas","Huerto","Hogar")
            .forEach(nombre -> {
                if (!categoriaRepo.existsByNombre(nombre)) {
                    categoriaRepo.save(
                    CategoriaEntity.builder().nombre(nombre)
                        .descripcion("Categoría " + nombre).build());
                }
            });
    }

    private void seedProductos() {
        seedProducto("KIT-001", "Kit de Huerto Urbano", "Kit para comenzar tu huerto en casa.",
            "15990", 12, "kit-huerto.jpg", "Huerto");
        seedProducto("COM-001", "Compostera Doméstica", "Convierte tus residuos orgánicos en abono.",
            "28990", 5, "compostera.jpg", "Hogar");
        seedProducto("HER-001", "Set de Herramientas de Jardín", "Herramientas para cuidar tus plantas y cultivos.",
            "12490", 20, "herramientas.jpg", "Huerto");
        seedProducto("SUS-001", "Sustrato Premium 25L", "Sustrato para macetas y huertos urbanos.",
            "6990", 15, "sustrato.jpg", "Hogar");
        seedProducto("FRU-001", "Manzanas Rojas", "Manzanas frescas seleccionadas, por kilo.",
            "2490", 30, "manzanas.jpg", "Frutas");
        seedProducto("VER-001", "Tomates Cherry", "Tomates cherry frescos, bandeja de 500 gramos.",
            "1990", 25, "tomates.jpg", "Verduras");
        seedProducto("HIE-001", "Albahaca Fresca", "Albahaca aromática en maceta para tu cocina.",
            "2990", 18, "albahaca.jpg", "Hierbas");
        seedProducto("SEM-001", "Semillas de Lechuga", "Sobre de semillas para cultivar lechugas en casa.",
            "1490", 40, "semillas-lechuga.jpg", "Semillas");
    }

    private void seedProducto(String codigo, String nombre, String descripcion,
                              String precio, int stock, String imagen, String categoria) {
        if (productoRepo.existsByCodigo(codigo)) return;
        CategoriaEntity cat = StreamSupport.stream(categoriaRepo.findAll().spliterator(), false)
            .filter(item -> item.getNombre().equals(categoria)).findFirst().orElseThrow();
        productoRepo.save(ProductoEntity.builder()
            .codigo(codigo).nombre(nombre).descripcion(descripcion)
            .precio(new BigDecimal(precio)).stock(stock).stockCritico(3)
            .imagenUrl("/img/productos/" + imagen).categoria(cat).build());
    }
}
