package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.RecensioneRepository;
import it.uniroma3.siw.repository.UserRepository;

@Service
public class RecensioneService {

    @Autowired
    private RecensioneRepository recensioneRepository;

    @Autowired
    private UserRepository userRepository;

    public void save(Recensione recensione, Long userId, Book book) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + userId));


        //Ulteriore controllo per evitare che l'utente possa recensire lo stesso libro più volte
        if (recensioneRepository.existsByUserAndBook(user, book)) {
            throw new IllegalStateException("Hai già recensito questo libro");
        }

        recensione.setUser(user);
        recensione.setBook(book);
        user.getRecensioni().add(recensione);
        this.recensioneRepository.save(recensione);
    }

    //Metodi sovraccaricati per verificare se l'utente ha già recensito il libro
    public boolean hasRecensito(User user, Book book) {
        return this.recensioneRepository.existsByUserAndBook(user, book);
    }

    public boolean hasRecensito(Long userId, Book book) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + userId));
        return this.recensioneRepository.existsByUserAndBook(user, book);
    }

    public Iterable<Recensione> findByBookId(Long bookId) {
        return this.recensioneRepository.findByBookId(bookId);
    }

    public Optional<Recensione> findById(Long id) {
        return this.recensioneRepository.findById(id);
    }

    public void deleteById(Long id) {
        this.recensioneRepository.deleteById(id);
    }
}
