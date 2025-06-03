package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import it.uniroma3.siw.security.AuthorizationService;
import it.uniroma3.siw.service.UserService;

@Controller
public class UserController {

    @Autowired UserService userService;

    @Autowired
	private AuthorizationService authorizationService;

    @GetMapping("/user")
    public String showUsers(Model model) {
        if(this.authorizationService.isAdmin()){
            model.addAttribute("users", this.userService.getAllUsers());
            return "admin/users";
        }
        return "index";
    }

    @GetMapping("/user/{id}")
    public String showUser(@PathVariable("id") Long id,Model model) {
        if(this.authorizationService.isAdmin()){
            model.addAttribute("user", this.userService.getUserById(id));
            return "admin/user";
        }
        return "index";
    }

}
