package com.huerto.hogar.blog.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import com.huerto.hogar.blog.dto.BlogResponse;
import com.huerto.hogar.blog.dto.BlogDto;
import com.huerto.hogar.blog.interfaces.IBlogService;
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
@RequestMapping("/api/v1/entities/blogs")
@RequiredArgsConstructor
@Tag(name = "Blogs")
public class BlogController {

    private final IBlogService service;

    // Público
    @Operation(summary = "Listar blogs",
            description = "Devuelve todos los artículos del blog. Endpoint público.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de blogs",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BlogResponse.class)))),
        @ApiResponse(responseCode = "400", description = "Error al obtener los blogs")
    })
    @GetMapping
    public ResponseEntity<?> findAll() {
        try { return ResponseEntity.ok(service.findAll()); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    // Público
    @Operation(summary = "Obtener blog por ID",
            description = "Devuelve el detalle de un artículo del blog. Endpoint público.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Blog encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BlogResponse.class))),
        @ApiResponse(responseCode = "404", description = "Blog no encontrado"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try { return ResponseEntity.ok(service.findById(id)); }
        catch (RecursoNoEncontradoException e) { return ResponseEntity.status(404).body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Crear blog",
            description = "Crea un nuevo artículo del blog. Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Blog creado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BlogResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> save(@Valid @RequestBody BlogDto dto) {
        try {
            return ResponseEntity.status(201).body(service.save(dto));
        } catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Actualizar blog",
            description = "Modifica un artículo del blog existente y permite activarlo o desactivarlo. Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Blog actualizado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BlogResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Blog no encontrado"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody BlogDto dto) {
        try {
            return ResponseEntity.ok(service.update(id, dto));
        } catch (RecursoNoEncontradoException e) { return ResponseEntity.status(404).body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Desactivar blog",
            description = "Desactiva (borrado lógico) un artículo del blog. Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Blog desactivado"),
        @ApiResponse(responseCode = "404", description = "Blog no encontrado"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try { service.deleteById(id); return ResponseEntity.ok("Blog desactivado"); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }
}
