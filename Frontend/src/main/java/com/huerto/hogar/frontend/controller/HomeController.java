package com.huerto.hogar.frontend.controller;

import com.huerto.hogar.frontend.config.FrontendSession;
import com.huerto.hogar.frontend.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final ProductoService productos;
    private final FrontendSession session;
    public HomeController(ProductoService productos, FrontendSession session) {
        this.productos = productos; this.session = session;
    }

    @GetMapping("/")
    public String home(Model model) {
        try { model.addAttribute("productos", productos.findAll()); }
        catch (Exception e) { model.addAttribute("error", "No fue posible cargar los productos."); }
        model.addAttribute("session", session);
        return "index";
    }
}
