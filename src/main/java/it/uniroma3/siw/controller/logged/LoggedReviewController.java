package it.uniroma3.siw.controller.logged;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.service.BookService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequestMapping("/logged")
@PreAuthorize("hasRole('DEFAULT')")
public class LoggedReviewController {

    @Autowired BookService bookService;

    @GetMapping("/formNewReview/{id}")
    public String formNewReview(@PathVariable("id") Long id, Model model) {
        model.addAttribute("book", this.bookService.findById(id));
        return "logged/formNewReview";
    }
    
}
