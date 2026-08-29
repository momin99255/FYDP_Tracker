package com.momin.fydp_sync.service;


import com.momin.fydp_sync.entity.User;
import com.momin.fydp_sync.entity.UserProfile;

public interface UserService {

    User registerUser(User user);
    User getUserByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    UserProfile getOrCreateProfile(User user);
    String getProfilePictureUrl(User user);
}