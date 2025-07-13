package it.uniroma3.siw.model;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
public class Recensione {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String titolo;

    @Length(max = 50000)
    private String testo;

    @NotNull
    @Min(0)
    @Max(5)
    private Double voto;

    @ManyToOne
    private Book libro;       //Libro a cui appartiene la recensione

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

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public Double getVoto() {
        return voto;
    }

    public void setVoto(Double voto) {
        this.voto = voto;
    }

    public Book getLibro() {
        return libro;
    }

    public void setLibro(Book libro) {
        this.libro = libro;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((titolo == null) ? 0 : titolo.hashCode());
        result = prime * result + ((testo == null) ? 0 : testo.hashCode());
        result = prime * result + ((voto == null) ? 0 : voto.hashCode());
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
        Recensione other = (Recensione) obj;
        if (titolo == null) {
            if (other.titolo != null)
                return false;
        } else if (!titolo.equals(other.titolo))
            return false;
        if (testo == null) {
            if (other.testo != null)
                return false;
        } else if (!testo.equals(other.testo))
            return false;
        if (voto == null) {
            if (other.voto != null)
                return false;
        } else if (!voto.equals(other.voto))
            return false;
        return true;
    }
}
