package it.uniroma3.siw.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.service.UserService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired UserService userService;

    @GetMapping("/user")
    public String showUsers(Model model) {
        model.addAttribute("users", this.userService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/user/{id}")
    public String showUser(@PathVariable("id") Long id,Model model) {
            model.addAttribute("user", this.userService.getUserById(id));
            return "admin/user";
    }

}
