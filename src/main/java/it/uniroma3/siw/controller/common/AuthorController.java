package it.uniroma3.siw.controller.common;

import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.security.UserPrincipal;
import it.uniroma3.siw.service.AuthorService;

@Controller
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping("/authors")
    public String getAuthors(Model model, @AuthenticationPrincipal UserPrincipal self) {
        model.addAttribute("authors", authorService.getAllAuthors());
        return viewFor("authors", self);
    }

    @GetMapping("/author/{id}")
    public String getAuthor(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserPrincipal self) {
        Author author = authorService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Autore non trovato: " + id));
        model.addAttribute("author", author);
        return viewFor("author", self);
    }

    private String viewFor(String templateBase, UserPrincipal self) {
        boolean isAdmin = self != null && ADMIN_ROLE.equals(self.getRole());
        return (isAdmin ? "admin/" : "") + templateBase;
    }
}
