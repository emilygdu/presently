package com.presently.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.presently.item.ItemDTO;
import com.presently.item.ItemService;



@RestController
@RequestMapping("/users")
@RequiredArgsConstructor

public class UserController {

    private final UserService userService;
    private final ItemService itemService;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe() {
        String username = SecurityContextHolder.getContext()
            .getAuthentication().getName();

        return userService.findByUsername(username)
            .map(user -> ResponseEntity.ok(userService.toDTO(user)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(userService.toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateMe(@RequestBody Map<String, String> body) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        
        return userService.findByUsername(username).map(user -> {
            if (body.containsKey("username")) {
                user.setUsername(body.get("username"));
            }
            if (body.containsKey("email")) {
                user.setEmail(body.get("email"));
            }
            return ResponseEntity.ok(userService.toDTO(userService.save(user)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAll()
                .stream()
                .map(user -> userService.toDTO(user))
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}/wishlist")
    public ResponseEntity<List<ItemDTO>> getFriendWishlist(@PathVariable Long id) {
        return userService.findById(id).map(user -> {
            List<ItemDTO> items = itemService.getItemsByOwner(user)
                .stream()
                .map(item -> itemService.toDTO(item, false))
                .toList();
            return ResponseEntity.ok(items);
        }).orElse(ResponseEntity.notFound().build());
    }
    
    
    
}
