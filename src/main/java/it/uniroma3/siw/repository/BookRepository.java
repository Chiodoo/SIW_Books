package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Book;

public interface BookRepository extends CrudRepository<Book, Long> {

    List<Book> findByTitoloContainingIgnoreCase(String query);

    List<Book> findByAnnoPubblicazione(Integer anno);
    
    List<Book> findByTitoloContainingIgnoreCaseAndAnnoPubblicazione(String q, Integer anno);

    @Query(value = "SELECT * FROM book ORDER BY RANDOM() LIMIT 3", nativeQuery = true)
    List<Book> find3RandomBooks();

}