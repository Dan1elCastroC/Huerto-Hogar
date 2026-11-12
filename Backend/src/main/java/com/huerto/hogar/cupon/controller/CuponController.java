package com.huerto.hogar.cupon.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import com.huerto.hogar.cupon.dto.CuponResponse;
import com.huerto.hogar.cupon.dto.CuponDto;
import com.huerto.hogar.cupon.interfaces.ICuponService;
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
@RequestMapping("/api/v1/entities/cupones")
@RequiredArgsConstructor
@Tag(name = "Cupones")
public class CuponController {

    private final ICuponService service;

    @Operation(summary = "Listar cupones",
            description = "Devuelve todos los cupones de descuento. Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de cupones",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CuponResponse.class)))),
        @ApiResponse(responseCode = "400", description = "Error al obtener los cupones"),
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

    // Endpoint público: el cliente lo llama desde el carrito para validar
    @Operation(summary = "Validar cupón",
            description = "Consulta un cupón por su código para verificar que existe, está activo y no ha expirado. Endpoint público.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cupón válido",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CuponResponse.class))),
        @ApiResponse(responseCode = "400", description = "Cupón inexistente, inactivo o expirado")
    })
    @GetMapping("/validar/{codigo}")
    public ResponseEntity<?> validar(@PathVariable String codigo) {
        try { return ResponseEntity.ok(service.findByCodigo(codigo)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Crear cupón",
            description = "Crea un cupón de descuento (porcentaje entre 1 y 100). Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cupón creado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CuponResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos, fecha mal formada o código duplicado"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> save(@Valid @RequestBody CuponDto dto) {
        try {
            return ResponseEntity.status(201).body(service.save(dto));
        } catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Desactivar cupón",
            description = "Desactiva un cupón (borrado lógico). Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cupón desactivado"),
        @ApiResponse(responseCode = "404", description = "Cupón no encontrado"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try { service.deleteById(id); return ResponseEntity.ok("Cupón desactivado"); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }
}
