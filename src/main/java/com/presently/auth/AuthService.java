package com.presently.auth;

import com.presently.user.User;
import com.presently.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class AuthService {

    public final UserService userService;
    public final PasswordEncoder passwordEncoder;
    public final JwtUtil jwtUtil;

    public String register(String username, String email, String password) {
        if (userService.existsByUsername(username)) {
            throw new RuntimeException("Username bereits vorhanden");
        }
        if (userService.existsByEmail(email)) {
            throw new RuntimeException("Email bereits vergeben");
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
                .orElseThrow(() -> new RuntimeException("User nicht gefunden"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Falsches Passwort");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getId());
    }
    
}
