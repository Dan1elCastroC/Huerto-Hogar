package com.huerto.hogar.frontend.service;

import com.huerto.hogar.frontend.config.ApiClient;
import com.huerto.hogar.frontend.model.Forms;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {
    private final ApiClient api;
    public PedidoService(ApiClient api) { this.api = api; }

    public List<Object> misPedidos() {
        return api.getList("/api/v1/entities/pedidos/mis-pedidos",
            new ParameterizedTypeReference<List<Object>>() {});
    }
    public Object checkout(Forms.Checkout f) { return api.post("/api/v1/entities/pedidos/checkout", f, Object.class); }
    public List<Object> all() {
        return api.getList("/api/v1/entities/pedidos", new ParameterizedTypeReference<List<Object>>() {});
    }
    public Object estado(Long id, String estado) {
        return api.patch("/api/v1/entities/pedidos/"+id+"/estado?estado="+java.net.URLEncoder.encode(estado, java.nio.charset.StandardCharsets.UTF_8), Object.class);
    }
}
