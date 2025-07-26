package it.uniroma3.siw.controller.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import it.uniroma3.siw.configuration.ViewResolver;
import it.uniroma3.siw.security.UserPrincipal;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.RecensioneService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class BookController {

    @Autowired 
    private BookService bookService;

    @Autowired 
    private RecensioneService recensioneService;

    @Autowired
    private ViewResolver viewResolver;

    @GetMapping("/book/{id}")
    public String getBook(@PathVariable("id") Long id, Model model,  @AuthenticationPrincipal UserPrincipal self) {
        model.addAttribute("book", this.bookService.findById(id));
        model.addAttribute("reviews", this.recensioneService.findByBookId(id));
        return viewResolver.viewFor("book", self);
    }

    @GetMapping("/books")
    public String getAllBooks(Model model, @AuthenticationPrincipal UserPrincipal self) {
        model.addAttribute("books", this.bookService.getAllBooks());
        return viewResolver.viewFor("books", self);
    }
}
