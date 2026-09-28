package com.huerto.hogar.blog.controller;

import com.huerto.hogar.blog.dto.BlogDto;
import com.huerto.hogar.blog.entity.BlogEntity;
import com.huerto.hogar.blog.interfaces.IBlogService;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/entities/blogs")
@RequiredArgsConstructor
@Tag(name = "Blogs")
public class BlogController {

    private final IBlogService service;

    // Público
    @GetMapping
    public ResponseEntity<?> findAll() {
        try { return ResponseEntity.ok(service.findAll()); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    // Público
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try { return ResponseEntity.ok(service.findById(id)); }
        catch (RecursoNoEncontradoException e) { return ResponseEntity.status(404).body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> save(@Valid @RequestBody BlogDto dto) {
        try {
            BlogEntity b = BlogEntity.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .imagenUrl(dto.getImagenUrl())
                .build();
            return ResponseEntity.status(201).body(service.save(b));
        } catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody BlogDto dto) {
        try {
            BlogEntity datos = BlogEntity.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .imagenUrl(dto.getImagenUrl())
                .activo(dto.getActivo() == null || dto.getActivo())
                .build();
            return ResponseEntity.ok(service.update(id, datos));
        } catch (RecursoNoEncontradoException e) { return ResponseEntity.status(404).body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try { service.deleteById(id); return ResponseEntity.ok("Blog desactivado"); }
        catch (Exception e) { return ResponseEntity.status(404).body(e.getMessage()); }
    }
}
