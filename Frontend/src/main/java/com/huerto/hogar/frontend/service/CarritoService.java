package com.huerto.hogar.frontend.service;

import com.huerto.hogar.frontend.config.ApiClient;
import com.huerto.hogar.frontend.model.Forms;
import org.springframework.stereotype.Service;

@Service
public class CarritoService {
    private final ApiClient api;
    public CarritoService(ApiClient api) { this.api = api; }

    public Object get(String cupon) {
        String p = "/api/v1/entities/carrito" + (cupon == null || cupon.isBlank() ? "" : "?cupon="+java.net.URLEncoder.encode(cupon, java.nio.charset.StandardCharsets.UTF_8));
        return api.get(p, Object.class);
    }
    public Object agregar(Forms.Carrito f) { return api.post("/api/v1/entities/carrito/agregar", f, Object.class); }
    public Object actualizar(Long id, Integer cantidad) { return api.put("/api/v1/entities/carrito/actualizar/"+id+"?cantidad="+cantidad, null, Object.class); }
    public Object eliminar(Long id) { return api.deleteObject("/api/v1/entities/carrito/eliminar/"+id); }
    public void vaciar() { api.delete("/api/v1/entities/carrito/vaciar"); }
}
