package com.huerto.hogar.categoria.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import com.huerto.hogar.categoria.dto.CategoriaResponse;
import com.huerto.hogar.categoria.dto.CategoriaDto;
import com.huerto.hogar.categoria.interfaces.ICategoriaService;
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
@RequestMapping("/api/v1/entities/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorías")
public class CategoriaController {
    private final ICategoriaService service;

    @Operation(summary = "Listar categorías",
            description = "Devuelve todas las categorías de productos. Endpoint público.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de categorías",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CategoriaResponse.class)))),
        @ApiResponse(responseCode = "404", description = "No se pudieron obtener las categorías")
    })
    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            return ResponseEntity.ok(service.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @Operation(summary = "Obtener categoría por ID",
            description = "Devuelve una categoría. Endpoint público.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría encontrada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaResponse.class))),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @Operation(summary = "Crear categoría",
            description = "Crea una categoría de productos. Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Categoría creada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> save(@Valid @RequestBody CategoriaDto dto) {
        try {
            return ResponseEntity.status(201).body(service.save(dto));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar categoría",
            description = "Modifica nombre y descripción de una categoría. Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría actualizada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o categoría inexistente"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CategoriaDto dto) {
        try {
            return ResponseEntity.ok(service.update(id, dto));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar categoría",
            description = "Elimina una categoría. Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría eliminada"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.deleteById(id);
            return ResponseEntity.ok("Categoría eliminada");
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
