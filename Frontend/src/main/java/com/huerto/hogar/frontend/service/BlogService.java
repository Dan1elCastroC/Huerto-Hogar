package com.huerto.hogar.frontend.service;

import com.huerto.hogar.frontend.config.ApiClient;
import com.huerto.hogar.frontend.model.Blog;
import com.huerto.hogar.frontend.model.Forms;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class BlogService {
    private final ApiClient api;
    public BlogService(ApiClient api) { this.api = api; }

    public List<Blog> findAll() {
        return api.getList("/api/v1/entities/blogs", new ParameterizedTypeReference<List<Blog>>() {});
    }
    public Blog findById(Long id) { return api.get("/api/v1/entities/blogs/"+id, Blog.class); }
    public Blog save(Forms.Blog f) {
        return api.post("/api/v1/entities/blogs",
            Map.of("titulo", f.titulo, "descripcion", f.descripcion, "imagenUrl", f.imagenUrl == null ? "" : f.imagenUrl),
            Blog.class);
    }
    public Blog update(Long id, Forms.Blog f) {
        return api.put("/api/v1/entities/blogs/"+id,
            Map.of("titulo", f.titulo, "descripcion", f.descripcion, "imagenUrl", f.imagenUrl == null ? "" : f.imagenUrl, "activo", f.activo == null || f.activo),
            Blog.class);
    }
    public void delete(Long id) { api.delete("/api/v1/entities/blogs/"+id); }
}
