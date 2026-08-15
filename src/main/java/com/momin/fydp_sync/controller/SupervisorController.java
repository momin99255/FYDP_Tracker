package com.momin.fydp_sync.controller;

import com.momin.fydp_sync.model.Project;
import com.momin.fydp_sync.model.User;
import com.momin.fydp_sync.repository.ProjectRepository;
import com.momin.fydp_sync.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/supervisor")
@RequiredArgsConstructor
public class SupervisorController {

    private final UserService userService;
    private final ProjectRepository projectRepository;


    @GetMapping("/dashboard")
    public String supervisorDashboard(Model model, Principal principal) {
        String username = principal.getName();
        User currentUser = userService.getUserByUsername(username);

        List<Project> supervisedProjects = projectRepository.findProjectsForDashboard(currentUser);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("projects", supervisedProjects);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUsername", username);

        return "dashboard";
    }
}
