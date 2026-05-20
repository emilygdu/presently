package com.presently.item;

import com.presently.user.UserDTO;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor

public class ItemDTO {

    private Long id;
    private String title;
    private Double price;
    private String productUrl;
    private String imageUrl;
    private ProductCategory productCategory;
    private EventType eventType;
    private Boolean isFavorite;
    private UserDTO owner;
    private Boolean isBought; 
    
}
