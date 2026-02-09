package com.hawkins.schengen.web;

import com.hawkins.schengen.user.PasswordResetService;
import com.hawkins.schengen.user.UserService;
import com.hawkins.schengen.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;
    private final PasswordResetService resetService;

    public AuthController(UserService userService, PasswordResetService resetService) {
        this.userService = userService;
        this.resetService = resetService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegisterRequest("", "", ""));
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute @Valid RegisterRequest form,
                                 BindingResult binding,
                                 Model model) {
        if (binding.hasErrors()) return "register";
        try {
            userService.register(form);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("registerError", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPasswordSubmit(@RequestParam String usernameOrEmail, Model model,
                                       @RequestHeader(value = "X-Forwarded-For", required = false) String xff,
                                       @RequestHeader(value = "X-Real-IP", required = false) String xri,
                                       jakarta.servlet.http.HttpServletRequest req) {
        String client = (xff != null && !xff.isBlank()) ? xff.split(",")[0].trim() : (xri != null ? xri : req.getRemoteAddr());
        resetService.requestReset(usernameOrEmail, client);
        model.addAttribute("ok", "If the account exists, a reset link has been sent.");
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPasswordSubmit(@RequestParam String token,
                                      @RequestParam String password,
                                      Model model) {
        try {
            resetService.reset(token, password);
            return "redirect:/login?reset";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("token", token);
            model.addAttribute("err", ex.getMessage());
            return "reset-password";
        }
    }
}
