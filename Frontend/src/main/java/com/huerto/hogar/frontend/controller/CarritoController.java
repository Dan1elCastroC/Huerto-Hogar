package com.huerto.hogar.frontend.controller;

import com.huerto.hogar.frontend.config.FrontendSession;
import com.huerto.hogar.frontend.model.Forms;
import com.huerto.hogar.frontend.service.CarritoService;
import com.huerto.hogar.frontend.service.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/carrito")
public class CarritoController {
    private final CarritoService carrito; private final PedidoService pedidos; private final FrontendSession session;
    public CarritoController(CarritoService c, PedidoService p, FrontendSession s){carrito=c;pedidos=p;session=s;}

    @GetMapping public String ver(@RequestParam(required=false) String cupon,Model model){
        if(!session.isLogged())return"redirect:/login";
        model.addAttribute("carrito",carrito.get(cupon));model.addAttribute("session",session);return"carrito";
    }
    @PostMapping("/agregar") public String agregar(@ModelAttribute Forms.Carrito f){
        if(session.isLogged()) carrito.agregar(f); return"redirect:/carrito";
    }
    @GetMapping("/eliminar/{id}") public String eliminar(@PathVariable Long id){if(session.isLogged())carrito.eliminar(id);return"redirect:/carrito";}
    @GetMapping("/vaciar") public String vaciar(){if(session.isLogged())carrito.vaciar();return"redirect:/carrito";}
    @GetMapping("/checkout") public String checkout(Model model){if(!session.isLogged())return"redirect:/login";model.addAttribute("form",new Forms.Checkout());return"checkout";}
    @PostMapping("/checkout") public String confirmar(@ModelAttribute("form") Forms.Checkout f,Model model){
        try{pedidos.checkout(f);return"redirect:/pedidos?comprado";}catch(Exception e){model.addAttribute("error",e.getMessage());return"checkout";}
    }
}
