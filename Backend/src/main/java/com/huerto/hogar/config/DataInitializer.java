package com.huerto.hogar.config;

import com.huerto.hogar.categoria.entity.CategoriaEntity;
import com.huerto.hogar.categoria.repository.CategoriaRepository;
import com.huerto.hogar.usuario.entity.Rol;
import com.huerto.hogar.usuario.entity.UsuarioEntity;
import com.huerto.hogar.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepo;
    private final CategoriaRepository categoriaRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsuarios();
        seedCategorias();
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
        if (((List<CategoriaEntity>) categoriaRepo.findAll()).isEmpty()) {
            List.of("Frutas","Verduras","Hierbas","Orgánicos","Semillas")
                .forEach(nombre -> categoriaRepo.save(
                    CategoriaEntity.builder().nombre(nombre)
                        .descripcion("Categoría " + nombre).build()));
            log.info(">>> 5 categorías creadas");
        }
    }
}
