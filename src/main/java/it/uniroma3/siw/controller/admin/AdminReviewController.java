package it.uniroma3.siw.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.service.RecensioneService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReviewController {

    @Autowired
    private RecensioneService reviewService;


    @DeleteMapping("/deleteReview/{bookId}/{reviewId}")
    public String deleteAnyReview(@PathVariable Long bookId,@PathVariable Long reviewId, RedirectAttributes redirectAttr) {
        // Se non presente, deleteById non fallisce
        reviewService.deleteById(reviewId);
        redirectAttr.addFlashAttribute("success", "Recensione eliminata con successo.");
        return "redirect:/book/" + bookId;
    }
}
