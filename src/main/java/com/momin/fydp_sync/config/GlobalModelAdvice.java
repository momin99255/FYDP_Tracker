package com.momin.fydp_sync.config;

import com.momin.fydp_sync.entity.User;
import com.momin.fydp_sync.repository.UserProfileRepository;
import com.momin.fydp_sync.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final UserService userService;
    private final UserProfileRepository userProfileRepository;

    @ModelAttribute
    public void injectNavProfile(Model model, Principal principal) {
        if (principal == null) return;

        User user = userService.getUserByUsername(principal.getName());
        if (user == null) return;

        userProfileRepository.findByUser(user)
                .ifPresent(profile -> model.addAttribute("navProfilePicture", profile.getProfilePicture()));
    }
}