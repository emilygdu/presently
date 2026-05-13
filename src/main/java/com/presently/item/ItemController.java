package com.presently.item;

import com.presently.user.User;
import com.presently.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    @GetMapping("/items")
    public ResponseEntity<?> getItems(
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) EventType eventType) {
        
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        
        return userService.findByUsername(username).map(user ->
            ResponseEntity.ok(itemService.getItemsFiltered(user, category, eventType)))
            .orElse(ResponseEntity.notFound().build());
            }

    @PostMapping("/items")
    public ResponseEntity<Item> addItem(@RequestBody Item item) {
        String username = SecurityContextHolder.getContext()
            .getAuthentication().getName();
        
        return userService.findByUsername(username)
            .map(user -> {
                item.setOwner(user);
                return ResponseEntity.ok(itemService.save(item));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/items/{id}")
    public ResponseEntity updateItem(@PathVariable Long id, @RequestBody Item item) {
        String username = SecurityContextHolder.getContext()
            .getAuthentication().getName();
        
        return userService.findByUsername(username).map(user ->
            ResponseEntity.ok(itemService.markAsBought(id, user)))
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/items/{id}/bought")
    public ResponseEntity markAsBought(@PathVariable Long id, @RequestBody User buyer) {
        return ResponseEntity.ok(itemService.markAsBought(id, buyer));
    }
             
}

