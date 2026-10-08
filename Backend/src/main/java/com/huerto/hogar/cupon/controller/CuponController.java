package com.huerto.hogar.cupon.controller;

import com.huerto.hogar.cupon.dto.CuponDto;
import com.huerto.hogar.cupon.entity.CuponEntity;
import com.huerto.hogar.cupon.interfaces.ICuponService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDate;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:5273")
@RestController
@RequestMapping("/api/v1/entities/cupones")
@RequiredArgsConstructor
@Tag(name = "Cupones")
public class CuponController {

    private final ICuponService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> findAll() {
        try { return ResponseEntity.ok(service.findAll()); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    // Endpoint público: el cliente lo llama desde el carrito para validar
    @GetMapping("/validar/{codigo}")
    public ResponseEntity<?> validar(@PathVariable String codigo) {
        try { return ResponseEntity.ok(service.findByCodigo(codigo)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> save(@Valid @RequestBody CuponDto dto) {
        try {
            CuponEntity c = CuponEntity.builder()
                .codigo(dto.getCodigo())
                .porcentaje(dto.getPorcentaje())
                .fechaExpiracion(dto.getFechaExpiracion() != null
                    ? LocalDate.parse(dto.getFechaExpiracion()) : null)
                .build();
            return ResponseEntity.status(201).body(service.save(c));
        } catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try { service.deleteById(id); return ResponseEntity.ok("Cupón desactivado"); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }
}
