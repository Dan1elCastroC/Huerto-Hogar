package com.huerto.hogar;

import com.huerto.hogar.blog.dto.BlogDto;
import com.huerto.hogar.blog.dto.BlogResponse;
import com.huerto.hogar.blog.entity.BlogEntity;
import com.huerto.hogar.blog.mapper.BlogMapper;
import com.huerto.hogar.blog.repository.BlogRepository;
import com.huerto.hogar.blog.service.BlogService;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
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
class BlogServiceTest {
    @Mock BlogRepository repo;
    // Mapper real (no un spy): así el código generado por MapStruct también cuenta en JaCoCo.
    final BlogMapper mapper = Mappers.getMapper(BlogMapper.class);
    BlogService service;

    @BeforeEach void preparar() { service = new BlogService(repo, mapper); }

    private BlogDto dto(Boolean activo) {
        BlogDto d = new BlogDto();
        d.setTitulo("Huerto"); d.setDescripcion("Desc"); d.setImagenUrl("http://i/x.jpg"); d.setActivo(activo);
        return d;
    }

    @Test void findByIdDevuelveBlogActivoComoDto() {
        BlogEntity b = BlogEntity.builder().id(1L).titulo("Huerto").descripcion("Desc").activo(true).build();
        when(repo.findById(1L)).thenReturn(Optional.of(b));
        BlogResponse r = service.findById(1L);
        assertEquals(1L, r.getId());
        assertEquals("Huerto", r.getTitulo());
        assertTrue(r.isActivo());
    }

    @Test void findByIdNoDevuelveBlogDesactivado() {
        BlogEntity b = BlogEntity.builder().id(1L).titulo("Huerto").descripcion("Desc").activo(false).build();
        when(repo.findById(1L)).thenReturn(Optional.of(b));
        assertThrows(RecursoNoEncontradoException.class, () -> service.findById(1L));
    }

    @Test void findAllConvierteLasEntidadesEnDtos() {
        when(repo.findByActivoTrueOrderByCreadoEnDesc()).thenReturn(List.of(
            BlogEntity.builder().id(2L).titulo("A").descripcion("B").build()));
        List<BlogResponse> lista = service.findAll();
        assertEquals(1, lista.size());
        assertEquals("A", lista.get(0).getTitulo());
    }

    @Test void saveActivaBlogAunqueElDtoVengaInactivo() {
        when(repo.save(any(BlogEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        BlogResponse r = service.save(dto(false));
        ArgumentCaptor<BlogEntity> cap = ArgumentCaptor.forClass(BlogEntity.class);
        verify(repo).save(cap.capture());
        assertTrue(cap.getValue().isActivo());
        assertEquals("http://i/x.jpg", cap.getValue().getImagenUrl());
        assertTrue(r.isActivo());
    }

    @Test void updateSinActivoLoDejaActivo() {
        BlogEntity b = BlogEntity.builder().id(1L).titulo("Viejo").descripcion("Viejo").activo(false).build();
        when(repo.findById(1L)).thenReturn(Optional.of(b));
        when(repo.save(b)).thenReturn(b);
        BlogResponse r = service.update(1L, dto(null));
        assertTrue(b.isActivo());
        assertEquals("Huerto", b.getTitulo());
        assertEquals("Huerto", r.getTitulo());
    }

    @Test void updateConActivoFalsoLoDesactiva() {
        BlogEntity b = BlogEntity.builder().id(1L).titulo("Viejo").descripcion("Viejo").activo(true).build();
        when(repo.findById(1L)).thenReturn(Optional.of(b));
        when(repo.save(b)).thenReturn(b);
        assertFalse(service.update(1L, dto(false)).isActivo());
        assertFalse(b.isActivo());
    }

    @Test void updateInexistenteLanzaExcepcion() {
        when(repo.findById(9L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.update(9L, dto(null)));
    }

    @Test void deleteDesactivaBlog() {
        BlogEntity b = BlogEntity.builder().id(1L).titulo("Huerto").descripcion("Desc").activo(true).build();
        when(repo.findById(1L)).thenReturn(Optional.of(b));
        when(repo.save(b)).thenReturn(b);
        service.deleteById(1L);
        assertFalse(b.isActivo());
        verify(repo).save(b);
    }
}
