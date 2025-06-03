package it.uniroma3.siw.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.CredentialsService;

@Service
public class AuthorizationService {

    @Autowired
    private CredentialsService credentialsService;

    /**
     * Verifica se l'utente autenticato ha il ruolo ADMIN.
     * @return true se l'utente ha ruolo ADMIN, false altrimenti
     */
    public boolean isAdmin() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            Credentials credentials = credentialsService.getCredentials(username);

            return credentials != null && Credentials.ADMIN_ROLE.equals(credentials.getRole());
        }
        return false;
    }

    // Puoi aggiungere altri metodi di autorizzazione qui (es. isUser(), getLoggedUser(), ecc.)
}
