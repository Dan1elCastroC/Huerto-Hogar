package com.huerto.hogar.frontend.service;

import com.huerto.hogar.frontend.config.ApiClient;
import com.huerto.hogar.frontend.model.Forms;
import com.huerto.hogar.frontend.model.Usuario;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final ApiClient api;
    public UsuarioService(ApiClient api) { this.api = api; }

    public Usuario perfil() { return api.get("/api/usuarios/perfil", Usuario.class); }
    public List<Usuario> all() {
        return api.getList("/api/admin/usuarios", new ParameterizedTypeReference<List<Usuario>>() {});
    }
    public void registrar(Forms.Registro f) { api.post("/api/auth/registro", f, Object.class); }
}
