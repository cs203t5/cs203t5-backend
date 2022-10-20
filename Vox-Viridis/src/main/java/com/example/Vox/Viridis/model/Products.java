package com.example.Vox.Viridis.model;

import javax.persistence.GeneratedValue;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.example.Vox.Viridis.model.dto.ProductsDTO;

import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Products")

public class Products {
    @Id @GeneratedValue
    private Long id;    
    @NotNull(message = "Products's name should not be null")
    @Size(min = 5, max = 255, message = "Product's name should be at least 5 characters long")
    private String name;
    @NotNull(message = "Products's description should not be null")
    @Size(min = 5, max = 255, message = "Product's description should be at least 15 characters long")
    private String description;
    private String image;
    private int point;
    private String category;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_products", 
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "products_id"))
    private List<Users> users;

    public ProductsDTO convertToDTO() {
        return new ProductsDTO(id, name, description, image, category, point);
    }
}
