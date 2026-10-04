package com.huerto.hogar.usuario.service;

import com.huerto.hogar.exception.*;
import com.huerto.hogar.usuario.dto.*;
import com.huerto.hogar.usuario.entity.Rol;
import com.huerto.hogar.usuario.entity.UsuarioEntity;
import com.huerto.hogar.usuario.interfaces.IUsuarioService;
import com.huerto.hogar.usuario.repository.UsuarioRepository;
import com.huerto.hogar.util.ValidadorRun;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UsuarioService implements IUsuarioService {

    private static final Pattern CORREO_OK =
        Pattern.compile("^[\\w.+-]+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)$",
                        Pattern.CASE_INSENSITIVE);

    private final UsuarioRepository repo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponse registrar(RegistroRequest req) {
        validarNuevo(req);
        return toResponse(repo.save(buildEntity(req, Rol.CLIENTE)));
    }

    @Override
    public UsuarioResponse crearDesdeAdmin(RegistroRequest req) {
        validarNuevo(req);
        Rol rol = parsearRol(req.getRol(), Rol.CLIENTE);
        return toResponse(repo.save(buildEntity(req, rol)));
    }

    @Override
    public List<UsuarioResponse> findAll() {
        return StreamSupport.stream(repo.findAll().spliterator(), false)
            .map(this::toResponse).toList();
    }

    @Override
    public UsuarioResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Override
    public UsuarioResponse findByCorreo(String correo) {
        return toResponse(repo.findByCorreo(correo)
            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado")));
    }

    @Override
    public UsuarioResponse actualizarPerfil(String correo, ActualizarUsuarioRequest req) {
        UsuarioEntity u = repo.findByCorreo(correo)
            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        u.setNombre(req.getNombre());
        u.setApellidos(req.getApellidos());
        u.setTelefono(req.getTelefono());
        u.setFechaNacimiento(parseFecha(req.getFechaNacimiento()));
        u.setDireccion(req.getDireccion());
        u.setRegion(req.getRegion());
        u.setComuna(req.getComuna());
        return toResponse(repo.save(u));
    }

    @Override
    public UsuarioResponse actualizarDesdeAdmin(Long id, RegistroRequest req) {
        UsuarioEntity u = getOrThrow(id);
        if (!u.getCorreo().equalsIgnoreCase(req.getCorreo())) {
            validarDominio(req.getCorreo());
            if (repo.existsByCorreo(req.getCorreo()))
                throw new ReglaDeNegocioException("El correo ya está registrado");
            u.setCorreo(req.getCorreo().toLowerCase());
        }
        u.setNombre(req.getNombre());
        u.setApellidos(req.getApellidos());
        u.setTelefono(req.getTelefono());
        u.setFechaNacimiento(parseFecha(req.getFechaNacimiento()));
        u.setDireccion(req.getDireccion());
        u.setRegion(req.getRegion());
        u.setComuna(req.getComuna());
        if (req.getRol() != null && !req.getRol().isBlank())
            u.setRol(parsearRol(req.getRol(), u.getRol()));
        if (req.getContrasena() != null && !req.getContrasena().isBlank()) {
            if (!req.getContrasena().equals(req.getConfirmarContrasena()))
                throw new ReglaDeNegocioException("Las contraseñas no coinciden");
            u.setContrasena(passwordEncoder.encode(req.getContrasena()));
        }
        return toResponse(repo.save(u));
    }

    @Override
    public void cambiarContrasena(String correo, CambioContrasenaRequest req) {
        UsuarioEntity u = repo.findByCorreo(correo)
            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        if (!passwordEncoder.matches(req.getContrasenaActual(), u.getContrasena()))
            throw new ReglaDeNegocioException("La contraseña actual no es correcta");
        if (!req.getNuevaContrasena().equals(req.getConfirmarContrasena()))
            throw new ReglaDeNegocioException("Las contraseñas nuevas no coinciden");
        u.setContrasena(passwordEncoder.encode(req.getNuevaContrasena()));
        repo.save(u);
    }

    @Override
    public UsuarioResponse toggleActivo(Long id) {
        UsuarioEntity u = getOrThrow(id);
        u.setActivo(!u.isActivo());
        return toResponse(repo.save(u));
    }

    @Override
    public void deleteById(Long id) {
        UsuarioEntity u = getOrThrow(id);
        u.setActivo(false);
        repo.save(u);
    }

    // ── helpers privados ─────────────────────────────────────────────

    private void validarNuevo(RegistroRequest req) {
        if (!ValidadorRun.esValido(req.getRun()))
            throw new ReglaDeNegocioException("RUN inválido: " + req.getRun() + ". Ej: 190110222");
        validarDominio(req.getCorreo());
        if (repo.existsByRun(req.getRun().toUpperCase()))
            throw new ReglaDeNegocioException("El RUN ya está registrado");
        if (repo.existsByCorreo(req.getCorreo().toLowerCase()))
            throw new ReglaDeNegocioException("El correo ya está registrado");
        if (!req.getContrasena().equals(req.getConfirmarContrasena()))
            throw new ReglaDeNegocioException("Las contraseñas no coinciden");
    }

    private void validarDominio(String correo) {
        if (!CORREO_OK.matcher(correo).matches())
            throw new ReglaDeNegocioException(
                "Solo se aceptan correos @duoc.cl, @profesor.duoc.cl o @gmail.com");
    }

    private UsuarioEntity buildEntity(RegistroRequest req, Rol rol) {
        return UsuarioEntity.builder()
            .run(req.getRun().toUpperCase())
            .nombre(req.getNombre())
            .apellidos(req.getApellidos())
            .correo(req.getCorreo().toLowerCase())
            .contrasena(passwordEncoder.encode(req.getContrasena()))
            .telefono(req.getTelefono())
            .fechaNacimiento(parseFecha(req.getFechaNacimiento()))
            .direccion(req.getDireccion())
            .region(req.getRegion())
            .comuna(req.getComuna())
            .rol(rol)
            .build();
    }

    private Rol parsearRol(String rolStr, Rol fallback) {
        if (rolStr == null || rolStr.isBlank()) return fallback;
        try { return Rol.valueOf(rolStr.toUpperCase()); }
        catch (IllegalArgumentException e) {
            throw new ReglaDeNegocioException("Rol inválido: " + rolStr +
                ". Valores: ADMINISTRADOR, VENDEDOR, CLIENTE");
        }
    }

    private LocalDate parseFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) return null;
        try { return LocalDate.parse(fecha); }
        catch (Exception e) { throw new ReglaDeNegocioException("Formato de fecha inválido. Use yyyy-MM-dd"); }
    }

    private UsuarioEntity getOrThrow(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));
    }

    public UsuarioResponse toResponse(UsuarioEntity u) {
        return UsuarioResponse.builder()
            .id(u.getId()).run(u.getRun()).nombre(u.getNombre())
            .apellidos(u.getApellidos()).correo(u.getCorreo())
            .telefono(u.getTelefono()).fechaNacimiento(u.getFechaNacimiento())
            .direccion(u.getDireccion()).region(u.getRegion()).comuna(u.getComuna())
            .rol(u.getRol().name()).activo(u.isActivo()).creadoEn(u.getCreadoEn())
            .build();
    }
}
