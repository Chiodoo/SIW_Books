package it.uniroma3.siw.controller;

import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.siw.security.UserPrincipal;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(@AuthenticationPrincipal UserPrincipal self, Model model) {
        if (self != null && ADMIN_ROLE.equals(self.getCredentialsDomain().getRole())) {
            return "admin/indexAdmin";
        }
        return "index";
    }

    @GetMapping("/success")
    public String defaultAfterLogin(@AuthenticationPrincipal UserPrincipal self, Model model) {
        if (self != null && ADMIN_ROLE.equals(self.getCredentialsDomain().getRole())) {
            return "admin/indexAdmin";
        }
        return "index";
    }
}