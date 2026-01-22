package com.example.schengen.web;

import com.example.schengen.user.UserEntity;
import com.example.schengen.user.UserService;
import com.example.schengen.user.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository users;
    private final UserService userService;

    public AdminController(UserRepository users, UserService userService) {
        this.users = users;
        this.userService = userService;
    }

    @GetMapping
    public String admin(Model model) {
        model.addAttribute("users", users.findAll());
        return "admin";
    }

    @GetMapping("/users/new")
    public String showAddUserForm() {
        return "user-add";
    }

    @PostMapping("/users/new")
    public String createUser(@RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            RedirectAttributes ra) {
        try {
            userService.createUser(username, email, password, role);
            ra.addFlashAttribute("success", "User created successfully.");
            return "redirect:/admin";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/users/new";
        }
    }

    @GetMapping("/users/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        UserEntity user = users.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        model.addAttribute("user", user);
        return "user-edit";
    }

    @PostMapping("/users/{id}/edit")
    public String updateUser(@PathVariable Long id,
            @RequestParam String email,
            @RequestParam String role,
            @RequestParam(required = false, defaultValue = "false") boolean enabled,
            @RequestParam(required = false) String password,
            RedirectAttributes ra) {
        try {
            userService.updateUser(id, email, role, enabled, password);
            ra.addFlashAttribute("success", "User updated successfully.");
            return "redirect:/admin";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/users/" + id + "/edit";
        }
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        try {
            userService.deleteUser(id);
            ra.addFlashAttribute("success", "User deleted successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin";
    }
}
