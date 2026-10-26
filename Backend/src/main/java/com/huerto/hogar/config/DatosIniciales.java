package com.huerto.hogar.config;

import com.huerto.hogar.blog.entity.BlogEntity;
import com.huerto.hogar.blog.repository.BlogRepository;
import com.huerto.hogar.categoria.entity.CategoriaEntity;
import com.huerto.hogar.categoria.repository.CategoriaRepository;
import com.huerto.hogar.cupon.entity.CuponEntity;
import com.huerto.hogar.cupon.repository.CuponRepository;
import com.huerto.hogar.producto.entity.ProductoEntity;
import com.huerto.hogar.producto.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

/**
 * Carga datos de demostración una sola vez. Comprueba códigos/nombres antes
 * de insertar para no duplicar registros en cada reinicio.
 */
@Configuration
public class DatosIniciales {
    @Bean
    CommandLineRunner cargarDatosIniciales(
            CategoriaRepository categorias,
            ProductoRepository productos,
            BlogRepository blogs,
            CuponRepository cupones) {
        return args -> {
            Map<String, CategoriaEntity> porNombre = new HashMap<>();
            crearCategoria(categorias, porNombre, "Huerto", "Productos para cultivar en casa.");
            crearCategoria(categorias, porNombre, "Hogar", "Artículos prácticos y sostenibles para el hogar.");
            crearCategoria(categorias, porNombre, "Herramientas", "Herramientas para jardinería.");

            crearProducto(productos, porNombre, "KIT-001", "Kit de Huerto Urbano",
                    "Kit inicial para cultivar verduras y hierbas en casa.", "15990.00", 12, 3,
                    "/img/productos/kit-huerto.jpg", "Huerto");
            crearProducto(productos, porNombre, "COM-001", "Compostera Doméstica",
                    "Compostera para aprovechar residuos orgánicos.", "28990.00", 5, 2,
                    "/img/productos/compostera.jpg", "Hogar");
            crearProducto(productos, porNombre, "HER-001", "Set de Herramientas de Jardín",
                    "Set básico para cuidar plantas y huertos.", "12490.00", 20, 4,
                    "/img/productos/herramientas.jpg", "Herramientas");
            crearProducto(productos, porNombre, "SUS-001", "Sustrato Premium 25L",
                    "Sustrato para maceteros y huertos urbanos.", "6990.00", 15, 4,
                    "/img/productos/sustrato.jpg", "Hogar");

            if (blogs.count() == 0) {
                blogs.save(BlogEntity.builder()
                        .titulo("Cómo comenzar tu huerto en casa")
                        .descripcion("Elige un lugar con luz, utiliza un sustrato adecuado y comienza con hierbas fáciles de cuidar.")
                        .imagenUrl("https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=800")
                        .activo(true).build());
            }

            if (!cupones.existsByCodigo("BIENVENIDA10")) {
                cupones.save(CuponEntity.builder()
                        .codigo("BIENVENIDA10")
                        .porcentaje(new BigDecimal("10.00"))
                        .fechaExpiracion(LocalDate.now().plusYears(1))
                        .activo(true).build());
            }
        };
    }

    private void crearCategoria(CategoriaRepository repo, Map<String, CategoriaEntity> cache,
                               String nombre, String descripcion) {
        CategoriaEntity categoria = encontrarCategoria(repo, nombre);
        if (categoria == null) {
            categoria = repo.save(CategoriaEntity.builder().nombre(nombre)
                    .descripcion(descripcion).activo(true).build());
        }
        cache.put(nombre, categoria);
    }

    private CategoriaEntity encontrarCategoria(CategoriaRepository repo, String nombre) {
        for (CategoriaEntity c : repo.findAll()) {
            if (nombre.equalsIgnoreCase(c.getNombre())) return c;
        }
        return null;
    }

    private void crearProducto(ProductoRepository repo, Map<String, CategoriaEntity> categorias,
                               String codigo, String nombre, String descripcion, String precio,
                               int stock, int stockCritico, String imagen, String categoriaNombre) {
        if (repo.existsByCodigo(codigo)) return;
        CategoriaEntity categoria = categorias.get(categoriaNombre);
        repo.save(ProductoEntity.builder()
                .codigo(codigo).nombre(nombre).descripcion(descripcion)
                .precio(new BigDecimal(precio)).stock(stock).stockCritico(stockCritico)
                .imagenUrl(imagen).categoria(categoria).activo(true).build());
    }
}
