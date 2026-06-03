package com.presently.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

import com.presently.item.ItemDTO;
import com.presently.item.ItemService;
import com.presently.item.ProductCategory;
import com.presently.item.EventType;
import com.presently.item.Item;



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
    public ResponseEntity<List<ItemDTO>> getFriendWishlist(
            @PathVariable Long id,
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) EventType eventType,
            @RequestParam(required = false) List<ProductCategory> categories,
            @RequestParam(required = false) Boolean isFavorite,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String sortBy) {

        return userService.findById(id).map(user -> {
            List<Item> items = itemService.getItemsFiltered(
                    user, category, eventType, categories, isFavorite, minPrice, maxPrice, title);
            List<ItemDTO> sorted = itemService.sortItems(items, sortBy)
                    .stream()
                    .map(item -> itemService.toDTO(item, false))
                    .toList();
        return ResponseEntity.ok(sorted);
        }).orElse(ResponseEntity.notFound().build());
    } 
}
