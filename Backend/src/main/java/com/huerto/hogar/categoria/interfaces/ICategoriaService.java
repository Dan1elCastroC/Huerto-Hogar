package com.huerto.hogar.categoria.interfaces;

import com.huerto.hogar.categoria.dto.CategoriaDto;
import com.huerto.hogar.categoria.dto.CategoriaResponse;

import java.util.List;

public interface ICategoriaService {
    List<CategoriaResponse> findAll();
    CategoriaResponse findById(Long id);
    CategoriaResponse save(CategoriaDto dto);
    CategoriaResponse update(Long id, CategoriaDto dto);
    void deleteById(Long id);
}
