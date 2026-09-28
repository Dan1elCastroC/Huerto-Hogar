package com.huerto.hogar.pedido.controller;

import com.huerto.hogar.pedido.dto.CheckoutRequest;
import com.huerto.hogar.pedido.interfaces.IPedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/entities/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos")
public class PedidoController {

    private final IPedidoService service;

    // ── CLIENTE ──────────────────────────────────────────────────────

    @PostMapping("/checkout")
    @Operation(summary = "Confirmar compra (genera pedido desde el carrito)")
    public ResponseEntity<?> checkout(Authentication auth,
            @Valid @RequestBody CheckoutRequest req) {
        try { return ResponseEntity.status(201).body(service.checkout(auth.getName(), req)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @GetMapping("/mis-pedidos")
    @Operation(summary = "Historial de pedidos del usuario autenticado")
    public ResponseEntity<?> historial(Authentication auth) {
        try { return ResponseEntity.ok(service.historial(auth.getName())); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @GetMapping("/mis-pedidos/{id}")
    @Operation(summary = "Ver detalle de un pedido propio")
    public ResponseEntity<?> findById(Authentication auth, @PathVariable Long id) {
        try { return ResponseEntity.ok(service.findById(id, auth.getName())); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }

    // ── ADMIN / VENDEDOR ─────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    @Operation(summary = "Listar todos los pedidos (admin/vendedor)")
    public ResponseEntity<?> findAll() {
        try { return ResponseEntity.ok(service.findAll()); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    @Operation(summary = "Filtrar pedidos por estado")
    public ResponseEntity<?> findByEstado(@PathVariable String estado) {
        try { return ResponseEntity.ok(service.findByEstado(estado)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Cambiar estado de un pedido (admin)")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id,
            @RequestParam String estado) {
        try { return ResponseEntity.ok(service.cambiarEstado(id, estado)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }
}
