package it.uniroma3.siw.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Min(0)
    private Long edizione;

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Immagine> immagini;
    //Non basta solo il cascade MERGE, ma serve anche l'orphanRemoval=true per eliminare le immagini quando vengono eliminate dal libro
    //CascadeType.MERGE aggiorna solo le entità figlie esistenti
    //orphanRemoval=true elimina le entità figlie che non sono più referenziate dal libro

    @ManyToMany(mappedBy = "libri")
    private List<Autore> autori;

    @OneToMany(mappedBy = "libro", cascade = CascadeType.REMOVE, orphanRemoval = true)
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

    public List<Autore> getAutori() {
        return autori;
    }

    public void setAutori(List<Autore> autori) {
        this.autori = autori;
    }

    public List<Recensione> getRecensioni() {
        return recensioni;
    }

    public void setRecensioni(List<Recensione> recensioni) {
        this.recensioni = recensioni;
    }

    public Long getEdizione() {
        return edizione;
    }

    public void setEdizione(Long edizione) {
        this.edizione = edizione;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((titolo == null) ? 0 : titolo.hashCode());
        result = prime * result + ((edizione == null) ? 0 : edizione.hashCode());
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
        if (edizione == null) {
            if (other.edizione != null)
                return false;
        } else if (!edizione.equals(other.edizione))
            return false;
        return true;
    }
}
