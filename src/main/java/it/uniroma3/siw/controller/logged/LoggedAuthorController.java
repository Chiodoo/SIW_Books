package it.uniroma3.siw.controller.logged;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.service.AuthorService;

@Controller
@RequestMapping("/logged")
@PreAuthorize("isAuthenticated()")
public class LoggedAuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping("/authors")
    public String getAuthors(Model model) {
        model.addAttribute("authors", authorService.getAllAuthors());
        return "logged/authors";
    }

    @GetMapping("/author/{id}")
    public String getAuthor(@PathVariable("id") Long id, Model model) {
        Author author = authorService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Autore non trovato: " + id));
        model.addAttribute("author", author);
        return "logged/author";
    }
}
