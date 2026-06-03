package com.presently.item;

import com.presently.user.User;
import com.presently.user.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class ItemService {

    public final ItemRepository itemRepository;

    public List<Item> getItemsByOwner(User owner){
        return itemRepository.findByOwner(owner);
    }

    public List<Item> getItemsFiltered(User owner, ProductCategory category, EventType eventType,
        List<ProductCategory> categories, Boolean isFavorite,
        Double minPrice, Double maxPrice, String title) {

        List<Item> items = itemRepository.findByOwner(owner);

        if (categories != null && !categories.isEmpty()) {
            items = items.stream()
                    .filter(item -> categories.contains(item.getProductCategory()))
                    .toList();
        } else if (category != null) {
            items = items.stream()
                    .filter(item -> category.equals(item.getProductCategory()))
                    .toList();
        }

        if (eventType != null) {
            items = items.stream()
                    .filter(item -> eventType.equals(item.getEventType()))
                    .toList();
        }

        if (isFavorite != null && isFavorite) {
            items = items.stream()
                    .filter(item -> Boolean.TRUE.equals(item.getIsFavorite()))
                    .toList();
        }

        if (minPrice != null && maxPrice != null) {
            items = items.stream()
                    .filter(item -> item.getPrice() >= minPrice && item.getPrice() <= maxPrice)
                    .toList();
        } else if (minPrice != null) {
            items = items.stream()
                    .filter(item -> item.getPrice() >= minPrice)
                    .toList();
        } else if (maxPrice != null) {
            items = items.stream()
                    .filter(item -> item.getPrice() <= maxPrice)
                    .toList();
        }

        if (title != null) {
            items = items.stream()
                    .filter(item -> item.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .toList();
        }

        return items;
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public Item markAsBought(Long itemId, User buyer) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setBoughtBy(buyer);
        return itemRepository.save(item);
    }

    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public ItemDTO toDTO(Item item, boolean isOwner) {
        UserDTO ownerDTO = new UserDTO(
            item.getOwner().getId(),
            item.getOwner().getUsername(),
            item.getOwner().getEmail()
        );

        if (isOwner) {
            return new ItemDTO(
                item.getId(),
                item.getTitle(),
                item.getPrice(),
                item.getProductUrl(),
                item.getImageUrl(),
                item.getProductCategory(),
                item.getEventType(),
                item.getIsFavorite(),
                ownerDTO,
                null //Owner can't  see woh bought the item
            );
        } else {
            return new ItemDTO(
                item.getId(),
                item.getTitle(),
                item.getPrice(),
                item.getProductUrl(),
                item.getImageUrl(),
                item.getProductCategory(),
                item.getEventType(),
                item.getIsFavorite(),
                ownerDTO,
                item.getBoughtBy() != null //Friends only can see true or false

            );
        }
    }
    
    public List<Item> sortItems(List<Item> items, String sortBy) {
        if (sortBy == null) return items;

        return switch (sortBy) {
            case "price_asc" -> items.stream()
                .sorted((a, b) -> Double.compare(a.getPrice(), b.getPrice()))
                .toList();
            case "price_desc" -> items.stream()
                .sorted((a, b) -> Double.compare(b.getPrice(), a.getPrice()))
                .toList();
            case "title_asc" -> items.stream()
                .sorted((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()))
                .toList();
            case "title_desc" -> items.stream()
                .sorted((a, b) -> b.getTitle().compareToIgnoreCase(a.getTitle()))
                .toList();
            default -> items;
        };
    }
}
