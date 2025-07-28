package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    // ————————— Lookup con cache —————————

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "credenzialiByUsername", key = "#username")
    public Optional<Credentials> findByUsername(String username) {
        return credentialsRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "credenzialiById", key = "#userId")
    public Optional<Credentials> findByUserId(Long userId) {
        return credentialsRepository.findByUserId(userId);
    }

    @Transactional
    public Credentials getCredentials(Long id) {
        return findByUserId(id).orElse(null);
    }

    @Transactional
    public Credentials getCredentials(String username) {
        return findByUsername(username).orElse(null);
    }

    // ————————— Creazione / salvataggio —————————

    /**
     * Salva (o crea) delle Credentials. Se manca il ruolo, imposta DEFAULT_ROLE;
     * se la password non è ancora in hash BCrypt, la codifica.
     *
     * NOTA: questo metodo NON invalida/aggiorna la cache,
     * perché di norma per la creazione ex-novo va bene che venga popolata
     * al primo lookup.
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = "credenzialiByUsername", key = "#credentials.username"),
        @CacheEvict(cacheNames = "credenzialiById",       key = "#result.user.id") // se hai già l’ID
    })
    public Credentials saveCredentials(Credentials credentials) {
        if (credentials.getRole() == null) {
            credentials.setRole(Credentials.DEFAULT_ROLE);
        }
        String pwd = credentials.getPassword();
        if (pwd != null && !pwd.startsWith("$2")) {
            credentials.setPassword(passwordEncoder.encode(pwd));
        }
        return credentialsRepository.save(credentials);
    }

    // ————————— Aggiornamento —————————

    /**
     * Aggiorna delle Credentials già esistenti e
     * INVALIDA le entry di cache sia per username sia per userId,
     * in modo che i next lookup vedano subito i valori aggiornati.
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = "credenzialiByUsername", key = "#oldUsername"),
        @CacheEvict(cacheNames = "credenzialiByUsername", key = "#credentials.username"),
        @CacheEvict(cacheNames = "credenzialiById", key = "#credentials.user.id")
    })
    public Credentials updateCredential(Credentials credentials, String oldUsername) {
        return saveCredentials(credentials);
    }

    
    @Transactional
    @Caching(evict = {
    @CacheEvict(cacheNames = "credenzialiByUsername", allEntries = true),
    @CacheEvict(cacheNames = "credenzialiById",       key = "#userId")
    })
    public void deleteCredentialsByUserId(Long userId) {
        credentialsRepository.deleteByUser_Id(userId);
    }
}