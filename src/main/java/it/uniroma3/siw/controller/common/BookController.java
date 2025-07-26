package it.uniroma3.siw.controller.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import it.uniroma3.siw.configuration.ViewResolver;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.security.UserPrincipal;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.RecensioneService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


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
        model.addAttribute("books", bookService.getAllBooks());
        return viewResolver.viewFor("books", self);
    }

    // 2) Endpoint di ricerca per titolo
    @GetMapping("/books/search")
    public String searchBooks(
            @RequestParam(value="q",   required=false) String query,
            @RequestParam(value="anno",required=false) Integer anno,
            Model model,
            @AuthenticationPrincipal UserPrincipal self) {

        List<Book> books = bookService.search(query, anno);
        model.addAttribute("books", books);
        model.addAttribute("count", books.size());
        model.addAttribute("q",     query);
        model.addAttribute("anno",  anno);
        if (books.isEmpty()) {
            model.addAttribute("message", "Nessun risultato per i filtri specificati");
        }
        return viewResolver.viewFor("books", self);
    }
}

