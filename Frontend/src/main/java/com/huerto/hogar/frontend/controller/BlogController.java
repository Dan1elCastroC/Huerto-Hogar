package com.huerto.hogar.frontend.controller;

import com.huerto.hogar.frontend.config.FrontendSession;
import com.huerto.hogar.frontend.model.Forms;
import com.huerto.hogar.frontend.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/blog")
public class BlogController {
    private final BlogService service; private final FrontendSession session;
    public BlogController(BlogService s, FrontendSession session){this.service=s;this.session=session;}

    @GetMapping public String listar(Model model){model.addAttribute("blogs",service.findAll());model.addAttribute("session",session);return"blog";}
    @GetMapping("/{id}") public String detalle(@PathVariable Long id,Model model){model.addAttribute("blog",service.findById(id));return"blog-detalle";}
    @GetMapping("/admin") public String admin(Model model){if(!session.isAdmin())return"redirect:/login";model.addAttribute("blogs",service.findAll());return"admin-blog";}
    @GetMapping("/admin/nuevo") public String nuevo(Model model){if(!session.isAdmin())return"redirect:/login";model.addAttribute("form",new Forms.Blog());return"blog-form";}
    @PostMapping("/admin/guardar") public String guardar(@ModelAttribute("form") Forms.Blog f,Model model){if(!session.isAdmin())return"redirect:/login";try{service.save(f);return"redirect:/blog/admin";}catch(Exception e){model.addAttribute("error",e.getMessage());return"blog-form";}}
    @GetMapping("/admin/{id}/eliminar") public String eliminar(@PathVariable Long id){if(session.isAdmin())service.delete(id);return"redirect:/blog/admin";}
}
