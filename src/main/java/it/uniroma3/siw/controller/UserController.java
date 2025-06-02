package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import it.uniroma3.siw.service.UserService;

@Controller
public class UserController {

    @Autowired UserService userService;

    @GetMapping("/user")
    public String showUsers(Model model) {
        model.addAttribute("users", this.userService.getAllUsers());
        return "users";
    }

    @GetMapping("/user/{id}")
    public String showUser(@PathVariable("id") Long id,Model model) {
        model.addAttribute("user", this.userService.getUserById(id));
        return "user";
    }

}
