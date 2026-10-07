package com.huerto.hogar.frontend.controller;

import com.huerto.hogar.frontend.config.FrontendSession;
import com.huerto.hogar.frontend.model.Forms;
import com.huerto.hogar.frontend.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth, FrontendSession session) { this.auth=auth; }

    @GetMapping("/login") public String login(Model model) {
        model.addAttribute("form", new Forms.Login()); return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("form") Forms.Login form, Model model) {
        try { auth.login(form); return "redirect:/"; }
        catch (Exception e) { model.addAttribute("error", "Correo o contraseña incorrectos."); return "login"; }
    }

    @GetMapping("/registro") public String registro(Model model) {
        model.addAttribute("form", new Forms.Registro()); return "registro";
    }

    @PostMapping("/registro")
    public String registro(@ModelAttribute("form") Forms.Registro form, Model model) {
        try { auth.registro(form); return "redirect:/login?registrado"; }
        catch (Exception e) { model.addAttribute("error", e.getMessage()); return "registro"; }
    }

    @GetMapping("/logout")
    public String logout() { auth.logout(); return "redirect:/"; }

}
