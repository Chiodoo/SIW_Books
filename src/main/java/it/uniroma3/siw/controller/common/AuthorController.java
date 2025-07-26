package it.uniroma3.siw.controller.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.configuration.ViewResolver;
import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.security.UserPrincipal;
import it.uniroma3.siw.service.AuthorService;

@Controller
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private ViewResolver viewResolver;

    @GetMapping("/authors")
    public String getAuthors(Model model, @AuthenticationPrincipal UserPrincipal self) {
        model.addAttribute("authors", authorService.getAllAuthors());
        return viewResolver.viewFor("authors", self);
    }

    @GetMapping("/author/{id}")
    public String getAuthor(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserPrincipal self) {
        Author author = authorService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Autore non trovato: " + id));
        model.addAttribute("author", author);
        return viewResolver.viewFor("author", self);
    }

    @GetMapping("/authors/search")
    public String searchAuthors(
            @RequestParam("q") String query,
            Model model,
            @AuthenticationPrincipal UserPrincipal self) {

        List<Author> authors = authorService.searchAuthors(query);
        model.addAttribute("authors", authors);
        model.addAttribute("q", query);
        model.addAttribute("count", authors.size());

        if (authors.isEmpty()) {
            model.addAttribute("message", "Nessun autore trovato per \"" + query + "\"");
        }

        return viewResolver.viewFor("authors", self);
    }
}
