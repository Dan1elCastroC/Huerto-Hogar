package com.huerto.hogar.frontend.service;

import com.huerto.hogar.frontend.config.ApiClient;
import com.huerto.hogar.frontend.config.FrontendSession;
import com.huerto.hogar.frontend.model.Forms;
import com.huerto.hogar.frontend.model.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final ApiClient api;
    private final FrontendSession session;

    public AuthService(ApiClient api, FrontendSession session) {
        this.api = api; this.session = session;
    }

    public void login(Forms.Login form) {
        LoginResponse r = api.post("/api/auth/login", form, LoginResponse.class);
        session.login(r.token, r.correo, r.rol, r.usuarioId);
    }

    public void registro(Forms.Registro form) {
        api.post("/api/auth/registro", form, Object.class);
    }

    public void logout() { session.logout(); }
}
