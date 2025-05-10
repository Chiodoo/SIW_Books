package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Immagine;
import it.uniroma3.siw.repository.ImmagineRepository;

@Service
public class ImmagineService {

    @Autowired
    private ImmagineRepository immagineRepository;

    public void save(Immagine immagine) {
        this.immagineRepository.save(immagine);
    }

}
