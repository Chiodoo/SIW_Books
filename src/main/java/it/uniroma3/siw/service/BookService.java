package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.uniroma3.siw.service.storage.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Immagine;
import it.uniroma3.siw.repository.BookRepository;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ImageStorageService imageStorageService;

    public void save(Book libro) {
        this.bookRepository.save(libro);
    }

    public Book findById(Long id) {
        return this.bookRepository.findById(id).orElse(null);
    }

    public Iterable<Book> getAllBooks() {
        return this.bookRepository.findAll();
    }

    public Book saveWithImages(Book book, List<MultipartFile> images) throws IOException {
    book = this.bookRepository.save(book);
    List<Immagine> imgs = new ArrayList<>();
    for (MultipartFile f : images) {
        if (!f.isEmpty()) {
            String p = this.imageStorageService.store(f, "books/" + book.getId());
            Immagine img = new Immagine();
            img.setPath(p);
            img.setLibro(book);
            imgs.add(img);
        }
    }
    book.setImmagini(imgs);
    return this.bookRepository.save(book);
}

}
