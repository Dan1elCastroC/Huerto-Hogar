package com.huerto.hogar.usuario.controller;

import com.huerto.hogar.usuario.dto.*;
import com.huerto.hogar.usuario.interfaces.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:5273"})
@RestController
@RequiredArgsConstructor
@Tag(name = "Usuarios")
public class UsuarioController {

    private final IUsuarioService service;

    // ── REGISTRO PÚBLICO ─────────────────────────────────────────────
    @Operation(summary = "Registrar usuario",
            description = "Crea una cuenta de cliente nueva. Endpoint público.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos, RUN o correo duplicado, o contraseñas distintas")
    })
    @PostMapping("/api/auth/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequest req) {
        try { return ResponseEntity.status(201).body(service.registrar(req)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    // ── PERFIL PROPIO ────────────────────────────────────────────────
    @Operation(summary = "Ver perfil propio",
            description = "Devuelve los datos del usuario autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil del usuario",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/usuarios/perfil")
    public ResponseEntity<?> perfil(Authentication auth) {
        try { return ResponseEntity.ok(service.findByCorreo(auth.getName())); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }

    @Operation(summary = "Actualizar perfil propio",
            description = "Modifica los datos personales del usuario autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil actualizado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/api/usuarios/perfil")
    public ResponseEntity<?> actualizarPerfil(Authentication auth,
            @Valid @RequestBody ActualizarUsuarioRequest req) {
        try { return ResponseEntity.ok(service.actualizarPerfil(auth.getName(), req)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Cambiar contraseña",
            description = "Cambia la contraseña del usuario autenticado validando la actual.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contraseña actualizada"),
        @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta o nuevas contraseñas distintas"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/api/usuarios/perfil/contrasena")
    public ResponseEntity<?> cambiarContrasena(Authentication auth,
            @Valid @RequestBody CambioContrasenaRequest req) {
        try {
            service.cambiarContrasena(auth.getName(), req);
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente"));
        } catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    // ── ADMIN ────────────────────────────────────────────────────────
    @Operation(summary = "Listar usuarios",
            description = "Devuelve todos los usuarios. Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de usuarios",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UsuarioResponse.class)))),
        @ApiResponse(responseCode = "400", description = "Error al obtener los usuarios"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/admin/usuarios")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> findAll() {
        try { return ResponseEntity.ok(service.findAll()); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Obtener usuario por ID",
            description = "Devuelve un usuario. Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/admin/usuarios/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try { return ResponseEntity.ok(service.findById(id)); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }

    @Operation(summary = "Crear usuario (admin)",
            description = "Crea un usuario con el rol indicado. Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario creado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o duplicados"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/api/admin/usuarios")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> crearDesdeAdmin(@Valid @RequestBody RegistroRequest req) {
        try { return ResponseEntity.status(201).body(service.crearDesdeAdmin(req)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Actualizar usuario (admin)",
            description = "Modifica datos, rol y contraseña de un usuario. Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario inexistente"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/api/admin/usuarios/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> actualizarDesdeAdmin(@PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioAdminRequest req) {
        try { return ResponseEntity.ok(service.actualizarDesdeAdmin(id, req)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Activar o desactivar usuario",
            description = "Invierte el estado activo de un usuario. Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/api/admin/usuarios/{id}/toggle-activo")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> toggleActivo(@PathVariable Long id) {
        try { return ResponseEntity.ok(service.toggleActivo(id)); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }

    @Operation(summary = "Eliminar usuario",
            description = "Elimina un usuario. Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario eliminado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/api/admin/usuarios/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try { service.deleteById(id); return ResponseEntity.ok("Usuario eliminado"); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }
}
