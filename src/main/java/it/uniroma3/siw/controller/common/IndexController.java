package it.uniroma3.siw.controller.common;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.siw.configuration.ViewResolver;
import it.uniroma3.siw.security.UserPrincipal;
import it.uniroma3.siw.service.BookService;

@Controller
public class IndexController {

    @Autowired
    private ViewResolver viewResolver;

    @Autowired
    private BookService bookService;

    @GetMapping("/")
    public String index(@AuthenticationPrincipal UserPrincipal self, Model model) {
        model.addAttribute("randomBooks", bookService.get3RandomBooks());
        return viewResolver.viewFor("index", self);
    }

    @GetMapping("/success")
    public String defaultAfterLogin(@AuthenticationPrincipal UserPrincipal self, Model model) {
        return "redirect:/";
    }
}