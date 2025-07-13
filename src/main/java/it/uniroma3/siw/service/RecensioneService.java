package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.RecensioneRepository;

@Service
public class RecensioneService {

    @Autowired
    private RecensioneRepository recensioneRepository;

    public void save(Recensione recensione, User user, Book book) {
        recensione.setUser(user);
        recensione.setBook(book);
        this.recensioneRepository.save(recensione);
    }

    public boolean hasRecensito(User user, Book book) {
        return this.recensioneRepository.exiexistsByUserAndBook(user, book);
    }
}
