package com.huerto.hogar;

import com.huerto.hogar.categoria.dto.CategoriaDto;
import com.huerto.hogar.categoria.dto.CategoriaResponse;
import com.huerto.hogar.categoria.entity.CategoriaEntity;
import com.huerto.hogar.categoria.mapper.CategoriaMapper;
import com.huerto.hogar.categoria.repository.CategoriaRepository;
import com.huerto.hogar.categoria.service.CategoriaService;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
import com.huerto.hogar.exception.ReglaDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {
    @Mock CategoriaRepository repo;
    // Mapper real (no un spy): así el código generado por MapStruct también cuenta en JaCoCo.
    final CategoriaMapper mapper = Mappers.getMapper(CategoriaMapper.class);
    CategoriaService service;

    @BeforeEach void preparar() { service = new CategoriaService(repo, mapper); }

    private CategoriaDto dto(String nombre, String descripcion) {
        CategoriaDto d = new CategoriaDto();
        d.setNombre(nombre); d.setDescripcion(descripcion);
        return d;
    }

    @Test void findAllDevuelveCategoriasActivasComoDtos() {
        List<CategoriaEntity> lista = List.of(CategoriaEntity.builder().id(1L).nombre("Frutas").build());
        when(repo.findByActivoTrue()).thenReturn(lista);
        List<CategoriaResponse> r = service.findAll();
        assertEquals(1, r.size());
        assertEquals("Frutas", r.get(0).getNombre());
        verify(repo).findByActivoTrue();
    }

    @Test void findByIdDevuelveDto() {
        when(repo.findById(1L)).thenReturn(Optional.of(CategoriaEntity.builder().id(1L).nombre("Frutas").descripcion("Frescas").build()));
        CategoriaResponse r = service.findById(1L);
        assertEquals(1L, r.getId());
        assertEquals("Frescas", r.getDescripcion());
        assertTrue(r.isActivo());
    }

    @Test void findByIdInexistenteLanzaExcepcion() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.findById(99L));
    }

    @Test void saveConvierteElDtoEnEntidadYDevuelveDto() {
        when(repo.existsByNombre("Frutas")).thenReturn(false);
        when(repo.save(any(CategoriaEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        CategoriaResponse r = service.save(dto("Frutas", "Desc"));
        ArgumentCaptor<CategoriaEntity> cap = ArgumentCaptor.forClass(CategoriaEntity.class);
        verify(repo).save(cap.capture());
        assertEquals("Frutas", cap.getValue().getNombre());
        assertEquals("Desc", cap.getValue().getDescripcion());
        assertEquals("Frutas", r.getNombre());
    }

    @Test void saveNoPermiteNombreRepetido() {
        when(repo.existsByNombre("Frutas")).thenReturn(true);
        assertThrows(ReglaDeNegocioException.class, () -> service.save(dto("Frutas", "Desc")));
        verify(repo, never()).save(any());
    }

    @Test void updateActualizaLosCamposDeLaEntidad() {
        CategoriaEntity c = CategoriaEntity.builder().id(1L).nombre("Viejo").descripcion("Viejo").build();
        when(repo.findById(1L)).thenReturn(Optional.of(c));
        when(repo.save(c)).thenReturn(c);
        CategoriaResponse r = service.update(1L, dto("Nuevo", "Nueva"));
        assertEquals("Nuevo", c.getNombre());
        assertEquals("Nuevo", r.getNombre());
        assertEquals("Nueva", r.getDescripcion());
    }

    @Test void deleteDesactivaCategoria() {
        CategoriaEntity c = CategoriaEntity.builder().id(1L).nombre("Frutas").build();
        when(repo.findById(1L)).thenReturn(Optional.of(c));
        service.deleteById(1L);
        assertFalse(c.isActivo());
        verify(repo).save(c);
    }
}
