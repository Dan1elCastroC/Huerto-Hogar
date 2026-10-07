package com.huerto.hogar.frontend.controller;

import com.huerto.hogar.frontend.config.FrontendSession;
import com.huerto.hogar.frontend.model.Forms;
import com.huerto.hogar.frontend.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/productos")
public class ProductoController {
    private final ProductoService service;
    private final FrontendSession session;
    public ProductoController(ProductoService service, FrontendSession session) { this.service=service; this.session=session; }

    @GetMapping
    public String listar(@RequestParam(required=false) String q, @RequestParam(required=false) Long categoriaId, Model model) {
        try {
            model.addAttribute("productos", q != null && !q.isBlank() ? service.buscar(q) :
                categoriaId != null ? service.porCategoria(categoriaId) : service.findAll());
            model.addAttribute("categorias", service.categorias());
        } catch (Exception e) { model.addAttribute("error", "No fue posible cargar los productos."); }
        model.addAttribute("q", q); model.addAttribute("categoriaId", categoriaId); model.addAttribute("session", session);
        return "productos";
    }

    @GetMapping("/{id}") public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("producto", service.findById(id)); model.addAttribute("session", session); return "producto-detalle";
    }

    @GetMapping("/admin") public String admin(Model model) {
        if (!session.isAdmin()) return "redirect:/login";
        model.addAttribute("productos", service.findAll()); model.addAttribute("session", session); return "admin-productos";
    }

    @GetMapping("/admin/nuevo") public String nuevo(Model model) {
        if (!session.isAdmin()) return "redirect:/login";
        model.addAttribute("form", new Forms.Producto()); model.addAttribute("categorias", service.categorias()); return "producto-form";
    }

    @PostMapping("/admin/guardar")
    public String guardar(@ModelAttribute("form") Forms.Producto f, Model model) {
        if (!session.isAdmin()) return "redirect:/login";
        try { service.save(f); return "redirect:/productos/admin"; }
        catch (Exception e) { model.addAttribute("error", e.getMessage()); model.addAttribute("categorias", service.categorias()); return "producto-form"; }
    }

    @GetMapping("/admin/{id}/editar") public String editar(@PathVariable Long id, Model model) {
        if (!session.isAdmin()) return "redirect:/login";
        var p=service.findById(id); Forms.Producto f=new Forms.Producto();
        f.codigo=p.codigo; f.nombre=p.nombre; f.descripcion=p.descripcion; f.precio=p.precio.toString();
        f.stock=String.valueOf(p.stock); f.stockCritico=String.valueOf(p.stockCritico); f.imagenUrl=p.imagenUrl; f.categoriaId=p.categoriaId;
        model.addAttribute("form", f); model.addAttribute("id", id); model.addAttribute("categorias", service.categorias()); return "producto-form";
    }

    @PostMapping("/admin/{id}/actualizar") public String actualizar(@PathVariable Long id, @ModelAttribute("form") Forms.Producto f, Model model) {
        if (!session.isAdmin()) return "redirect:/login";
        try { service.update(id, f); return "redirect:/productos/admin"; }
        catch (Exception e) { model.addAttribute("error", e.getMessage()); model.addAttribute("id", id); model.addAttribute("categorias", service.categorias()); return "producto-form"; }
    }

    @GetMapping("/admin/{id}/eliminar")
    public String eliminar(@PathVariable Long id) { if (session.isAdmin()) service.delete(id); return "redirect:/productos/admin"; }
}
