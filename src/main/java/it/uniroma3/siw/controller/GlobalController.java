package it.uniroma3.siw.controller;

import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import it.uniroma3.siw.security.UserPrincipal;

@ControllerAdvice
public class GlobalController {

    @ModelAttribute("userDetails")
    public CurrentUserDTO currentUser(@AuthenticationPrincipal UserPrincipal self) {
        if (self == null) return null;

        //Se l'utente è OAuth2, usa il displayName, altrimenti usa il nome utente
        return new CurrentUserDTO(
            self.isOAuth2() ? self.getDisplayName() : self.getUsername(),
            self.isOAuth2(),
            ADMIN_ROLE.equals(self.getRole()),
            self.getUserId()
        );
    }
}
