package it.uniroma3.siw.controller.logged;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.security.UserPrincipal;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;


@Controller
@RequestMapping("/logged")
@PreAuthorize("isAuthenticated()")
public class LoggedUserController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private SecurityContextLogoutHandler logoutHandler;


    @Autowired
    private UserService userService;

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/account")
    public String getUserAccount(@AuthenticationPrincipal UserPrincipal self, Model model) {
        Long userId = self.getUserId();

        if(userId == null) {
            return "redirect:/login"; // Se non c'è un utente autenticato, reindirizza al login
        }

        User user = this.userService.getUserById(userId);

        user.getRecensioni().size(); // Forza il caricamento delle recensioni

        model.addAttribute("user", user);

        return "logged/account";
    }

    @GetMapping("/account/edit")
    public String editUserForm(@AuthenticationPrincipal UserPrincipal self, Model model, RedirectAttributes redirectAttrs) {

        if(self.isOAuth2()) {
            redirectAttrs.addFlashAttribute("error", "Non puoi modificare il profilo OAuth2");
            return "redirect:/logged/account";
        }

        Long userId = self.getUserId();
        User user = this.userService.getUserById(userId);
        Credentials credentials = this.credentialsService.getCredentials(userId);
        // svuoto la password per il form (caso di cambio)
        credentials.setPassword(null);
        model.addAttribute("user", user);
        model.addAttribute("credentials", credentials);
        return "logged/formEditUser";
    }

    @PutMapping("/account/edit")
    public String updateUserAccount(
            @AuthenticationPrincipal UserPrincipal self,
            @Valid User user,
            BindingResult uErr,
            @Valid Credentials credentials,
            BindingResult cErr,
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            Model model,
            RedirectAttributes redirectAttrs
    ) throws IOException {

        Long userId = self.getUserId();

        if (uErr.hasErrors() || cErr.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("credentials", credentials);
            return "logged/formEditUser";
        }

        Credentials existing = credentialsService.findByUserId(userId).orElseThrow();
        if (!passwordEncoder.matches(currentPassword, existing.getPassword())) {
            redirectAttrs.addFlashAttribute("error", "Password corrente non corretta");
            return "redirect:/logged/account/edit";
        }

        userService.updateUserAccount(
            userId,
            user.getName(),
            user.getSurname(),
            user.getEmail(),
            user.getBirth(),
            credentials.getUsername(),
            credentials.getPassword(),
            profileImage
        );

        // Forza logout per invalidare le vecchie credenziali nella sessione
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logoutHandler.logout(request, response, auth);
        SecurityContextHolder.clearContext();

        redirectAttrs.addFlashAttribute("success", "Profilo aggiornato con successo, effettua nuovamente l'accesso!");
        return "redirect:/login";
    }
    
}
