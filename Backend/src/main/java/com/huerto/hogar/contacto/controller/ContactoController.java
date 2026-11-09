package com.huerto.hogar.contacto.controller;

import com.huerto.hogar.contacto.dto.ContactoDto;
import com.huerto.hogar.contacto.entity.ContactoEntity;
import com.huerto.hogar.contacto.interfaces.IContactoService;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:5273"})
@RestController
@RequestMapping("/api/v1/entities/contacto")
@RequiredArgsConstructor
@Tag(name = "Contacto")
public class ContactoController {

    private final IContactoService service;

    // Público: formulario de contacto
    @Operation(summary = "Enviar mensaje de contacto",
            description = "Registra un mensaje desde el formulario de contacto. Endpoint público.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Mensaje registrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody ContactoDto dto) {
        try {
            ContactoEntity c = ContactoEntity.builder()
                .nombre(dto.getNombre())
                .correo(dto.getCorreo())
                .comentario(dto.getComentario())
                .build();
            return ResponseEntity.status(201).body(service.save(c));
        } catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Listar mensajes de contacto",
            description = "Devuelve todos los mensajes recibidos. Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de mensajes"),
        @ApiResponse(responseCode = "400", description = "Error al obtener los mensajes"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> findAll() {
        try { return ResponseEntity.ok(service.findAll()); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Marcar mensaje como leído",
            description = "Marca un mensaje de contacto como leído. Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mensaje marcado como leído"),
        @ApiResponse(responseCode = "404", description = "Mensaje no encontrado"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{id}/leido")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> marcarLeido(@PathVariable Long id) {
        try { return ResponseEntity.ok(service.marcarLeido(id)); }
        catch (RecursoNoEncontradoException e) { return ResponseEntity.status(404).body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }
}
