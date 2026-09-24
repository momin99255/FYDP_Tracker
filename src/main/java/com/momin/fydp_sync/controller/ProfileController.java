package com.momin.fydp_sync.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.momin.fydp_sync.entity.User;
import com.momin.fydp_sync.entity.UserProfile;
import com.momin.fydp_sync.enums.Designation;
import com.momin.fydp_sync.repository.UserProfileRepository;
import com.momin.fydp_sync.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final UserProfileRepository userProfileRepository;
    private final Cloudinary cloudinary;

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
    public String viewOtherProfile(
            @PathVariable String username,
            Model model,
            Principal principal) {

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

    @CacheEvict(value = "profilePictures", key = "#principal.name")
    @PostMapping("/update")
    public String updateProfile(
            @RequestParam(value = "profilePictureFile", required = false)
            MultipartFile file,

            @RequestParam(value = "contactNo", required = false)
            String contactNo,

            @RequestParam(value = "university", required = false)
            String university,

            @RequestParam(value = "department", required = false)
            String department,

            @RequestParam(value = "batch", required = false)
            String batch,

            @RequestParam(value = "designation", required = false)
            Designation designation,

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

            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder",
                            "fydp-sync/profile-pics"
                    )
            );

            String secureUrl = (String) uploadResult.get("secure_url");

            profile.setProfilePicture(secureUrl);
        }

        userProfileRepository.save(profile);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Profile updated successfully."
        );

        return "redirect:/profile";
    }
}