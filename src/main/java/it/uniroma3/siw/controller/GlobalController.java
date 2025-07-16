package it.uniroma3.siw.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalController {

    @ModelAttribute("userDetails")
    public CurrentUserDTO getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails userDetails) {
                return new CurrentUserDTO(userDetails.getUsername()); // puoi recuperare isAdmin dal DB
            }

            if (principal instanceof DefaultOAuth2User oauthUser) {
                String username = extractUsername(oauthUser);
                return new CurrentUserDTO(username);
            }
        }

        return null;
    }

    private String extractUsername(DefaultOAuth2User oauthUser) {
    // Provo a prendere username in base a chi è il provider
    String username = null;

    // Se c'è un "login" (GitHub)
    if (oauthUser.getAttributes().containsKey("login")) {
        username = (String) oauthUser.getAttributes().get("login");
    }
    // Se c'è una email (Google, Facebook, ecc.)
    else if (oauthUser.getAttributes().containsKey("email")) {
        username = (String) oauthUser.getAttributes().get("email");
    }
    // Se c'è un nome (fallback)
    else if (oauthUser.getAttributes().containsKey("name")) {
        username = (String) oauthUser.getAttributes().get("name");
    }
    else {
        // fallback generico: usa l'id (spesso presente)
        username = oauthUser.getName(); // solitamente l'id provider-unique
    }

    return username;
}

}
