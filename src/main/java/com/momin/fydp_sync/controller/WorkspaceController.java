package com.momin.fydp_sync.controller;

import com.momin.fydp_sync.entity.*;
import com.momin.fydp_sync.enums.ProjectRole;
import com.momin.fydp_sync.enums.WorkspaceCategory;
import com.momin.fydp_sync.repository.WorkspaceRepository;
import com.momin.fydp_sync.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceRepository workspaceRepository;
    private final com.momin.fydp_sync.service.WorkspaceService workspaceService;
    private final UserService userService;

    @GetMapping("/{id}")
    public String viewWorkspace(@PathVariable Long id, Model model, Principal principal) {
        Workspace workspace = workspaceRepository.findById(id).orElseThrow();
        User currentUser = userService.getUserByUsername(principal.getName());

        boolean isTableType = workspace.getCategory() == WorkspaceCategory.TRAIN_MODEL
                || workspace.getCategory() == WorkspaceCategory.LITERATURE_REVIEW;

        model.addAttribute("workspace", workspace);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isTableType", isTableType);
        model.addAttribute("hasPrivilege", hasColumnPrivilege(workspace, currentUser));
        model.addAttribute("isLoggedIn", true);

        if (isTableType) {
            model.addAttribute("columns", workspaceService.getColumns(workspace));
        }
        model.addAttribute("rows", workspaceService.getRows(workspace));

        return "workspace_details";
    }

    @PostMapping("/{id}/add-column")
    public String addColumn(@PathVariable Long id, @RequestParam String name,
                            Principal principal, RedirectAttributes redirectAttributes) {
        Workspace workspace = workspaceRepository.findById(id).orElseThrow();
        User currentUser = userService.getUserByUsername(principal.getName());

        if (!hasColumnPrivilege(workspace, currentUser)) {
            throw new AccessDeniedException("Only the project creator or a supervisor can add columns.");
        }

        workspaceService.addColumn(workspace, name);
        redirectAttributes.addFlashAttribute("successMessage", "Column added.");
        return "redirect:/workspaces/" + id;
    }

    @PostMapping("/{id}/add-row")
    public String addRow(@PathVariable Long id, HttpServletRequest request,
                         Principal principal, RedirectAttributes redirectAttributes) {
        Workspace workspace = workspaceRepository.findById(id).orElseThrow();
        User currentUser = userService.getUserByUsername(principal.getName());

        boolean isTableType = workspace.getCategory() == WorkspaceCategory.TRAIN_MODEL
                || workspace.getCategory() == WorkspaceCategory.LITERATURE_REVIEW;

        if (isTableType) {
            List<WorkspaceColumn> columns = workspaceService.getColumns(workspace);
            Map<Long, String> values = new HashMap<>();
            for (WorkspaceColumn column : columns) {
                values.put(column.getId(), request.getParameter("cell_" + column.getId()));
            }
            workspaceService.addTableRow(workspace, currentUser, values, columns);
        } else {
            workspaceService.addSimpleRow(workspace, currentUser, request.getParameter("content"));
        }

        redirectAttributes.addFlashAttribute("successMessage", "Entry added.");
        return "redirect:/workspaces/" + id;
    }

    @PostMapping("/{workspaceId}/rows/{rowId}/update")
    public String updateRow(@PathVariable Long workspaceId, @PathVariable Long rowId,
                            HttpServletRequest request, Principal principal,
                            RedirectAttributes redirectAttributes) {
        WorkspaceRow row = workspaceService.getRow(rowId);
        requireRowOwner(row, principal);

        Workspace workspace = row.getWorkspace();
        boolean isTableType = workspace.getCategory() == WorkspaceCategory.TRAIN_MODEL
                || workspace.getCategory() == WorkspaceCategory.LITERATURE_REVIEW;

        if (isTableType) {
            Map<Long, String> values = new HashMap<>();
            for (WorkspaceCellValue cell : row.getCellValues()) {
                values.put(cell.getColumn().getId(), request.getParameter("cell_" + cell.getColumn().getId()));
            }
            workspaceService.updateTableRow(row, values);
        } else {
            workspaceService.updateSimpleRow(row, request.getParameter("content"));
        }

        redirectAttributes.addFlashAttribute("successMessage", "Entry updated.");
        return "redirect:/workspaces/" + workspaceId;
    }

    @PostMapping("/{workspaceId}/rows/{rowId}/delete")
    public String deleteRow(@PathVariable Long workspaceId, @PathVariable Long rowId,
                            Principal principal, RedirectAttributes redirectAttributes) {
        WorkspaceRow row = workspaceService.getRow(rowId);
        requireRowOwner(row, principal);

        workspaceService.deleteRow(rowId);
        redirectAttributes.addFlashAttribute("successMessage", "Entry deleted.");
        return "redirect:/workspaces/" + workspaceId;
    }

    // Column add:
    private boolean hasColumnPrivilege(Workspace workspace, User currentUser) {
        Project project = workspace.getProject();
        boolean isCreator = project.getMembers().stream()
                .anyMatch(m -> m.getUser().getId() == currentUser.getId() && m.getRole() == ProjectRole.CREATOR);
        boolean isSupervisor = currentUser.getRole().name().equals("SUPERVISOR");
        return isCreator || isSupervisor;
    }

    // Row edit/delete:
    private void requireRowOwner(WorkspaceRow row, Principal principal) {
        if (!row.getAddedBy().getUsername().equalsIgnoreCase(principal.getName())) {
            throw new AccessDeniedException("You can only edit or delete entries you added yourself.");
        }
    }
}