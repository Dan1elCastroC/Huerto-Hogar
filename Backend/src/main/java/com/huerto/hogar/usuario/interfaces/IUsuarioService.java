package com.huerto.hogar.usuario.interfaces;
import com.huerto.hogar.usuario.dto.*;
import java.util.List;
public interface IUsuarioService {
    UsuarioResponse registrar(RegistroRequest req);
    UsuarioResponse crearDesdeAdmin(RegistroRequest req);
    List<UsuarioResponse> findAll();
    UsuarioResponse findById(Long id);
    UsuarioResponse findByCorreo(String correo);
    UsuarioResponse actualizarPerfil(String correo, ActualizarUsuarioRequest req);
    UsuarioResponse actualizarDesdeAdmin(Long id, RegistroRequest req);
    void cambiarContrasena(String correo, CambioContrasenaRequest req);
    UsuarioResponse toggleActivo(Long id);
    void deleteById(Long id);
}
