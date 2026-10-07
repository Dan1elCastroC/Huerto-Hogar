package com.huerto.hogar.frontend.controller;

import com.huerto.hogar.frontend.config.FrontendSession;
import com.huerto.hogar.frontend.model.Forms;
import com.huerto.hogar.frontend.service.ContactoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contacto")
public class ContactoController {
    private final ContactoService service; private final FrontendSession session;
    public ContactoController(ContactoService s,FrontendSession session){service=s;this.session=session;}
    @GetMapping public String form(Model model){model.addAttribute("form",new Forms.Contacto());return"contacto";}
    @PostMapping public String enviar(@ModelAttribute("form") Forms.Contacto f,Model model){try{service.enviar(f);return"redirect:/contacto?enviado";}catch(Exception e){model.addAttribute("error",e.getMessage());return"contacto";}}
    @GetMapping("/admin") public String admin(Model model){if(!session.isAdmin())return"redirect:/login";model.addAttribute("contactos",service.all());return"admin-contacto";}
}
