package it.uniroma3.siw.controller.admin;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            @RequestParam("authorImage") MultipartFile authorImage,
            Model model) throws IOException {

        // 1) validazione bean
        if (bindingResult.hasErrors()) {
            model.addAttribute("allBooks", bookService.getAllBooks());
            return "admin/formNewAuthor";
        }

        Author savedAuthor = authorService.createAuthorWithBooksAndImage(author, bookIds, authorImage);

        return "redirect:/author/" + savedAuthor.getId();
    }

    @DeleteMapping("/deleteAuthor/{id}")
    public String deleteAuthor(@PathVariable Long id,RedirectAttributes ra) {
        boolean removed = authorService.deleteAuthorWithImage(id);
        if (removed) {
            ra.addFlashAttribute("success", "Autore e immagine eliminati con successo.");
        } else {
            ra.addFlashAttribute("error", "Autore non trovato, impossibile eliminare.");
        }
        return "redirect:/authors";
    }
}

