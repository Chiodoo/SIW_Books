package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.repository.AuthorRepository;


@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public void save(Author author) {
        this.authorRepository.save(author);
    }

    public Optional<Author> findById(Long id) {
        return this.authorRepository.findById(id);
    }

    public Iterable<Author> getAllAuthors() {
        return this.authorRepository.findAll();
    }

}
