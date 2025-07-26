package it.uniroma3.siw.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.CredentialsService;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CredentialsService credentialsService;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Optional<Credentials> opt = credentialsService.findByUsername(username);
        Credentials cred = opt.orElseThrow(() ->
            new UsernameNotFoundException("User not found: "+username)
        );
        return new UserPrincipal(cred);
    }
}
