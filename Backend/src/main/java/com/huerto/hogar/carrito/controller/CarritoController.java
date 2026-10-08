package com.huerto.hogar.carrito.controller;

import com.huerto.hogar.carrito.dto.CarritoItemDto;
import com.huerto.hogar.carrito.interfaces.ICarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:5273")
@RestController
@RequestMapping("/api/v1/entities/carrito")
@RequiredArgsConstructor
@Tag(name = "Carrito de Compras")
public class CarritoController {

    private final ICarritoService service;

    @GetMapping
    @Operation(summary = "Ver carrito del usuario autenticado (con cupón opcional)")
    public ResponseEntity<?> getCarrito(Authentication auth,
            @RequestParam(required = false) String cupon) {
        try { return ResponseEntity.ok(service.getCarrito(auth.getName(), cupon)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @PostMapping("/agregar")
    @Operation(summary = "Agregar producto al carrito")
    public ResponseEntity<?> agregar(Authentication auth,
            @Valid @RequestBody CarritoItemDto dto) {
        try { return ResponseEntity.ok(service.agregar(auth.getName(), dto)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @PutMapping("/actualizar/{productoId}")
    @Operation(summary = "Actualizar cantidad de un item (0 = eliminar)")
    public ResponseEntity<?> actualizar(Authentication auth,
            @PathVariable Long productoId,
            @RequestParam Integer cantidad) {
        try { return ResponseEntity.ok(service.actualizar(auth.getName(), productoId, cantidad)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @DeleteMapping("/eliminar/{productoId}")
    @Operation(summary = "Eliminar un producto del carrito")
    public ResponseEntity<?> eliminarItem(Authentication auth,
            @PathVariable Long productoId) {
        try { return ResponseEntity.ok(service.eliminarItem(auth.getName(), productoId)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @DeleteMapping("/vaciar")
    @Operation(summary = "Vaciar el carrito completo")
    public ResponseEntity<?> vaciar(Authentication auth) {
        try { service.vaciar(auth.getName()); return ResponseEntity.ok("Carrito vaciado"); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }
}
