package com.huerto.hogar.blog.service;

import com.huerto.hogar.blog.dto.BlogDto;
import com.huerto.hogar.blog.dto.BlogResponse;
import com.huerto.hogar.blog.entity.BlogEntity;
import com.huerto.hogar.blog.interfaces.IBlogService;
import com.huerto.hogar.blog.mapper.BlogMapper;
import com.huerto.hogar.blog.repository.BlogRepository;
import com.huerto.hogar.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogService implements IBlogService {

    private final BlogRepository repo;
    private final BlogMapper mapper;

    @Override
    public List<BlogResponse> findAll() {
        return mapper.toDtoList(repo.findByActivoTrueOrderByCreadoEnDesc());
    }

    @Override
    public BlogResponse findById(Long id) {
        BlogEntity b = getOrThrow(id);
        // Un blog desactivado no debe ser visible públicamente
        if (!b.isActivo())
            throw new RecursoNoEncontradoException("Blog no encontrado: " + id);
        return mapper.toDto(b);
    }

    @Override
    public BlogResponse save(BlogDto dto) {
        BlogEntity b = mapper.toEntity(dto);
        b.setActivo(true);
        return mapper.toDto(repo.save(b));
    }

    @Override
    public BlogResponse update(Long id, BlogDto datos) {
        BlogEntity b = getOrThrow(id);
        b.setTitulo(datos.getTitulo());
        b.setDescripcion(datos.getDescripcion());
        b.setImagenUrl(datos.getImagenUrl());
        b.setActivo(datos.getActivo() == null || datos.getActivo());
        return mapper.toDto(repo.save(b));
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
