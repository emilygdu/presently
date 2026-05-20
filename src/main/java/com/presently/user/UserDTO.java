package com.presently.user;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor

public class UserDTO {

    private Long id;
    private String username;
    private String email;
    
}
