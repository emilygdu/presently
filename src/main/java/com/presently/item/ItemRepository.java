package com.presently.item;

import com.presently.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner(User owner);
    List<Item> findByOwnerAndProductCategory(User owner, ProductCategory productcategory);
    List<Item> findByOwnerAndEventType(User owner, EventType eventType);
    List<Item> findByOwnerAndProductCategoryAndEventType(User owner, ProductCategory productCategory, EventType eventType);
}
