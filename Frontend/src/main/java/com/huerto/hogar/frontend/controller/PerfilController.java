package com.huerto.hogar.frontend.controller;

import com.huerto.hogar.frontend.config.FrontendSession;
import com.huerto.hogar.frontend.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PerfilController {
    private final UsuarioService service; private final FrontendSession session;
    public PerfilController(UsuarioService s,FrontendSession session){service=s;this.session=session;}
    @GetMapping("/perfil") public String perfil(Model model){if(!session.isLogged())return"redirect:/login";model.addAttribute("usuario",service.perfil());return"perfil";}
}
