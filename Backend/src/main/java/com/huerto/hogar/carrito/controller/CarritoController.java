package com.huerto.hogar.carrito.controller;

import com.huerto.hogar.carrito.dto.CarritoItemDto;
import com.huerto.hogar.carrito.interfaces.ICarritoService;
import io.swagger.v3.oas.annotations.Operation;
import com.huerto.hogar.carrito.dto.CarritoResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:5273"})
@RestController
@RequestMapping("/api/v1/entities/carrito")
@RequiredArgsConstructor
@Tag(name = "Carrito de Compras")
public class CarritoController {

    private final ICarritoService service;

    @Operation(summary = "Ver carrito",
            description = "Devuelve el carrito del usuario autenticado con subtotal, descuento y total; aplica un cupón si se envía su código.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrito del usuario",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarritoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Cupón inválido o error al calcular el carrito"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<?> getCarrito(Authentication auth,
            @RequestParam(required = false) String cupon) {
        try { return ResponseEntity.ok(service.getCarrito(auth.getName(), cupon)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Agregar producto al carrito",
            description = "Agrega un producto con la cantidad indicada al carrito del usuario autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrito actualizado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarritoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos, producto inexistente o sin stock suficiente"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(Authentication auth,
            @Valid @RequestBody CarritoItemDto dto) {
        try { return ResponseEntity.ok(service.agregar(auth.getName(), dto)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Actualizar cantidad de un producto",
            description = "Cambia la cantidad de un producto en el carrito; con cantidad 0 lo elimina.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrito actualizado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarritoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Cantidad inválida, producto no está en el carrito o sin stock"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/actualizar/{productoId}")
    public ResponseEntity<?> actualizar(Authentication auth,
            @PathVariable Long productoId,
            @RequestParam Integer cantidad) {
        try { return ResponseEntity.ok(service.actualizar(auth.getName(), productoId, cantidad)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Eliminar producto del carrito",
            description = "Quita un producto del carrito del usuario autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrito actualizado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CarritoResponse.class))),
        @ApiResponse(responseCode = "400", description = "El producto no está en el carrito"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/eliminar/{productoId}")
    public ResponseEntity<?> eliminarItem(Authentication auth,
            @PathVariable Long productoId) {
        try { return ResponseEntity.ok(service.eliminarItem(auth.getName(), productoId)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @Operation(summary = "Vaciar carrito",
            description = "Elimina todos los productos del carrito del usuario autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrito vaciado"),
        @ApiResponse(responseCode = "400", description = "Error al vaciar el carrito"),
        @ApiResponse(responseCode = "401", description = "No autenticado: falta el token JWT o es inválido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/vaciar")
    public ResponseEntity<?> vaciar(Authentication auth) {
        try { service.vaciar(auth.getName()); return ResponseEntity.ok("Carrito vaciado"); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }
}
