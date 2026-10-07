package com.huerto.hogar.frontend.service;

import com.huerto.hogar.frontend.config.ApiClient;
import com.huerto.hogar.frontend.model.Categoria;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {
    private final ApiClient api;
    public CategoriaService(ApiClient api) { this.api = api; }

    public java.util.List<Categoria> findAll() {
        return api.getList("/api/v1/entities/categorias",
            new ParameterizedTypeReference<java.util.List<Categoria>>() {});
    }
    public Categoria findById(Long id) { return api.get("/api/v1/entities/categorias/"+id, Categoria.class); }
    public Categoria save(String nombre, String descripcion) {
        return api.post("/api/v1/entities/categorias",
            Map.of("nombre", nombre, "descripcion", descripcion == null ? "" : descripcion), Categoria.class);
    }
    public Categoria update(Long id, String nombre, String descripcion) {
        return api.put("/api/v1/entities/categorias/"+id,
            Map.of("nombre", nombre, "descripcion", descripcion == null ? "" : descripcion), Categoria.class);
    }
    public void delete(Long id) { api.delete("/api/v1/entities/categorias/"+id); }
}
