package com.huerto.hogar.blog.service;

import com.huerto.hogar.blog.entity.BlogEntity;
import com.huerto.hogar.blog.interfaces.IBlogService;
import com.huerto.hogar.blog.repository.BlogRepository;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogService implements IBlogService {

    private final BlogRepository repo;

    @Override
    public List<BlogEntity> findAll() {
        return repo.findByActivoTrueOrderByCreadoEnDesc();
    }

    @Override
    public BlogEntity findById(Long id) {
        BlogEntity b = getOrThrow(id);
        // Un blog desactivado no debe ser visible públicamente
        if (!b.isActivo())
            throw new RecursoNoEncontradoException("Blog no encontrado: " + id);
        return b;
    }

    @Override
    public BlogEntity save(BlogEntity b) {
        b.setActivo(true);
        return repo.save(b);
    }

    @Override
    public BlogEntity update(Long id, BlogEntity datos) {
        BlogEntity b = getOrThrow(id);
        b.setTitulo(datos.getTitulo());
        b.setDescripcion(datos.getDescripcion());
        b.setImagenUrl(datos.getImagenUrl());
        b.setActivo(datos.isActivo());
        return repo.save(b);
    }

    @Override
    public void deleteById(Long id) {
        BlogEntity b = getOrThrow(id);
        b.setActivo(false);
        repo.save(b);
    }

    private BlogEntity getOrThrow(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Blog no encontrado: " + id));
    }
}
