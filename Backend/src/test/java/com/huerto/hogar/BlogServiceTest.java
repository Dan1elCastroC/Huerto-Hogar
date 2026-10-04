package com.huerto.hogar;

import com.huerto.hogar.blog.entity.BlogEntity;
import com.huerto.hogar.blog.repository.BlogRepository;
import com.huerto.hogar.blog.service.BlogService;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogServiceTest {
    @Mock BlogRepository repo;
    @InjectMocks BlogService service;

    @Test void findByIdDevuelveBlogActivo() {
        BlogEntity b = BlogEntity.builder().id(1L).titulo("Huerto").descripcion("Desc").activo(true).build();
        when(repo.findById(1L)).thenReturn(Optional.of(b));
        assertSame(b, service.findById(1L));
    }

    @Test void findByIdNoDevuelveBlogDesactivado() {
        BlogEntity b = BlogEntity.builder().id(1L).titulo("Huerto").descripcion("Desc").activo(false).build();
        when(repo.findById(1L)).thenReturn(Optional.of(b));
        assertThrows(RecursoNoEncontradoException.class, () -> service.findById(1L));
    }

    @Test void saveActivaBlog() {
        BlogEntity b = BlogEntity.builder().titulo("Huerto").descripcion("Desc").activo(false).build();
        when(repo.save(b)).thenReturn(b);
        service.save(b);
        assertTrue(b.isActivo());
        verify(repo).save(b);
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
