package com.presently.item;

import com.presently.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
            @RequestParam(required = false) String title) {

        String username = SecurityContextHolder.getContext()
            .getAuthentication().getName();

        return userService.findByUsername(username).map(user -> {
            List<ItemDTO> items = itemService.getItemsFiltered(
                    user, category, eventType, categories, isFavorite, minPrice, maxPrice, title)
                    .stream()
                    .map(item -> itemService.toDTO(item, true))
                    .toList();
            return ResponseEntity.ok(items);
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
        return itemService.findById(id).map(existingItem -> {
            existingItem.setTitle(updatedItem.getTitle());
            existingItem.setPrice(updatedItem.getPrice());
            existingItem.setProductUrl(updatedItem.getProductUrl());
            existingItem.setImageUrl(updatedItem.getImageUrl());
            existingItem.setProductCategory(updatedItem.getProductCategory());
            existingItem.setEventType(updatedItem.getEventType());
            existingItem.setIsFavorite(updatedItem.getIsFavorite());
            return ResponseEntity.ok(itemService.toDTO(itemService.save(existingItem), true));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
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
