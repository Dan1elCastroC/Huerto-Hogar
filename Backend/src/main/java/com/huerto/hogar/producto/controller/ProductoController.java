package com.huerto.hogar.producto.controller;
import com.huerto.hogar.producto.dto.*;
import com.huerto.hogar.producto.interfaces.IProductoService;
import io.swagger.v3.oas.annotations.Operation;
import com.huerto.hogar.producto.dto.ProductoResponse;
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
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:5273"})
@RestController
@RequestMapping("/api/v1/entities/productos")
@RequiredArgsConstructor
@Tag(name = "Productos")
public class ProductoController {
    private final IProductoService service;
    @Operation(summary = "Listar productos",
            description = "Devuelve el catálogo de productos activos. Endpoint público.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de productos",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductoResponse.class)))),
        @ApiResponse(responseCode = "404", description = "No se pudieron obtener los productos")
    })
    @GetMapping
    public ResponseEntity<?> findAll() {
        try { return ResponseEntity.ok(service.findAll()); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }
    @Operation(summary = "Obtener producto por ID",
            description = "Devuelve el detalle de un producto. Endpoint público.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try { return ResponseEntity.ok(service.findById(id)); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }
    @Operation(summary = "Buscar productos",
            description = "Busca productos por texto en nombre o descripción. Endpoint público.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Productos que coinciden con la búsqueda",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductoResponse.class)))),
        @ApiResponse(responseCode = "400", description = "Búsqueda inválida")
    })
    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam String q) {
        try { return ResponseEntity.ok(service.buscar(q)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }
    @Operation(summary = "Listar productos por categoría",
            description = "Devuelve los productos de una categoría. Endpoint público.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Productos de la categoría",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductoResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<?> findByCategoria(@PathVariable Long categoriaId) {
        try { return ResponseEntity.ok(service.findByCategoria(categoriaId)); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }
    @Operation(summary = "Productos con stock crítico",
            description = "Lista productos cuyo stock está en o bajo el nivel crítico. Solo administrador o vendedor.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Productos con stock crítico",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductoResponse.class)))),
        @ApiResponse(responseCode = "400", description = "Error al obtener los productos"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR o VENDEDOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/stock-critico")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    public ResponseEntity<?> findStockCritico() {
        try { return ResponseEntity.ok(service.findStockCritico()); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }
    @Operation(summary = "Crear producto",
            description = "Crea un producto del catálogo. Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos, código duplicado o categoría inexistente"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> save(@Valid @RequestBody ProductoDto dto) {
        try { return ResponseEntity.status(201).body(service.save(dto)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }
    @Operation(summary = "Actualizar producto",
            description = "Modifica los datos de un producto. Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o producto inexistente"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ProductoDto dto) {
        try { return ResponseEntity.ok(service.update(id, dto)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }
    @Operation(summary = "Eliminar producto",
            description = "Elimina (desactiva) un producto. Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto eliminado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try { service.deleteById(id); return ResponseEntity.ok("Producto eliminado"); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }
}
