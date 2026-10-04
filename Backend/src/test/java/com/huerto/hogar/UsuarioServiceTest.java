package com.huerto.hogar;

import com.huerto.hogar.exception.RecursoNoEncontradoException;
import com.huerto.hogar.exception.ReglaDeNegocioException;
import com.huerto.hogar.usuario.dto.CambioContrasenaRequest;
import com.huerto.hogar.usuario.dto.RegistroRequest;
import com.huerto.hogar.usuario.entity.Rol;
import com.huerto.hogar.usuario.entity.UsuarioEntity;
import com.huerto.hogar.usuario.repository.UsuarioRepository;
import com.huerto.hogar.usuario.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock UsuarioRepository repo;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UsuarioService service;

    private RegistroRequest registro() {
        RegistroRequest r = new RegistroRequest();
        r.setRun("19011022K"); r.setNombre("Juan"); r.setApellidos("Perez");
        r.setCorreo("juan@gmail.com"); r.setContrasena("1234"); r.setConfirmarContrasena("1234");
        r.setTelefono("999999999"); r.setFechaNacimiento("2000-01-01");
        r.setDireccion("Calle 1"); r.setRegion("Biobio"); r.setComuna("Coronel");
        return r;
    }

    @Test void registrarRechazaContrasenasDiferentes() {
        RegistroRequest r = registro(); r.setConfirmarContrasena("9999");
        assertThrows(ReglaDeNegocioException.class, () -> service.registrar(r));
        verify(repo, never()).save(any());
    }

    @Test void registrarRechazaCorreoNoPermitido() {
        RegistroRequest r = registro(); r.setCorreo("juan@hotmail.com");
        assertThrows(ReglaDeNegocioException.class, () -> service.registrar(r));
    }

   

    @Test void findByIdInexistenteLanzaExcepcion() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.findById(99L));
    }

    @Test void cambiarContrasenaRechazaContrasenaActualIncorrecta() {
        UsuarioEntity u = UsuarioEntity.builder().id(1L).correo("juan@gmail.com").contrasena("HASH").rol(Rol.CLIENTE).build();
        when(repo.findByCorreo("juan@gmail.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("mala", "HASH")).thenReturn(false);
        CambioContrasenaRequest r = new CambioContrasenaRequest();
        r.setContrasenaActual("mala"); r.setNuevaContrasena("5678"); r.setConfirmarContrasena("5678");
        assertThrows(ReglaDeNegocioException.class, () -> service.cambiarContrasena("juan@gmail.com", r));
        verify(repo, never()).save(any());
    }

    @Test void toggleActivoCambiaEstado() {
        UsuarioEntity u = UsuarioEntity.builder().id(1L).run("19011022K").nombre("Juan").apellidos("Perez")
            .correo("juan@gmail.com").contrasena("HASH").direccion("Calle 1").region("Biobio")
            .comuna("Coronel").rol(Rol.CLIENTE).activo(true).build();
        when(repo.findById(1L)).thenReturn(Optional.of(u));
        when(repo.save(u)).thenReturn(u);
        assertFalse(service.toggleActivo(1L).isActivo());
        verify(repo).save(u);
    }
}
