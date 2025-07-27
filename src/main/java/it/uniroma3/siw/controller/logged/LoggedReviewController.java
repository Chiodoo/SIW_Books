package it.uniroma3.siw.controller.logged;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.security.UserPrincipal;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.RecensioneService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
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

    //Mostra il form solo se l'utente è autenticato e non ha già recensito il libro
    @GetMapping("/formNewReview/{id}")
    public String formNewReview(@AuthenticationPrincipal UserPrincipal self,@PathVariable("id") Long id, Model model,RedirectAttributes redirectAttr) {
        Book book = this.bookService.findById(id);
        Long userId = self.getUserId();

        if (userId == null) {
            redirectAttr.addFlashAttribute("errorMessage", "Devi essere autenticato.");
            return "redirect:/login";
        }

        if(this.recensioneService.hasRecensito(userId, book)) {
            redirectAttr.addFlashAttribute("errorMessage", "Hai già lasciato una recensione per questo libro.");
            return "redirect:/book/" + book.getId();
        }

        model.addAttribute("book", book);
        model.addAttribute("review", new Recensione());
        return "logged/formNewReview";
    }

    //Gestisce l'invio del form per la recensione
    @PostMapping("/formNewReview/{id}")
    public String submitNewReview(@AuthenticationPrincipal UserPrincipal self, @PathVariable("id") Long id ,@Valid @ModelAttribute("review") Recensione formReview, BindingResult bindingResult, Model model, RedirectAttributes redirectAttr) {

        Long userId = self.getUserId();

        if (userId == null) {
            redirectAttr.addFlashAttribute("errorMessage", "Devi essere autenticato.");
            return "redirect:/login";
        }

        Book book = bookService.findById(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("book", book);
            return "logged/formNewReview";
        }

        if(this.recensioneService.hasRecensito(userId, book)) {
            redirectAttr.addFlashAttribute("errorMessage", "Hai già lasciato una recensione per questo libro.");
            return "redirect:/book/" + book.getId();
        }

        // Crea e salva la recensione associandola all'utente e al libro
        Recensione review = new Recensione();
        review.setTitolo(formReview.getTitolo());
        review.setTesto(formReview.getTesto());
        review.setVoto(formReview.getVoto());
        this.recensioneService.save(review, userId, book);

        return "redirect:/book/" + id;
    }

    /**
     * Cancella la recensione solo se l'utente loggato è l'autore.
     */

    @DeleteMapping("/deleteReview/{bookId}/{reviewId}")
    public String deleteReview(
            @PathVariable Long bookId,
            @PathVariable Long reviewId,
            RedirectAttributes redirectAttr,
            @AuthenticationPrincipal UserPrincipal self // la tua UserDetails con getUserId()
    ) {
        // 1) Recupera la recensione
        Optional<Recensione> maybeReview = recensioneService.findById(reviewId);
        if (maybeReview.isPresent()) {
            Recensione review = maybeReview.get();
            // 2) Controlla che l’utente loggato ne sia l’autore
            if (review.getUser().getId().equals(self.getUserId())) {
                // 3) Elimina
                recensioneService.deleteById(reviewId);
                // 4) Redirect alla pagina del libro
                redirectAttr.addFlashAttribute("success", "Recensione eliminata con successo.");
                return "redirect:/book/" + bookId;
            }
        }
        redirectAttr.addFlashAttribute("error", "Non puoi eliminare questa recensione.");
        // se non trova la recensione o non è autorizzato
        return "redirect:/book/" + bookId;
    }
    
}
