package com.momin.fydp_sync.controller;

import com.momin.fydp_sync.model.User;
import com.momin.fydp_sync.model.UserProfile;
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

    // Nijer profile - view mode
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

    // Nijer profile - edit mode
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

    // 🔥 Notun: onno kono user-er profile dekha (read-only)
    // Note: "/edit" already thakar karone Spring eta literal path hisebe age match kore,
    // tai "/profile/edit" ei method-e ashbe na, upore-r editProfile()-e jabe.
    @GetMapping("/{username}")
    public String viewOtherProfile(@PathVariable String username, Model model, Principal principal) {

        // Nijer username hole nijer profile-e pathiye dey (edit-e na, view-e)
        if (principal != null && principal.getName().equalsIgnoreCase(username)) {
            return "redirect:/profile";
        }

        User targetUser = userService.getUserByUsername(username);
        if (targetUser == null) {
            return "redirect:/student/dashboard";
        }
        UserProfile profile = userService.getOrCreateProfile(targetUser);

        model.addAttribute("currentUser", targetUser); // template-e "the profile being shown" hisebe use hocche
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
                                Principal principal,
                                RedirectAttributes redirectAttributes) throws IOException {

        // Principal theke niye user - tai keu URL-e kono trick koreo onno karo profile update korte parbe na
        User currentUser = userService.getUserByUsername(principal.getName());
        UserProfile profile = userService.getOrCreateProfile(currentUser);

        profile.setContactNo(contactNo);
        profile.setUniversity(university);
        profile.setDepartment(department);
        profile.setBatch(batch);

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