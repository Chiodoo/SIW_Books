package it.uniroma3.siw.controller.common;

import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import it.uniroma3.siw.security.UserPrincipal;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.RecensioneService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class BookController {

    @Autowired BookService bookService;

    @Autowired RecensioneService recensioneService;

    @GetMapping("/book/{id}")
    public String getBook(@PathVariable("id") Long id, Model model,  @AuthenticationPrincipal UserPrincipal self) {
        model.addAttribute("book", this.bookService.findById(id));
        model.addAttribute("reviews", this.recensioneService.findByBookId(id));
        return viewFor("book", self);
    }

    @GetMapping("/books")
    public String getAllBooks(Model model, @AuthenticationPrincipal UserPrincipal self) {
        model.addAttribute("books", this.bookService.getAllBooks());
        return viewFor("books", self);
    }

    private String viewFor(String templateBase, UserPrincipal self) {
        boolean isAdmin = self != null && ADMIN_ROLE.equals(self.getRole());
        return (isAdmin ? "admin/" : "") + templateBase;
    }
}
