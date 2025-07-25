package it.uniroma3.siw.controller.logged;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.security.SecurityUtils;
import it.uniroma3.siw.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/logged")
@PreAuthorize("isAuthenticated()")
public class LoggedUserController {

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private UserService userService;


    @GetMapping("/account")
    public String getUserAccount(Model model) {
        Long userId = securityUtils.getCurrentUser().getId();

        if(userId == null) {
            return "redirect:/login"; // Se non c'è un utente autenticato, reindirizza al login
        }

        User user = this.userService.getUserById(userId);

        user.getRecensioni().size(); // Forza il caricamento delle recensioni

        model.addAttribute("user", user);

        return "logged/account";
    }
    
}
