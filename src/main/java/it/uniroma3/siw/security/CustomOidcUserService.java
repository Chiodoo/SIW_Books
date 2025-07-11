package it.uniroma3.siw.security;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;

@Service
public class CustomOidcUserService extends OidcUserService {

    @Autowired
    private UserService userService;

    @Autowired
    private CredentialsService credentialsService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        // 1) Prendo l'utente OIDC “puro”
        OidcUser oidcUser = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "google"
        Map<String,Object> attrs = oidcUser.getAttributes();

        // 2) Identificativo univoco (email o sub)
        String email = (String) attrs.get("email");
        String usernameKey = (email != null) 
            ? email 
            : registrationId + "_" + attrs.get("sub").toString();

        // 3) Cerco/creo Credentials + User
        Optional<Credentials> opt = credentialsService.findByUsername(usernameKey);
        Credentials cred;
        User user;
        if (opt.isPresent()) {
            cred = opt.get();
            user = cred.getUser();
        } else {
            user = new User();
            // split name
            if (attrs.get("name") != null) {
                String[] parts = ((String)attrs.get("name")).split(" ",2);
                user.setName(parts[0]);
                if (parts.length>1) user.setSurname(parts[1]);
            }
            user.setEmail(email!=null ? email : usernameKey+"@oauth");
            userService.saveUser(user);

            cred = new Credentials();
            cred.setUsername(usernameKey);
            cred.setPassword(UUID.randomUUID().toString());
            cred.setRole(Credentials.DEFAULT_ROLE);
            cred.setUser(user);
            credentialsService.saveCredentials(cred);
        }

        // 4) Ricostruisco un OidcUser con le stesse authorities del token
        return new DefaultOidcUser(
            List.of(new SimpleGrantedAuthority(cred.getRole())),
            oidcUser.getIdToken(),
            oidcUser.getUserInfo()
        );
    }
}
