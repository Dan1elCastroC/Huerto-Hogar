package com.huerto.hogar.categoria.controller;
import com.huerto.hogar.categoria.dto.CategoriaDto;
import com.huerto.hogar.categoria.entity.CategoriaEntity;
import com.huerto.hogar.categoria.interfaces.ICategoriaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
@RestController
@RequestMapping("/api/v1/entities/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorías")
public class CategoriaController {
    private final ICategoriaService service;
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
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> save(@Valid @RequestBody CategoriaDto dto) {
        try { return ResponseEntity.status(201).body(service.save(dto.getNombre(), dto.getDescripcion())); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CategoriaDto dto) {
        try { return ResponseEntity.ok(service.update(id, dto.getNombre(), dto.getDescripcion())); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try { service.deleteById(id); return ResponseEntity.ok("Categoría eliminada"); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }
}
