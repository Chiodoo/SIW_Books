package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.repository.AuthorRepository;


@Service
public class AutoreService {

    @Autowired
    private AuthorRepository authorRepository;

    public void save(Author author) {
        this.authorRepository.save(author);
    }

}
