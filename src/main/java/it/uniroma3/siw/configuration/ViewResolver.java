package it.uniroma3.siw.configuration;

import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;

import org.springframework.stereotype.Component;

import it.uniroma3.siw.security.UserPrincipal;

@Component
public class ViewResolver {

    public String viewFor(String templateBase, UserPrincipal self) {
        boolean isAdmin = self != null && ADMIN_ROLE.equals(self.getRole());
        return (isAdmin ? "admin/" : "") + templateBase;
    }
}
