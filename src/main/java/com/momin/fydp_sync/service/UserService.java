package com.momin.fydp_sync.service;

import com.momin.fydp_sync.model.User;
import com.momin.fydp_sync.model.UserProfile;
import com.momin.fydp_sync.repository.UserProfileRepository;
import com.momin.fydp_sync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsernameOrEmail(username, username).orElse(null);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserProfile getOrCreateProfile(User user) {
        return userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile profile = new UserProfile();
                    profile.setUser(user);
                    return userProfileRepository.save(profile);
                });
    }

    public String getProfilePictureUrl(User user) {
        return userProfileRepository.findByUser(user)
                .map(UserProfile::getProfilePicture)
                .orElse(null);
    }
}