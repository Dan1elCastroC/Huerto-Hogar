package com.huerto.hogar.frontend.service;

import com.huerto.hogar.frontend.config.ApiClient;
import com.huerto.hogar.frontend.model.Forms;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class ContactoService {
    private final ApiClient api;
    public ContactoService(ApiClient api) { this.api = api; }
    public void enviar(Forms.Contacto f) { api.post("/api/v1/entities/contacto", f, Object.class); }
    public List<Object> all() {
        return api.getList("/api/v1/entities/contacto", new ParameterizedTypeReference<List<Object>>() {});
    }
}
