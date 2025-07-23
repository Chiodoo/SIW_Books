package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import it.uniroma3.siw.service.storage.ImageStorageService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Immagine;
import it.uniroma3.siw.repository.AuthorRepository;
import it.uniroma3.siw.repository.BookRepository;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private AuthorRepository authorRepository;

    public void save(Book libro) {
        this.bookRepository.save(libro);
    }

    public Book findById(Long id) {
        return this.bookRepository.findById(id).orElse(null);
    }

    public Iterable<Book> getAllBooks() {
        return this.bookRepository.findAll();
    }


    @Transactional
    public Book createBookWithAuthorsAndImages(Book book,
                                               List<Long> authorIds,
                                               List<MultipartFile> images) throws IOException {
        // 1) Associa gli autori (lato proprietario)
        if (authorIds != null) {
            for (Long authorId : authorIds) {
                Optional<Author> rawAuthor = authorRepository.findById(authorId);
                if (rawAuthor.isPresent()) {
                    Author author = rawAuthor.get();
                    book.addAuthor(author);
                }
            }
        }

        // 2) Salvo il book per generare l'id e
        //    per garantire che JPA registri la ManyToMany
        book = bookRepository.save(book);

        // 3) Gestione delle immagini collegate
        List<Immagine> imgs = new ArrayList<>();
        for (MultipartFile f : images) {
            if (!f.isEmpty()) {
                String path = imageStorageService.store(f, "books/" + book.getId());
                Immagine img = new Immagine();
                img.setPath(path);
                img.setLibro(book);
                imgs.add(img);
            }
        }
        book.setImmagini(imgs);

        // 4) Salvo di nuovo per cascata sulle immagini
        return bookRepository.save(book);
    }

}
