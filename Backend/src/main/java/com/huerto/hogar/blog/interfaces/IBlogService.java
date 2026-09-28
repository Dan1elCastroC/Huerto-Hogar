package com.huerto.hogar.blog.interfaces;

import com.huerto.hogar.blog.entity.BlogEntity;
import java.util.List;

public interface IBlogService {
    List<BlogEntity> findAll();          // solo activos
    BlogEntity findById(Long id);        // solo activos
    BlogEntity save(BlogEntity blog);
    BlogEntity update(Long id, BlogEntity blog);
    void deleteById(Long id);            // borrado lógico
}
