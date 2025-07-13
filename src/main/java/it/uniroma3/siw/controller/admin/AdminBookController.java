package it.uniroma3.siw.controller.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Immagine;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.util.FileUploadUtil;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookController {

    @Autowired BookService bookService;

    @GetMapping("/books")
    public String getAllBooksAdmin(Model model) {
        model.addAttribute("books", this.bookService.getAllBooks());
        return "admin/books";
    }

    @GetMapping("/book/{id}")
    public String getBookAdmin(@PathVariable("id") Long id, Model model) {
        model.addAttribute("book", this.bookService.findById(id));
        return "admin/book";
    }

    @GetMapping("/formNewBook")
    public String formNewBook(Model model) {
        model.addAttribute("book", new Book());
        return "admin/formNewBook";
    }

    @PostMapping("/book")
    public String addBook(@Valid @ModelAttribute("book") Book book,
                        BindingResult bindingResult,
                        @RequestParam("bookImages") List<MultipartFile> images,
                        Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/formNewBook";
        }

        bookService.save(book); // salva prima per generare l'ID

        List<Immagine> imageEntities = new ArrayList<>();
        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                String uploadDir = "uploads/book-images/" + book.getId();
                try {
                    FileUploadUtil.saveFile(uploadDir, fileName, image);
                    Immagine img = new Immagine();
                    img.setPath("/" + uploadDir + "/" + fileName);
                    img.setLibro(book);
                    imageEntities.add(img);
                } catch (IOException e) {
                    // gestisci errore
                }
            }
        }

        book.setImmagini(imageEntities);
        bookService.save(book); // salva di nuovo con immagini

        return "redirect:/admin/book/" + book.getId();
    }

}
