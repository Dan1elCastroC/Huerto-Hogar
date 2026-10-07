package com.huerto.hogar.frontend.service;

import com.huerto.hogar.frontend.config.ApiClient;
import com.huerto.hogar.frontend.model.Forms;
import com.huerto.hogar.frontend.model.Producto;
import com.huerto.hogar.frontend.model.Categoria;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {
    private final ApiClient api;
    public ProductoService(ApiClient api) { this.api = api; }

    public List<Producto> findAll() {
        return api.getList("/api/v1/entities/productos",
                new ParameterizedTypeReference<List<Producto>>() {});
    }
    public Producto findById(Long id) { return api.get("/api/v1/entities/productos/"+id, Producto.class); }
    public List<Producto> buscar(String q) {
        return api.getList("/api/v1/entities/productos/buscar?q="+java.net.URLEncoder.encode(q, java.nio.charset.StandardCharsets.UTF_8),
                new ParameterizedTypeReference<List<Producto>>() {});
    }
    public List<Producto> porCategoria(Long id) {
        return api.getList("/api/v1/entities/productos/categoria/"+id,
                new ParameterizedTypeReference<List<Producto>>() {});
    }
    public Producto save(Forms.Producto f) {
        Map<String,Object> body = Map.of(
            "codigo", f.codigo, "nombre", f.nombre, "descripcion", f.descripcion == null ? "" : f.descripcion,
            "precio", new BigDecimal(f.precio), "stock", Integer.parseInt(f.stock),
            "stockCritico", f.stockCritico == null || f.stockCritico.isBlank() ? 0 : Integer.parseInt(f.stockCritico),
            "imagenUrl", f.imagenUrl == null ? "" : f.imagenUrl, "categoriaId", f.categoriaId);
        return api.post("/api/v1/entities/productos", body, Producto.class);
    }
    public Producto update(Long id, Forms.Producto f) {
        Map<String,Object> body = Map.of(
            "codigo", f.codigo, "nombre", f.nombre, "descripcion", f.descripcion == null ? "" : f.descripcion,
            "precio", new BigDecimal(f.precio), "stock", Integer.parseInt(f.stock),
            "stockCritico", f.stockCritico == null || f.stockCritico.isBlank() ? 0 : Integer.parseInt(f.stockCritico),
            "imagenUrl", f.imagenUrl == null ? "" : f.imagenUrl, "categoriaId", f.categoriaId);
        return api.put("/api/v1/entities/productos/"+id, body, Producto.class);
    }
    public void delete(Long id) { api.delete("/api/v1/entities/productos/"+id); }

    public List<Categoria> categorias() {
        return api.getList("/api/v1/entities/categorias",
                new ParameterizedTypeReference<List<Categoria>>() {});
    }
}
