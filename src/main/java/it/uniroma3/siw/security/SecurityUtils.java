package it.uniroma3.siw.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.UserService;

@Service
public class SecurityUtils {

    @Autowired
    private UserService userService;

    /**
     * Restituisce l’entity User dell’utente autenticato, o null se non autenticato
     * o se non esiste un record corrispondente.
     */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        String email = null;

        if (auth instanceof OAuth2AuthenticationToken oauth2) {
            // per utenti OAuth2 prendi l'email dagli attributi del token
            email = oauth2.getPrincipal().getAttribute("email");
        }
        else if (auth.getPrincipal() instanceof UserDetails ud) {
            // per utenti "classici" prendi username (di solito email)
            email = ud.getUsername();
        }

        if (email == null) {
            return null;
        }

        // usa direttamente UserService per caricare l’entity User
        Optional<User> maybeUser = userService.getByEmail(email);
        return maybeUser.orElse(null);
    }
}
