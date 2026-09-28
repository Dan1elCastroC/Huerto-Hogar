package com.huerto.hogar.producto.controller;
import com.huerto.hogar.producto.dto.*;
import com.huerto.hogar.producto.interfaces.IProductoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
@RestController
@RequestMapping("/api/v1/entities/productos")
@RequiredArgsConstructor
@Tag(name = "Productos")
public class ProductoController {
    private final IProductoService service;
    @GetMapping
    public ResponseEntity<?> findAll() {
        try { return ResponseEntity.ok(service.findAll()); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try { return ResponseEntity.ok(service.findById(id)); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }
    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam String q) {
        try { return ResponseEntity.ok(service.buscar(q)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<?> findByCategoria(@PathVariable Long categoriaId) {
        try { return ResponseEntity.ok(service.findByCategoria(categoriaId)); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }
    @GetMapping("/stock-critico")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','VENDEDOR')")
    public ResponseEntity<?> findStockCritico() {
        try { return ResponseEntity.ok(service.findStockCritico()); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> save(@Valid @RequestBody ProductoDto dto) {
        try { return ResponseEntity.status(201).body(service.save(dto)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ProductoDto dto) {
        try { return ResponseEntity.ok(service.update(id, dto)); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try { service.deleteById(id); return ResponseEntity.ok("Producto eliminado"); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }
}
