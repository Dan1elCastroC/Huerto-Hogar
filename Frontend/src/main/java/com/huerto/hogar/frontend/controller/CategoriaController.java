package com.huerto.hogar.frontend.controller;

import com.huerto.hogar.frontend.config.FrontendSession;
import com.huerto.hogar.frontend.model.Forms;
import com.huerto.hogar.frontend.service.CategoriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {
    private final CategoriaService service; private final FrontendSession session;
    public CategoriaController(CategoriaService s, FrontendSession session){this.service=s;this.session=session;}

    @GetMapping("/admin") public String admin(Model model){
        if(!session.isAdmin()) return "redirect:/login";
        model.addAttribute("categorias", service.findAll()); return "admin-categorias";
    }
    @GetMapping("/admin/nuevo") public String nuevo(Model model){
        if(!session.isAdmin()) return "redirect:/login";
        model.addAttribute("form",new Forms.Categoria()); return "categoria-form";
    }
    @PostMapping("/admin/guardar") public String guardar(@ModelAttribute("form") Forms.Categoria f, Model model){
        if(!session.isAdmin()) return "redirect:/login";
        try{service.save(f.nombre,f.descripcion);return "redirect:/categorias/admin";}
        catch(Exception e){model.addAttribute("error",e.getMessage());return "categoria-form";}
    }
    @GetMapping("/admin/{id}/editar") public String editar(@PathVariable Long id,Model model){
        if(!session.isAdmin())return"redirect:/login"; var c=service.findById(id); Forms.Categoria f=new Forms.Categoria();f.nombre=c.nombre;f.descripcion=c.descripcion;
        model.addAttribute("form",f);model.addAttribute("id",id);return"categoria-form";
    }
    @PostMapping("/admin/{id}/actualizar") public String actualizar(@PathVariable Long id,@ModelAttribute("form") Forms.Categoria f,Model model){
        if(!session.isAdmin())return"redirect:/login";try{service.update(id,f.nombre,f.descripcion);return"redirect:/categorias/admin";}catch(Exception e){model.addAttribute("error",e.getMessage());return"categoria-form";}
    }
    @GetMapping("/admin/{id}/eliminar") public String eliminar(@PathVariable Long id){if(session.isAdmin())service.delete(id);return"redirect:/categorias/admin";}
}
