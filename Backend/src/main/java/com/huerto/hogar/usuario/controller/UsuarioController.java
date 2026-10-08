package com.huerto.hogar.usuario.controller;

import com.huerto.hogar.usuario.dto.*;
import com.huerto.hogar.usuario.interfaces.IUsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:5273")
@RestController
@RequiredArgsConstructor
@Tag(name = "Usuarios")
public class UsuarioController {

    private final IUsuarioService service;

    // ── REGISTRO PÚBLICO ─────────────────────────────────────────────
    @PostMapping("/api/auth/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequest req) {
        try { return ResponseEntity.status(201).body(service.registrar(req)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    // ── PERFIL PROPIO ────────────────────────────────────────────────
    @GetMapping("/api/usuarios/perfil")
    public ResponseEntity<?> perfil(Authentication auth) {
        try { return ResponseEntity.ok(service.findByCorreo(auth.getName())); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }

    @PutMapping("/api/usuarios/perfil")
    public ResponseEntity<?> actualizarPerfil(Authentication auth,
            @Valid @RequestBody ActualizarUsuarioRequest req) {
        try { return ResponseEntity.ok(service.actualizarPerfil(auth.getName(), req)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @PutMapping("/api/usuarios/perfil/contrasena")
    public ResponseEntity<?> cambiarContrasena(Authentication auth,
            @Valid @RequestBody CambioContrasenaRequest req) {
        try {
            service.cambiarContrasena(auth.getName(), req);
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente"));
        } catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    // ── ADMIN ────────────────────────────────────────────────────────
    @GetMapping("/api/admin/usuarios")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> findAll() {
        try { return ResponseEntity.ok(service.findAll()); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @GetMapping("/api/admin/usuarios/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try { return ResponseEntity.ok(service.findById(id)); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }

    @PostMapping("/api/admin/usuarios")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> crearDesdeAdmin(@Valid @RequestBody RegistroRequest req) {
        try { return ResponseEntity.status(201).body(service.crearDesdeAdmin(req)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @PutMapping("/api/admin/usuarios/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> actualizarDesdeAdmin(@PathVariable Long id,
            @Valid @RequestBody RegistroRequest req) {
        try { return ResponseEntity.ok(service.actualizarDesdeAdmin(id, req)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @PatchMapping("/api/admin/usuarios/{id}/toggle-activo")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> toggleActivo(@PathVariable Long id) {
        try { return ResponseEntity.ok(service.toggleActivo(id)); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }

    @DeleteMapping("/api/admin/usuarios/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try { service.deleteById(id); return ResponseEntity.ok("Usuario eliminado"); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }
}
