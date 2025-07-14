package it.uniroma3.siw.controller.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.RecensioneService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class BookController {

    @Autowired BookService bookService;

    @Autowired RecensioneService recensioneService;

    @GetMapping("/book/{id}")
    public String getBook(@PathVariable("id") Long id, Model model) {
        model.addAttribute("book", this.bookService.findById(id));
        model.addAttribute("reviews", this.recensioneService.findByBookId(id));
        return "book";
    }

    @GetMapping("/books")
    public String getAllBooks(Model model) {
        model.addAttribute("books", this.bookService.getAllBooks());
        return "books";
    }
}
