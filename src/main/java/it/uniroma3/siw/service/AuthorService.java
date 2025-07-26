package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Immagine;
import it.uniroma3.siw.repository.AuthorRepository;
import it.uniroma3.siw.repository.BookRepository;
import it.uniroma3.siw.service.storage.ImageStorageService;


@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ImageStorageService imageStorageService;

    public void save(Author author) {
        this.authorRepository.save(author);
    }

    public Optional<Author> findById(Long id) {
        return this.authorRepository.findById(id);
    }

    public Iterable<Author> getAllAuthors() {
        return this.authorRepository.findAll();
    }

    @Transactional
    public Author createAuthorWithBooksAndImage(Author author,
                                                List<Long> bookIds,
                                                MultipartFile image) throws IOException {
        author = authorRepository.save(author);
        if (image != null && !image.isEmpty()) {
            String path = imageStorageService.store(image, "authors/" + author.getId());
            Immagine immagine = new Immagine();
            immagine.setPath(path);
            author.setImage(immagine);
            author = authorRepository.save(author);
        }
        if (bookIds != null) {
            for (Long bookId : bookIds) {
                Book book = bookRepository.findById(bookId).orElse(null);
                if (book != null) {
                    book.addAuthor(author); // Associa l'autore al libro
                    bookRepository.save(book); // Salva il libro aggiornato
                }
            }
        }
        return author;
    }

    public List<Author> searchAuthors(String query) {
        // se query è null/empty, restituisco tutti
        if (query == null || query.isBlank()) {
            return (List<Author>) this.getAllAuthors();
        }
        return authorRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(query, query);
    }

    public long countAuthors() {
        return authorRepository.count();
    }

    @Transactional
    public boolean deleteAuthorWithImage(Long id) {
        return authorRepository.findById(id).map(author -> {

            // 1) cancello la cartella immagini
            try {
                imageStorageService.deleteDirectory("authors/" + author.getId());
            } catch (IOException e) {
                throw new RuntimeException("Errore durante l'eliminazione dell'immagine dell'autore", e);
            }

            // 2) rimuovo le associazioni many-to-many in memoria
            for (Book book : author.getBooks()) {
                book.getAuthors().remove(author);
            }
            author.getBooks().clear();

            // 3) cancello l'autore (le righe in book_author ora non esistono più)
            authorRepository.delete(author);
            return true;
        }).orElse(false);
    }

}
