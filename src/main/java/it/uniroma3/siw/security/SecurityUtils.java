package it.uniroma3.siw.security;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

        String usernameKey = null;

        // 2a) Flusso OAuth2 (compresi GitHub, Google, ecc.)
        if (auth instanceof OAuth2AuthenticationToken oauth2) {
            // estraggo gli attributi del token
            Map<String,Object> attrs = oauth2.getPrincipal().getAttributes();

            // 1° tentativo: email
            String emailAttr = (String) attrs.get("email");
            if (StringUtils.hasText(emailAttr)) {
                usernameKey = emailAttr;
            }
            // 2° tentativo (GitHub): login
            else if (attrs.get("login") instanceof String loginAttr) {
                usernameKey = loginAttr;
            }
            // fallback generico: userNameAttribute (es. id, sub, ecc.)
            else {
                usernameKey = oauth2.getName();
            }
        }
        // 2b) Flusso OIDC puro (es. Google OpenID Connect)
        else if (auth.getPrincipal() instanceof OidcUser oidc) {
            usernameKey = oidc.getEmail();
        }
        // 2c) Flusso “classico” username/password
        else if (auth.getPrincipal() instanceof UserDetails ud) {
            usernameKey = ud.getUsername();
        }

        // 3) Se non ho ricavato nulla → null
        if (!StringUtils.hasText(usernameKey)) {
            return null;
        }

        // 4) Cerco le Credentials e ne ricavo il User associato
        return credentialsService
                   .findByUsername(usernameKey)
                   .map(Credentials::getUser)
                   .orElse(null);
    }
}
