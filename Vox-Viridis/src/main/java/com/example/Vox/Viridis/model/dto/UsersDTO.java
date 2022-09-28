package com.example.Vox.Viridis.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsersDTO {
    private Long account_id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private int points;
    private String image;
}
