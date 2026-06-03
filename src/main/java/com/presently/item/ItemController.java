package com.presently.item;

import com.presently.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.presently.item.Item;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    @GetMapping("/items")
    public ResponseEntity<List<ItemDTO>> getItems(
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) EventType eventType,
            @RequestParam(required = false) List<ProductCategory> categories,
            @RequestParam(required = false) Boolean isFavorite,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String sortBy) {

        String username = SecurityContextHolder.getContext()
            .getAuthentication().getName();

        return userService.findByUsername(username).map(user -> {
            List<Item> items = itemService.getItemsFiltered(
                    user, category, eventType, categories, isFavorite, minPrice, maxPrice, title);
            List<ItemDTO> sorted = itemService.sortItems(items, sortBy)
                    .stream()
                    .map(item -> itemService.toDTO(item, true))
                    .toList();
            return ResponseEntity.ok(sorted);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/items")
    public ResponseEntity<ItemDTO> addItem(@RequestBody Item item) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userService.findByUsername(username)
                .map(user -> {
                    item.setOwner(user);
                    return ResponseEntity.ok(itemService.toDTO(itemService.save(item), true));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id, @RequestBody Item updatedItem) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        
        return userService.findByUsername(username).map(user ->
            itemService.findById(id).map(existingItem -> {
                if (!existingItem.getOwner().getId().equals(user.getId())) {
                    return ResponseEntity.status(403).<ItemDTO>build();
                }
                existingItem.setTitle(updatedItem.getTitle());
                existingItem.setPrice(updatedItem.getPrice());
                existingItem.setProductUrl(updatedItem.getProductUrl());
                existingItem.setImageUrl(updatedItem.getImageUrl());
                existingItem.setProductCategory(updatedItem.getProductCategory());
                existingItem.setEventType(updatedItem.getEventType());
                existingItem.setIsFavorite(updatedItem.getIsFavorite());
                return ResponseEntity.ok(itemService.toDTO(itemService.save(existingItem), true));
            }).orElse(ResponseEntity.notFound().build()))
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        
        var user = userService.findByUsername(username);
        if (user.isEmpty()) return ResponseEntity.notFound().build();
            
        var item = itemService.findById(id);
        if (item.isEmpty()) return ResponseEntity.notFound().build();
            
        if (!item.get().getOwner().getId().equals(user.get().getId())) {
            return ResponseEntity.status(403).build();
        }
            
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/items/{id}/bought")
    public ResponseEntity<ItemDTO> markAsBought(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userService.findByUsername(username)
                .map(user -> ResponseEntity.ok(
                        itemService.toDTO(itemService.markAsBought(id, user), false)))
                .orElse(ResponseEntity.notFound().build());
    }
}
