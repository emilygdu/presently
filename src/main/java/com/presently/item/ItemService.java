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

        if (categories != null && !categories.isEmpty()) {
            return itemRepository.findByOwnerAndProductCategoryIn(owner, categories);
        }
        if (isFavorite != null && isFavorite) {
            return itemRepository.findByOwnerAndIsFavoriteTrue(owner);
        }
        if (minPrice != null) {
            return itemRepository.findByOwnerAndPriceGreaterThanEqual(owner, minPrice);
        }
        if (maxPrice != null) {
            return itemRepository.findByOwnerAndPriceLessThanEqual(owner, maxPrice);
        }
        if (title != null) {
            return itemRepository.findByOwnerAndTitleContainingIgnoreCase(owner, title);
        }
        if (category != null && eventType != null) {
            return itemRepository.findByOwnerAndProductCategoryAndEventType(owner, category, eventType);
        }
        if (category != null) {
            return itemRepository.findByOwnerAndProductCategory(owner, category);
        }
        if (eventType != null) {
            return itemRepository.findByOwnerAndEventType(owner, eventType);
        }
        return itemRepository.findByOwner(owner);
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
}
