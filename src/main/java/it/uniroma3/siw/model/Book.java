package it.uniroma3.siw.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Book{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String titolo;

    @Max(2025)
    private Long annoPubblicazione;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Immagine> immagini;
    //Non basta solo il cascade MERGE, ma serve anche l'orphanRemoval=true per eliminare le immagini quando vengono eliminate dal libro
    //CascadeType.MERGE aggiorna solo le entità figlie esistenti
    //orphanRemoval=true elimina le entità figlie che non sono più referenziate dal libro


    @ManyToMany( cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
    @JoinTable(name = "book_author",joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
    private Set<Author> authors = new HashSet<>();


    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch= FetchType.LAZY, orphanRemoval = true)
    private List<Recensione> recensioni;

    //===================================METODI=========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public Long getAnnoPubblicazione() {
        return annoPubblicazione;
    }

    public void setAnnoPubblicazione(Long annoPubblicazione) {
        this.annoPubblicazione = annoPubblicazione;
    }

    public List<Immagine> getImmagini() {
        return immagini;
    }

    public void setImmagini(List<Immagine> immagini) {
        this.immagini = immagini;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public List<Recensione> getRecensioni() {
        return recensioni;
    }

    public void setRecensioni(List<Recensione> recensioni) {
        this.recensioni = recensioni;
    }

    public void addAuthor(Author author) {
        if(author != null && !this.authors.contains(author)) {
            this.authors.add(author);
            if(!author.getBooks().contains(this)) {
                author.getBooks().add(this);
            }
        }
    }

    public void removeAuthor(Author author) {
        if(author != null && this.authors.contains(author)) {
            this.authors.remove(author);
            author.getBooks().remove(this);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((titolo == null) ? 0 : titolo.hashCode());
        result = prime * result + ((annoPubblicazione == null) ? 0 : annoPubblicazione.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Book other = (Book) obj;
        if (titolo == null) {
            if (other.titolo != null)
                return false;
        } else if (!titolo.equals(other.titolo))
            return false;
        if (annoPubblicazione == null) {
            if (other.annoPubblicazione != null)
                return false;
        } else if (!annoPubblicazione.equals(other.annoPubblicazione))
            return false;
        return true;
    }
}
