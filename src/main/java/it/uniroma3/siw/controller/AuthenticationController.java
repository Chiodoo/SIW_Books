package it.uniroma3.siw.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Immagine;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import it.uniroma3.siw.service.storage.ImageStorageService;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private ImageStorageService imageStorageService;
	
	@GetMapping(value = "/register") 
	public String showRegisterForm (Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("credentials", new Credentials());
		return "formRegisterUser";
	}

    @PostMapping("/register")
    public String registerUser(
        @Valid @ModelAttribute("user") User user,
        BindingResult userBindingResult,
        @Valid @ModelAttribute("credentials") Credentials credentials,
        BindingResult credentialsBindingResult,
        @RequestParam("profileImage") MultipartFile profileImage,
        RedirectAttributes redirectAttrs,
        Model model) throws IOException {

        if (userBindingResult.hasErrors() || credentialsBindingResult.hasErrors()) {
            return "formRegisterUser";
        }

        userService.saveUser(user);
        credentials.setUser(user);
        credentialsService.saveCredentials(credentials);

        String path = imageStorageService.store(profileImage, "users/" + user.getId());
        if (path != null) {
            Immagine img = new Immagine();
            img.setPath(path);
            user.setImage(img);
            userService.saveUser(user);
        }

        redirectAttrs.addAttribute("id", user.getId());
        return "redirect:/registrationSuccessful";
    }

    @GetMapping("/registrationSuccessful")
    public String showRegistrationSuccessful(@RequestParam("id") Long id, Model model) {

        /* 1. Ricarica l’utente dal DB (stateless, no sessione) */
        User user = userService.getUserById(id);            // CrudRepository::findById

        /* 2. Espone ‘user’ con lo stesso nome usato nel template */
        model.addAttribute("user", user);

        /* 3. Thymeleaf renderizza registrationSuccessful.html */
        return "registrationSuccessful";
    }

	@GetMapping(value = "/login") 
	public String showLoginForm (Model model) {
		return "formLogin";
	}
}