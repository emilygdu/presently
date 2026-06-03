package com.presently.auth;

import com.presently.user.User;
import com.presently.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String register(String username, String email, String password) {
        if (userService.existsByUsername(username)) {
            throw new RuntimeException("Invalid credentials");
        }
        if (userService.existsByEmail(email)) {
            throw new RuntimeException("Invalid credentials");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        User saved = userService.save(user);
        return jwtUtil.generateToken(saved.getUsername(), saved.getId());
    }

    public String login(String username, String password) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getId());
    }
    
}
