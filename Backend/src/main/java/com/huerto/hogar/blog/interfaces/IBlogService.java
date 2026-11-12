package com.huerto.hogar.blog.interfaces;

import com.huerto.hogar.blog.dto.BlogDto;
import com.huerto.hogar.blog.dto.BlogResponse;

import java.util.List;

public interface IBlogService {
    List<BlogResponse> findAll();          // solo activos
    BlogResponse findById(Long id);        // solo activos
    BlogResponse save(BlogDto dto);
    BlogResponse update(Long id, BlogDto dto);
    void deleteById(Long id);              // borrado lógico
}
