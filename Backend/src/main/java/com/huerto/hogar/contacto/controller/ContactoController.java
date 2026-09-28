package com.huerto.hogar.contacto.controller;

import com.huerto.hogar.contacto.dto.ContactoDto;
import com.huerto.hogar.contacto.entity.ContactoEntity;
import com.huerto.hogar.contacto.interfaces.IContactoService;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/entities/contacto")
@RequiredArgsConstructor
@Tag(name = "Contacto")
public class ContactoController {

    private final IContactoService service;

    // Público: formulario de contacto
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody ContactoDto dto) {
        try {
            ContactoEntity c = ContactoEntity.builder()
                .nombre(dto.getNombre())
                .correo(dto.getCorreo())
                .comentario(dto.getComentario())
                .build();
            return ResponseEntity.status(201).body(service.save(c));
        } catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> findAll() {
        try { return ResponseEntity.ok(service.findAll()); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }

    @PatchMapping("/{id}/leido")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> marcarLeido(@PathVariable Long id) {
        try { return ResponseEntity.ok(service.marcarLeido(id)); }
        catch (RecursoNoEncontradoException e) { return ResponseEntity.status(404).body(e.getMessage()); }
        catch (Exception e) { return ResponseEntity.status(400).body(e.getMessage()); }
    }
}
