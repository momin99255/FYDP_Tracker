package com.momin.fydp_sync.controller;

import com.momin.fydp_sync.entity.User;
import com.momin.fydp_sync.entity.UserProfile;
import com.momin.fydp_sync.enums.Designation;
import com.momin.fydp_sync.repository.UserProfileRepository;
import com.momin.fydp_sync.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final UserProfileRepository userProfileRepository;

    private static final String UPLOAD_DIR = "uploads/profile-pics/";

    @GetMapping
    public String viewProfile(Model model, Principal principal) {
        User currentUser = userService.getUserByUsername(principal.getName());
        UserProfile profile = userService.getOrCreateProfile(currentUser);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("profile", profile);
        model.addAttribute("editMode", false);
        model.addAttribute("isOwnProfile", true);
        model.addAttribute("isLoggedIn", true);
        return "my-profile";
    }

    @GetMapping("/edit")
    public String editProfile(Model model, Principal principal) {
        User currentUser = userService.getUserByUsername(principal.getName());
        UserProfile profile = userService.getOrCreateProfile(currentUser);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("profile", profile);
        model.addAttribute("editMode", true);
        model.addAttribute("isOwnProfile", true);
        model.addAttribute("isLoggedIn", true);
        return "my-profile";
    }

    @GetMapping("/{username}")
    public String viewOtherProfile(@PathVariable String username, Model model, Principal principal) {

        if (principal != null && principal.getName().equalsIgnoreCase(username)) {
            return "redirect:/profile";
        }

        User targetUser = userService.getUserByUsername(username);
        if (targetUser == null) {
            return "redirect:/student/dashboard";
        }
        UserProfile profile = userService.getOrCreateProfile(targetUser);

        model.addAttribute("currentUser", targetUser);
        model.addAttribute("profile", profile);
        model.addAttribute("editMode", false);
        model.addAttribute("isOwnProfile", false);
        model.addAttribute("isLoggedIn", true);
        return "my-profile";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam(value = "profilePictureFile", required = false) MultipartFile file,
                                @RequestParam(value = "contactNo", required = false) String contactNo,
                                @RequestParam(value = "university", required = false) String university,
                                @RequestParam(value = "department", required = false) String department,
                                @RequestParam(value = "batch", required = false) String batch,
                                @RequestParam(value = "designation", required = false) Designation designation,
                                Principal principal,
                                RedirectAttributes redirectAttributes) throws IOException {

        User currentUser = userService.getUserByUsername(principal.getName());
        UserProfile profile = userService.getOrCreateProfile(currentUser);

        profile.setContactNo(contactNo);
        profile.setUniversity(university);
        profile.setDepartment(department);
        profile.setBatch(batch);
        profile.setDesignation(designation);

        if (file != null && !file.isEmpty()) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String original = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "photo");
            String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
            String filename = "user-" + currentUser.getId() + "-" + System.currentTimeMillis() + ext;

            Path target = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            profile.setProfilePicture("/uploads/profile-pics/" + filename);
        }

        userProfileRepository.save(profile);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/profile";
    }
}