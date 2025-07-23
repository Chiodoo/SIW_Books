package it.uniroma3.siw.controller.logged;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.security.SecurityUtils;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.RecensioneService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;



@Controller
@RequestMapping("/logged")
@PreAuthorize("isAuthenticated()")
public class LoggedReviewController {

    @Autowired BookService bookService;

    @Autowired RecensioneService recensioneService;
    
    @Autowired SecurityUtils securityUtil;

    //Mostra il form solo se l'utente è autenticato e non ha già recensito il libro
    @GetMapping("/formNewReview/{id}")
    public String formNewReview(@PathVariable("id") Long id, Model model,RedirectAttributes redirectAttr) {
        Book book = this.bookService.findById(id);
        User user = securityUtil.getCurrentUser();

        if (user == null) {
            redirectAttr.addFlashAttribute("errorMessage", "Devi essere autenticato.");
            return "redirect:/login";
        }

        if(this.recensioneService.hasRecensito(user, book)) {
            redirectAttr.addFlashAttribute("errorMessage", "Hai già lasciato una recensione per questo libro.");
            return "redirect:/book/" + book.getId();
        }

        model.addAttribute("book", book);
        model.addAttribute("review", new Recensione());
        return "logged/formNewReview";
    }

    //Gestisce l'invio del form per la recensione
    @PostMapping("/formNewReview/{id}")
    public String submitNewReview(@PathVariable("id") Long id ,@Valid @ModelAttribute("review") Recensione formReview, BindingResult bindingResult, Model model, RedirectAttributes redirectAttr) {

        User user = securityUtil.getCurrentUser();

        if (user == null) {
            redirectAttr.addFlashAttribute("errorMessage", "Devi essere autenticato.");
            return "redirect:/login";
        }

        Book book = bookService.findById(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("book", book);
            return "logged/formNewReview";
        }

        if(this.recensioneService.hasRecensito(user, book)) {
            redirectAttr.addFlashAttribute("errorMessage", "Hai già lasciato una recensione per questo libro.");
            return "redirect:/book/" + book.getId();
        }

        // Crea e salva la recensione associandola all'utente e al libro
        Recensione review = new Recensione();
        review.setTitolo(formReview.getTitolo());
        review.setTesto(formReview.getTesto());
        review.setVoto(formReview.getVoto());
        this.recensioneService.save(review, user, book);

        return "redirect:/book/" + id;
    }
    
}
