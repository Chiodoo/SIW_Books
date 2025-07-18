package it.uniroma3.siw.controller.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import it.uniroma3.siw.service.AuthorService;


@Controller
public class AuthorController {

    @Autowired AuthorService authorService;
}
