package it.uniroma3.siw.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import it.uniroma3.siw.security.UserPrincipal;

@ControllerAdvice
public class GlobalController {

    @ModelAttribute("userDetails")
    public CurrentUserDTO currentUser(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) return null;
        
    
    //Se l'utente è OAuth2, usa il displayName, altrimenti usa il nome utente
    return new CurrentUserDTO(
        principal.isOAuth2() ? principal.getDisplayName() : principal.getUsername(),
        principal.isOAuth2()
    );
    }
}
