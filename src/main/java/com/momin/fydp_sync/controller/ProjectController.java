package com.momin.fydp_sync.controller;

import com.momin.fydp_sync.model.*;
import com.momin.fydp_sync.repository.ProjectMemberRepository;
import com.momin.fydp_sync.repository.ProjectRepository;
import com.momin.fydp_sync.repository.WorkspaceRepository;
import com.momin.fydp_sync.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final WorkspaceRepository workspaceRepository; // 🔥 Workspace save korar jonno add kora holo
    private final UserService userService;

    @PostMapping("/create")
    public String createProject(@RequestParam("title") String title, Principal principal) {
        String username = principal.getName();
        User currentUser = userService.getUserByUsername(username);

        // 1. Prothome project save kore ID niye asha
        Project newProject = new Project();
        newProject.setTitle(title);
        Project savedProject = projectRepository.save(newProject);

        // 2. Creator ke explicitly member table-e save kora
        ProjectMember creatorMember = new ProjectMember();
        creatorMember.setUser(currentUser);
        creatorMember.setProject(savedProject);
        creatorMember.setRole(ProjectRole.CREATOR);
        projectMemberRepository.save(creatorMember);

        if (currentUser.getRole().name().equals("STUDENT")) {
            return "redirect:/student/dashboard";
        } else {
            return "redirect:/supervisor/dashboard";
        }
    }

    @GetMapping("/{id}")
    public String viewProjectDetails(@PathVariable("id") Long id, Model model, Principal principal) {
        String username = principal.getName();
        User currentUser = userService.getUserByUsername(username);

        Project project = projectRepository.findById(id).orElseThrow();

        boolean isCreator = project.getMembers().stream()
                .anyMatch(m -> m.getUser().getUsername().equals(username) && m.getRole() == ProjectRole.CREATOR);

        boolean isSupervisor = currentUser.getRole().name().equals("SUPERVISOR");
        boolean hasPrivilege = isCreator || isSupervisor;

        model.addAttribute("project", project);
        model.addAttribute("hasPrivilege", hasPrivilege);

        return "project_details";
    }

    @PostMapping("/{id}/add-member")
    public String addMember(@PathVariable("id") Long projectId,
                            @RequestParam("username") String username,
                            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        User userToAdd = userService.getUserByUsername(username);

        // Jodi user database-e na thake
        if (userToAdd == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "User does not exist");
            redirectAttributes.addFlashAttribute("failedUsername", username); // Input theke jate muche na jay
            return "redirect:/projects/" + projectId;
        }

        boolean alreadyMember = project.getMembers().stream()
                .anyMatch(m -> m.getUser().getUsername().equals(username));

        if (alreadyMember) {
            redirectAttributes.addFlashAttribute("errorMessage", "User is already a member");
            redirectAttributes.addFlashAttribute("failedUsername", username);
        } else {
            ProjectMember newMember = new ProjectMember();
            newMember.setUser(userToAdd);
            newMember.setProject(project);
            newMember.setRole(ProjectRole.MEMBER);
            projectMemberRepository.save(newMember);

            redirectAttributes.addFlashAttribute("successMessage", "Member was added successfully");
        }

        return "redirect:/projects/" + projectId;
    }

    // 🔥 Add Workspace Feature (Aager code-er sathe match kore add kora holo)
    @PostMapping("/{id}/add-workspace")
    public String addWorkspace(@PathVariable("id") Long projectId,
                               @RequestParam("title") String title,
                               @RequestParam("category") WorkspaceCategory category) {
        Project project = projectRepository.findById(projectId).orElseThrow();

        Workspace workspace = new Workspace();
        workspace.setTitle(title);
        workspace.setCategory(category);
        workspace.setProject(project);

        workspaceRepository.save(workspace);

        return "redirect:/projects/" + projectId;
    }
}