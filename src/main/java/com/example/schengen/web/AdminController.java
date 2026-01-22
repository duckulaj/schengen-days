package com.example.schengen.web;

import com.example.schengen.user.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final UserRepository users;

    public AdminController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("users", users.findAll());
        return "admin";
    }
}
