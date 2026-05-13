package com.presently.item;

import com.presently.user.User;
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

    public List<Item> getItemsFiltered(User owner, ProductCategory category, EventType eventType) {
        if (category != null && eventType != null) {
            return itemRepository.findByOwnerAndProductCategoryAndEventType(owner, category, eventType);
        } else if (category != null) {
            return itemRepository.findByOwnerAndProductCategory(owner, category);
        } else if (eventType != null) {
            return itemRepository.findByOwnerAndEventType(owner, eventType);
        } else {
            return itemRepository.findByOwner(owner);
        }
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
    
}
