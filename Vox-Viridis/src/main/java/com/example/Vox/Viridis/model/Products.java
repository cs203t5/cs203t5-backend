package com.example.Vox.Viridis.model;

import javax.persistence.GeneratedValue;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.example.Vox.Viridis.model.dto.ProductsDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.JoinColumn;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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
    @Size(min = 5, max = 255, message = "Product's description should be at least 5 characters long")
    private String description;

    private String image;
    private int point;
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Users createdBy;

    public ProductsDTO convertToDTO() {
        return new ProductsDTO(id, name, description, image, category, point);
    }

}
