package com.huerto.hogar.pedido.controller;

import com.huerto.hogar.pedido.dto.CheckoutRequest;
import com.huerto.hogar.pedido.interfaces.IPedidoService;
import io.swagger.v3.oas.annotations.Operation;
import com.huerto.hogar.pedido.dto.PedidoResponse;
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
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:5273"})
@RestController
@RequestMapping("/api/v1/entities/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos")
public class PedidoController {

    private final IPedidoService service;

    // ── CLIENTE ──────────────────────────────────────────────────────

    @Operation(summary = "Confirmar compra",
            description = "Genera un pedido a partir del carrito del usuario autenticado, aplicando cupón opcional y descontando stock.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido creado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PedidoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos, carrito vacío o stock insuficiente"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(Authentication auth,
            @Valid @RequestBody CheckoutRequest req) {
        try { return ResponseEntity.status(201).body(service.checkout(auth.getName(), req)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Historial de pedidos propios",
            description = "Lista los pedidos del usuario autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de pedidos del usuario",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PedidoResponse.class)))),
        @ApiResponse(responseCode = "400", description = "Error al obtener el historial"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/mis-pedidos")
    public ResponseEntity<?> historial(Authentication auth) {
        try { return ResponseEntity.ok(service.historial(auth.getName())); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Detalle de un pedido propio",
            description = "Devuelve el detalle de un pedido del usuario autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PedidoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado o no pertenece al usuario"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/mis-pedidos/{id}")
    public ResponseEntity<?> findById(Authentication auth, @PathVariable Long id) {
        try { return ResponseEntity.ok(service.findById(id, auth.getName())); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }

    // ── ADMIN / VENDEDOR ─────────────────────────────────────────────

    @Operation(summary = "Listar todos los pedidos",
            description = "Devuelve todos los pedidos de la tienda. Solo administrador o vendedor.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de pedidos",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PedidoResponse.class)))),
        @ApiResponse(responseCode = "400", description = "Error al obtener los pedidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR o VENDEDOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    public ResponseEntity<?> findAll() {
        try { return ResponseEntity.ok(service.findAll()); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Filtrar pedidos por estado",
            description = "Lista los pedidos que están en el estado indicado. Solo administrador o vendedor.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedidos del estado indicado",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PedidoResponse.class)))),
        @ApiResponse(responseCode = "400", description = "Estado inválido"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR o VENDEDOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    public ResponseEntity<?> findByEstado(@PathVariable String estado) {
        try { return ResponseEntity.ok(service.findByEstado(estado)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Cambiar estado de un pedido",
            description = "Actualiza el estado de un pedido. Solo administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PedidoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Estado inválido, transición no permitida o pedido inexistente"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido"),
        @ApiResponse(responseCode = "403", description = "Sin permisos: se requiere rol ADMINISTRADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id,
            @RequestParam String estado) {
        try { return ResponseEntity.ok(service.cambiarEstado(id, estado)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }
}
