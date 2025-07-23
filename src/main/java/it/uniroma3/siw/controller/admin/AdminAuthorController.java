package it.uniroma3.siw.controller.admin;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.BookService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuthorController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @GetMapping("/authors")
    public String getAuthors(Model model) {
        model.addAttribute("authors", authorService.getAllAuthors());
        return "admin/authors";
    }
    

    @GetMapping("/formNewAuthor")
    public String formNewAuthor(Model model) {

        model.addAttribute("author", new Author());
        model.addAttribute("allBooks", bookService.getAllBooks());

        return "admin/formNewAuthor";
    }

    @PostMapping("/author")
    public String saveAuthor(
            @Valid @ModelAttribute("author") Author author,
            BindingResult bindingResult,
            @RequestParam(value = "books", required = false) List<Long> bookIds,
            @RequestParam("image") MultipartFile image,
            Model model) throws IOException {

        // 1) validazione bean
        if (bindingResult.hasErrors()) {
            model.addAttribute("allBooks", bookService.getAllBooks());
            return "admin/formNewAuthor";
        }

        Author savedAuthor = authorService.createAuthorWithBooksAndImage(author, bookIds, image);

        return "redirect:/admin/author/" + savedAuthor.getId();
    }

    @GetMapping("/author/{id}")
    public String getAuthor(@PathVariable("id") Long id, Model model) {
        Author author = authorService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Autore non trovato: " + id));
        model.addAttribute("author", author);
        return "admin/author";
    }
    
}

