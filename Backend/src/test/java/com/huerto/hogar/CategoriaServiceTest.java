package com.huerto.hogar;

import com.huerto.hogar.categoria.entity.CategoriaEntity;
import com.huerto.hogar.categoria.repository.CategoriaRepository;
import com.huerto.hogar.categoria.service.CategoriaService;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
import com.huerto.hogar.exception.ReglaDeNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {
    @Mock CategoriaRepository repo;
    @InjectMocks CategoriaService service;

    @Test void findAllDevuelveCategoriasActivas() {
        List<CategoriaEntity> lista = List.of(CategoriaEntity.builder().id(1L).nombre("Frutas").build());
        when(repo.findByActivoTrue()).thenReturn(lista);
        assertEquals(1, service.findAll().size());
        verify(repo).findByActivoTrue();
    }

    @Test void findByIdInexistenteLanzaExcepcion() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.findById(99L));
    }

    @Test void saveNoPermiteNombreRepetido() {
        when(repo.existsByNombre("Frutas")).thenReturn(true);
        assertThrows(ReglaDeNegocioException.class, () -> service.save("Frutas", "Desc"));
        verify(repo, never()).save(any());
    }

    @Test void deleteDesactivaCategoria() {
        CategoriaEntity c = CategoriaEntity.builder().id(1L).nombre("Frutas").build();
        when(repo.findById(1L)).thenReturn(Optional.of(c));
        service.deleteById(1L);
        assertFalse(c.isActivo());
        verify(repo).save(c);
    }
}
