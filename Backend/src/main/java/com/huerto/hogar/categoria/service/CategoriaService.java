package com.huerto.hogar.categoria.service;

import com.huerto.hogar.categoria.dto.CategoriaDto;
import com.huerto.hogar.categoria.dto.CategoriaResponse;
import com.huerto.hogar.categoria.entity.CategoriaEntity;
import com.huerto.hogar.categoria.interfaces.ICategoriaService;
import com.huerto.hogar.categoria.mapper.CategoriaMapper;
import com.huerto.hogar.categoria.repository.CategoriaRepository;
import com.huerto.hogar.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService implements ICategoriaService {

    private final CategoriaRepository repo;
    private final CategoriaMapper mapper;

    @Override public List<CategoriaResponse> findAll() { return mapper.toDtoList(repo.findByActivoTrue()); }

    @Override public CategoriaResponse findById(Long id) { return mapper.toDto(getOrThrow(id)); }

    @Override public CategoriaResponse save(CategoriaDto dto) {
        if (repo.existsByNombre(dto.getNombre())) throw new ReglaDeNegocioException("Ya existe una categoría con ese nombre");
        return mapper.toDto(repo.save(mapper.toEntity(dto)));
    }

    @Override public CategoriaResponse update(Long id, CategoriaDto dto) {
        CategoriaEntity c = getOrThrow(id);
        c.setNombre(dto.getNombre()); c.setDescripcion(dto.getDescripcion());
        return mapper.toDto(repo.save(c));
    }

    @Override public void deleteById(Long id) {
        CategoriaEntity c = getOrThrow(id);
        c.setActivo(false); repo.save(c);
    }

    private CategoriaEntity getOrThrow(Long id) {
        return repo.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada: " + id));
    }
}
