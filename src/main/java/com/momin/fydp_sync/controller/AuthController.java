package com.momin.fydp_sync.controller;

import com.momin.fydp_sync.dto.RegisterRequest;
import com.momin.fydp_sync.model.Project;
import com.momin.fydp_sync.model.Role;
import com.momin.fydp_sync.model.User;
import com.momin.fydp_sync.repository.ProjectRepository;
import com.momin.fydp_sync.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final ProjectRepository projectRepository;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("isLoggedIn", false);
        return "login";
    }

    // 🔥 DTO update: Khali DTO pathano hocche
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new RegisterRequest("", "", "", "", "STUDENT"));
        return "register";
    }

    // 🔥 DTO update: User er bodole RegisterRequest DTO receive kora hocche
    @PostMapping("/register")
    public String registration(@Valid @ModelAttribute("user") RegisterRequest request, BindingResult bindingResult, Model model) {

        // Existing Validation: Username check
        if (userService.existsByUsername(request.username())) {
            bindingResult.rejectValue("username", "error.user", "This Username is taken");
        }

        // Existing Validation: Email check
        if (userService.existsByEmail(request.email())) {
            bindingResult.rejectValue("email", "error.user", "This email is already in use!");
        }

        // Error thakle abar register page-e pathay dibe
        if (bindingResult.hasErrors()) {
            model.addAttribute("isLoggedIn", false);
            return "register";
        }

        // 🔥 DTO theke data niye fresh User toiri kora hocche
        User newUser = new User();
        newUser.setName(request.name());
        newUser.setUsername(request.username());
        newUser.setEmail(request.email());
        newUser.setPassword(request.password());
        newUser.setRole(Role.valueOf(request.role()));

        // Tor existing service call (Ekhane password auto encode hoye jabe jodi tor service e kora thake)
        userService.registerUser(newUser);

        return "redirect:/login?registered";
    }

    @GetMapping("/dashboard")
    public String dashboardGateway(Principal principal) {
        String username = principal.getName();
        User currentUser = userService.getUserByUsername(username);

        if (currentUser.getRole().name().equals("STUDENT")) {
            return "redirect:/student/dashboard";
        } else {
            return "redirect:/supervisor/dashboard";
        }
    }
}