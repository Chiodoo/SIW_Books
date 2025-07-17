package it.uniroma3.siw.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.service.BookService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuthorController {

    @Autowired
    private BookService bookService;

    @GetMapping("/formNewAuthor")
    public String formNewAuthor(Model model) {

        model.addAttribute("author", new Author());
        model.addAttribute("allBooks", bookService.getAllBooks());

        return "admin/formNewAuthor";
    }
}
