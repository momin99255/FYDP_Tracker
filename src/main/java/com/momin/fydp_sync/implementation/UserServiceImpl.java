package com.momin.fydp_sync.implementation;

import com.momin.fydp_sync.entity.User;
import com.momin.fydp_sync.entity.UserProfile;
import com.momin.fydp_sync.repository.UserProfileRepository;
import com.momin.fydp_sync.repository.UserRepository;
import com.momin.fydp_sync.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Cacheable(value = "users", key = "#username")
    public User getUserByUsername(String username) {
        return userRepository.findByUsernameOrEmail(username, username).orElse(null);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserProfile getOrCreateProfile(User user) {
        return userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile profile = new UserProfile();
                    profile.setUser(user);
                    return userProfileRepository.save(profile);
                });
    }

    @Override
    @Cacheable(value = "profilePictures", key = "#user.username")
    public String getProfilePictureUrl(User user) {
        return userProfileRepository.findByUser(user)
                .map(UserProfile::getProfilePicture)
                .orElse(null);
    }
}