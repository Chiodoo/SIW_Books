package it.uniroma3.siw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Credentials;

public interface CredentialsRepository extends CrudRepository<Credentials, Long> {

    // Utilizza un EntityGraph per caricare anche l'entità "user" associata alle credenziali.
    // Evita il problema della lazy loading exception se "user" viene usato dopo la chiusura del contesto di persistenza.
    @EntityGraph(attributePaths = "user")
    public Optional<Credentials> findByUsername(String username);

    // Stessa logica: carica l'entità "user" insieme alle credenziali trovate tramite l'id dell'utente.
    @EntityGraph(attributePaths = "user")
    public Optional<Credentials> findByUserId(Long userId);

    public void deleteByUser_Id(Long userId);
}
