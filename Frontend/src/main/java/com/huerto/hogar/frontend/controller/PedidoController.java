package com.huerto.hogar.frontend.controller;

import com.huerto.hogar.frontend.config.FrontendSession;
import com.huerto.hogar.frontend.service.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidoService service; private final FrontendSession session;
    public PedidoController(PedidoService s,FrontendSession session){service=s;this.session=session;}
    @GetMapping public String misPedidos(Model model){if(!session.isLogged())return"redirect:/login";model.addAttribute("pedidos",service.misPedidos());return"pedidos";}
    @GetMapping("/admin") public String admin(Model model){if(!session.isAdmin()&&!session.isVendedor())return"redirect:/login";model.addAttribute("pedidos",service.all());return"admin-pedidos";}
    @GetMapping("/admin/{id}/estado") public String estado(@PathVariable Long id,@RequestParam String estado){if(session.isAdmin())service.estado(id,estado);return"redirect:/pedidos/admin";}
}
