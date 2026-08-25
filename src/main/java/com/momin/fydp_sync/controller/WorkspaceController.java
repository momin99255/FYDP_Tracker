package com.momin.fydp_sync.controller;

import com.momin.fydp_sync.model.Workspace;
import com.momin.fydp_sync.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceRepository workspaceRepository;

    // 🔥 Ekhon-er jonno shudhu ekta placeholder page - por-e egiye niye kaj korte parbi
    @GetMapping("/{id}")
    public String viewWorkspace(@PathVariable Long id, Model model) {
        Workspace workspace = workspaceRepository.findById(id).orElseThrow();
        model.addAttribute("workspace", workspace);
        model.addAttribute("isLoggedIn", true);
        return "workspace_details";
    }
}