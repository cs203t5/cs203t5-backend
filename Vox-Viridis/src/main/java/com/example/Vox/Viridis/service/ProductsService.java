package com.example.Vox.Viridis.service;

import java.util.List;
import java.util.Optional;

import com.example.Vox.Viridis.model.Products;
public interface ProductsService {
    Optional<Products> getProducts(Long id);
    List<Products> getAllProducts();
    Products addProducts(Products products);
    Products updateProducts(Products products, Long id);
    void deleteProducts(Long id);
}
