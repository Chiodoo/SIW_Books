package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import it.uniroma3.siw.service.storage.ImageStorageService;
import jakarta.persistence.EntityNotFoundException;

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
                imgs.add(img);
            }
        }
        book.setImmagini(imgs);

        // 4) Salvo di nuovo per cascata sulle immagini
        return bookRepository.save(book);
    }

    @SuppressWarnings("null")
    public List<Book> search(String query, Integer anno) {
        boolean hasQ   = (query   != null && !query.isBlank());
        boolean hasAnno= (anno!= null);
        if (hasQ && hasAnno) {
            return bookRepository.findByTitoloContainingIgnoreCaseAndAnnoPubblicazione(query.trim(), anno);
        }
        if (hasQ) {
            return bookRepository.findByTitoloContainingIgnoreCase(query.trim());
        }
        if (hasAnno) {
            return bookRepository.findByAnnoPubblicazione(anno);
        }
        // se non ho filtri, restituisco tutti i libri
        // (Trasformazione dell'iterable in una List)
        return StreamSupport
                 .stream(bookRepository.findAll().spliterator(), false)
                 .collect(Collectors.toList());
    }
    

    @Transactional
    public boolean deleteBookWithImages(Long id) {
        return bookRepository.findById(id).map(book -> {

            try {
                this.imageStorageService.deleteDirectory("books/" + id);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete image directory for book " + id, e);
            }

            // elimina record DB (cascade REMOVE si occuperà delle immagini in tabella)
            bookRepository.delete(book);
            return true;
        }).orElse(false);
    }

    @Transactional
    public Book updateBook(Long id,
                        Book bookForm,
                        List<Long> authorIds,
                        List<MultipartFile> images) throws IOException {
        // 0) null‐safe per 'images'
        List<MultipartFile> files = (images != null) ? images : Collections.emptyList();

        // 1) recupera il book esistente
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Book non trovato: " + id));

        // 2) aggiorna i campi semplici
        book.setTitolo(bookForm.getTitolo());
        book.setAnnoPubblicazione(bookForm.getAnnoPubblicazione());

        // 3) ripopola la relazione ManyToMany con gli Autori
        book.getAuthors().clear();
        if (authorIds != null) {
            for (Long aid : authorIds) {
                authorRepository.findById(aid).ifPresent(book::addAuthor);
            }
        }

        // 4) gestisci il rimpiazzo delle immagini solo se ne hai di nuove
        boolean hasNew = files.stream().anyMatch(f -> !f.isEmpty());
        if (hasNew) {
            // 4.a) cancella fisicamente la cartella
            this.imageStorageService.deleteDirectory("books/" + id);

            // 4.b) svuota la collection mantenendo la stessa List
            List<Immagine> imgs = book.getImmagini();
            imgs.clear();

            // 4.c) salva le nuove immagini
            for (MultipartFile f : files) {
                if (!f.isEmpty()) {
                    String path = imageStorageService.store(f, "books/" + id);
                    Immagine img = new Immagine();
                    img.setPath(path);
                    imgs.add(img);
                }
            }
        }

        // 5) persisti e ritorna
        return bookRepository.save(book);
    }

    public List<Book> get3RandomBooks() {
        return bookRepository.find3RandomBooks();
    }

}
