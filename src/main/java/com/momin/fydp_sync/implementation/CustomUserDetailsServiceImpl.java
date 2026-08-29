package com.momin.fydp_sync.implementation;

import com.momin.fydp_sync.entity.User;
import com.momin.fydp_sync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService { // <-- এখানে ইন্টারফেসটি ইমপ্লিমেন্ট করা হয়েছে

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {

        System.out.println("🔍 Spring Security is searching for: " + input);

        User user = userRepository.findByUsernameOrEmail(input, input)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID or Email: " + input));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}