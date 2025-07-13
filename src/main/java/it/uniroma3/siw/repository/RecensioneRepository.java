package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import java.util.Optional;


public interface RecensioneRepository extends CrudRepository<Recensione,Long> {

    boolean exiexistsByUserAndBook(User user, Book book);

    Optional<Recensione> findByUserAndBook(User user, Book book);

    Iterable<Recensione> findByUser(User user);
    
    Iterable<Recensione> findByBook(Book book);

}