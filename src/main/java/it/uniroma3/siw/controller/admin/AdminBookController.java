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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.BookService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @GetMapping("/formNewBook")
    public String formNewBook(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("allAuthors", this.authorService.getAllAuthors());
        return "admin/formNewBook";
    }

    @PostMapping("/book")
    public String addBook(
            @Valid @ModelAttribute("book") Book book,
            BindingResult bindingResult,
            @RequestParam("bookImages") List<MultipartFile> images,
            @RequestParam(value = "authors", required = false) List<Long> authorIds,
            Model model
    ) throws IOException {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allAuthors", this.authorService.getAllAuthors());
            return "admin/formNewBook";
        }

        // Usa il service che gestisce salvataggio del book, delle immagini e dei relativi autori
        Book savedBook = this.bookService.createBookWithAuthorsAndImages(book, authorIds, images);

        return "redirect:/book/" + savedBook.getId();
    }
}
