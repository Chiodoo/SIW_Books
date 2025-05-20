# SIW-Books
## Portale per organizzare libri
Progetto svolto per l'esame di "Sistemi Informativi sul Web"<br>Sistema di gestione libri, relativi autori e recensioni

## Author
<a href="https://github.com/Chiodoo">
  <img src="https://avatars.githubusercontent.com/u/167012156?v=4" width="80">
</a>


```mermaid
classDiagram

  class Siw-Books {

  }
  
  class Libro {
    ID
    Titolo
    Anno di pubblicazione
    Edizione
  }

  class Autore {
    ID
    Nome
    Cognome
    Nazionalità
    Nascita
    Morte
  }

  class Recensione {
    ID
    Titolo
    Testo
  }

  class Immagine {
    ID
    Path
  }
```