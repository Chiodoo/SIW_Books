package it.uniroma3.siw.security;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UserService userService;

    @Autowired
    private CredentialsService credentialsService;

    private static final String ROLE = Credentials.DEFAULT_ROLE;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1) Chiamo il delegate standard
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oauthUser = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "github", "facebook", etc.
        Map<String, Object> attributes = oauthUser.getAttributes();

        // 2) Trovo una chiave univoca per l'username
        String email = (String) attributes.get("email");
        String usernameKey;
        if (email != null && !email.isBlank()) {
            usernameKey = email;
        } else if ("github".equals(registrationId)) {
            usernameKey = (String) attributes.get("login");
        } else {
            usernameKey = registrationId + "_" + attributes.get("id").toString();
        }

        // 3) Cerco eventuali credenziali esistenti
        Optional<Credentials> existing = credentialsService.findByUsername(usernameKey);

        Credentials cred;
        User user;
        if (existing.isPresent()) {
            // login successivo: riprendo User/Credentials esistenti
            cred = existing.get();
            user = cred.getUser();
        } else {
            // primo login OAuth2: creo User + Credentials
            user = new User();
            // popoliamo sempre name e surname per rispettare @NotBlank
            if ("github".equals(registrationId)) {
                // GitHub: prova a leggere "name", altrimenti usa "login"
                String fullName = (String) attributes.get("name");
                if (fullName != null && !fullName.isBlank()) {
                    String[] parts = fullName.trim().split("\\s+", 2);
                    user.setName(parts[0]);
                    user.setSurname(parts.length > 1 ? parts[1] : "");
                } else {
                    String login = (String) attributes.get("login");
                    user.setName(login);
                    user.setSurname("");
                }
            } else {
                // altri OAuth2 “classici” senza OIDC: fallback generico
                String fullName = (String) attributes.get("name");
                if (fullName != null && !fullName.isBlank()) {
                    String[] parts = fullName.trim().split("\\s+", 2);
                    user.setName(parts[0]);
                    user.setSurname(parts.length > 1 ? parts[1] : "");
                } else if (email != null) {
                    user.setName(email);
                    user.setSurname("");
                } else {
                    user.setName(usernameKey);
                    user.setSurname("");
                }
            }

            // email
            if (email != null && !email.isBlank()) {
                user.setEmail(email);
            } else {
                user.setEmail(usernameKey + "@oauth");
            }

            userService.saveUser(user);

            cred = new Credentials();
            cred.setUsername(usernameKey);
            cred.setPassword(UUID.randomUUID().toString());
            cred.setRole(ROLE);
            cred.setUser(user);
            credentialsService.saveCredentials(cred);
        }

        // 4) Ritorno l’utente con le sue authority
        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(cred.getRole())),
                attributes,
                "id"
        );
    }
}
