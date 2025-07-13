package it.uniroma3.siw.controller.common;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.security.AuthorizationService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import it.uniroma3.siw.util.FileUploadUtil;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private AuthorizationService authorizationService;
	
	@GetMapping(value = "/register") 
	public String showRegisterForm (Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("credentials", new Credentials());
		return "formRegisterUser";
	}

	@PostMapping(value = { "/register" })
	public String registerUser(@Valid @ModelAttribute("user") User user,
							BindingResult userBindingResult,
							@Valid @ModelAttribute("credentials") Credentials credentials,
							BindingResult credentialsBindingResult,
							@RequestParam("profileImage") MultipartFile profileImage,
							Model model){

		// se user e credential hanno entrambi contenuti validi, memorizza User e Credentials nel DB
		if (!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
			userService.saveUser(user);
			credentials.setUser(user);
			credentialsService.saveCredentials(credentials);

			if (!profileImage.isEmpty()) {
				try {
					String filename = "user-" + user.getId() + ".jpg";
					FileUploadUtil.saveFile("uploads/users", filename, profileImage);
					user.setImagePath("users/" + filename);
					userService.saveUser(user); // aggiorna con path immagine
				} catch (IOException e) {
					e.printStackTrace(); // oppure usa un logger
				}
			}

			model.addAttribute("user", user);
			return "registrationSuccessful";
		}
				return "formRegisterUser";
	}
	
	@GetMapping(value = "/login") 
	public String showLoginForm (Model model) {
		return "formLogin";
	}

	@GetMapping(value = "/") 
	public String index(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
	        return "index";
		} else if(this.authorizationService.isAdmin()) {		
			return "admin/indexAdmin.html";
		}
        return "index";
	}

    @GetMapping(value = "/success")
    public String defaultAfterLogin(Model model) {

		if(this.authorizationService.isAdmin()) {
			return "admin/indexAdmin";
		}
		return "index";
    }
}