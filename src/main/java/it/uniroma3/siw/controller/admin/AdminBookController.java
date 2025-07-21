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
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.RecensioneService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private RecensioneService recensioneService;

    @Autowired
    private AuthorService authorService;

    @GetMapping("/books")
    public String getAllBooksAdmin(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "admin/books";
    }

    @GetMapping("/book/{id}")
    public String getBookAdmin(@PathVariable("id") Long id, Model model) {
        model.addAttribute("book", bookService.findById(id));
        model.addAttribute("reviews", this.recensioneService.findByBookId(id));
        return "admin/book";
    }

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
            return "admin/formNewBook";
        }

        if (authorIds != null) {
            for (Long authorId : authorIds) {
                Author author = authorService.findById(authorId).orElse(null);
                if (author != null) {
                    book.addAuthor(author);
                }
            }
        }



        // Usa il service che gestisce salvataggio del book e delle immagini
        Book savedBook = bookService.saveWithImages(book, images);

        return "redirect:/admin/book/" + savedBook.getId();
    }
}
