package com.example.Vox.Viridis.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductsDTO {
    private Long id;
    private String name;
    private String description;
    private String image;
    private String category;
    private int point;
}
