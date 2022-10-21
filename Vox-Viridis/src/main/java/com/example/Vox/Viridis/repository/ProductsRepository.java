package com.example.Vox.Viridis.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Vox.Viridis.model.Products;

public interface ProductsRepository extends JpaRepository<Products,Long>{
    Optional<Products> findByName(String name);
}
