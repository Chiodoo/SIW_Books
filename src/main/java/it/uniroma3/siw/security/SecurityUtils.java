package it.uniroma3.siw.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;

@Service
public class SecurityUtils {

    @Autowired
    private CredentialsService credentialsService;

    /**
     * Restituisce il domain User dell’utente autenticato, o null
     * se non autenticato o anonymous.
     */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 1) Non autenticato o anonimo → null
        if (auth == null
         || !auth.isAuthenticated()
         || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        // 2) Determino la "usernameKey" a seconda del principal
        String usernameKey = null;
        Object principal = auth.getPrincipal();

        if (principal instanceof OAuth2AuthenticationToken oauth2) {
            usernameKey = oauth2.getPrincipal().getAttribute("email");
        }
        else if (principal instanceof OidcUser oidc) {
            usernameKey = oidc.getEmail(); // o oidc.getAttribute("email")
        }
        else if (principal instanceof UserDetails ud) {
            usernameKey = ud.getUsername();
        }

        // 3) Se non ho ricavato nulla → null
        if (usernameKey == null || usernameKey.isBlank()) {
            return null;
        }

        // 4) Cerco le Credentials e ne ricavo il User associato
        return credentialsService.findByUsername(usernameKey)
                                 .map(Credentials::getUser)
                                 .orElse(null);
    }
}
