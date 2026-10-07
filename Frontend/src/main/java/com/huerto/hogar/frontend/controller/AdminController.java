package com.huerto.hogar.frontend.controller;

import com.huerto.hogar.frontend.config.FrontendSession;
import com.huerto.hogar.frontend.service.ReporteService;
import com.huerto.hogar.frontend.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final FrontendSession session; private final ReporteService reportes; private final UsuarioService usuarios;
    public AdminController(FrontendSession s,ReporteService r,UsuarioService u){session=s;reportes=r;usuarios=u;}
    @GetMapping public String dashboard(Model model){
        if(!session.isAdmin()&&!session.isVendedor())return"redirect:/login";
        model.addAttribute("resumen",reportes.resumen());return"admin";
    }
    @GetMapping("/usuarios") public String users(Model model){if(!session.isAdmin())return"redirect:/login";model.addAttribute("usuarios",usuarios.all());return"admin-usuarios";}
}
