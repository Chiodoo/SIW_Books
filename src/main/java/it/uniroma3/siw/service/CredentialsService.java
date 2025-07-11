package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;

@Service
public class CredentialsService {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected CredentialsRepository credentialsRepository;

    @Transactional
    public Credentials getCredentials(Long id) {
        Optional<Credentials> result = this.credentialsRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional
    public Credentials getCredentials(String username) {
        Optional<Credentials> result = this.credentialsRepository.findByUsername(username);
        return result.orElse(null);
    }

    @Transactional(readOnly = true)
    public Optional<Credentials> findById(Long id) {
        return credentialsRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Credentials> findByUsername(String username) {
        return credentialsRepository.findByUsername(username);
    }

/**
     * Salva (o aggiorna) delle Credentials.
     * - Se non è già presente un ruolo, imposta DEFAULT_ROLE.
     * - Se la password non è già in BCrypt, la codifica.
     */
    @Transactional
    public Credentials saveCredentials(Credentials credentials) {
        // 1) Ruolo
        if (credentials.getRole() == null) {
            credentials.setRole(Credentials.DEFAULT_ROLE);
        }

        // 2) Password: se non è già un hash BCrypt (inizia con "$2"), allora codificala
        String pwd = credentials.getPassword();
        if (pwd != null && !pwd.startsWith("$2")) {
            credentials.setPassword(passwordEncoder.encode(pwd));
        }

        return credentialsRepository.save(credentials);
    }
}
