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
import jakarta.persistence.EntityNotFoundException;


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

    @Transactional
    public Author updateAuthor(Long id,
                            Author authorForm,
                            List<Long> bookIds,
                            MultipartFile image) throws IOException {

        // 1) recupera l’entità esistente
        Author author = authorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Autore non trovato: " + id));

        // 2) aggiorna campi semplici
        author.setName(authorForm.getName());
        author.setSurname(authorForm.getSurname());
        author.setNationality(authorForm.getNationality());
        author.setBirth(authorForm.getBirth());
        author.setDeath(authorForm.getDeath());

        // 3) associazioni many-to-many libri
        author.getBooks().forEach(book -> book.getAuthors().remove(author));
        author.getBooks().clear();
        if (bookIds != null) {
            for (Long bid : bookIds) {
                bookRepository.findById(bid)
                    .ifPresent(book -> {
                        author.getBooks().add(book);
                        book.getAuthors().add(author);
                    });
            }
        }

        // 4) gestione immagine (se ne arriva una nuova)
        if (image != null && !image.isEmpty()) {
            // 4.a) elimina cartella vecchia
            imageStorageService.deleteDirectory("authors/" + id);

            // 4.b) salva nuova immagine
            String path = imageStorageService.store(image, "authors/" + id);
            Immagine img = new Immagine();
            img.setPath(path);
            author.setImage(img);
        }

        // 5) persisti e ritorna
        return authorRepository.save(author);
    }

}
