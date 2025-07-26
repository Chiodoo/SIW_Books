package it.uniroma3.siw.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.service.UserService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired UserService userService;

    @GetMapping("/user")
    public String showUsers(Model model) {
        model.addAttribute("users", this.userService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/user/{id}")
    public String showUser(@PathVariable("id") Long id,Model model) {
            model.addAttribute("user", this.userService.getUserById(id));
            return "admin/user";
    }

    @DeleteMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Long id,
                             RedirectAttributes ra) {
        boolean ok = userService.deleteUserWithImage(id);
        if (ok)
            ra.addFlashAttribute("success", "Utente eliminato con successo.");
        else
            ra.addFlashAttribute("error", "Utente non trovato.");

        return "redirect:/admin/user";
    }

}
