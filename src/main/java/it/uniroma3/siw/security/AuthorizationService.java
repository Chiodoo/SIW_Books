package it.uniroma3.siw.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.CredentialsService;

@Service
public class AuthorizationService {

    @Autowired
    private CredentialsService credentialsService;

    /**
     * Verifica se l'utente autenticato ha il ruolo ADMIN.
     * Gli utenti OAuth2 non sono salvati nel DB e quindi sono trattati come utenti normali.
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        if (authentication instanceof OAuth2AuthenticationToken) {
            // Utente OAuth2 → trattato come utente normale, non admin
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            Credentials credentials = credentialsService.getCredentials(username);
            return credentials != null && Credentials.ADMIN_ROLE.equals(credentials.getRole());
        }

        return false;
    }

}
