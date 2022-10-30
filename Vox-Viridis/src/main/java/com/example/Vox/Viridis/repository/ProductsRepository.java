package com.example.Vox.Viridis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Vox.Viridis.model.Products;

public interface ProductsRepository extends JpaRepository<Products,Long>{
    List<Products> findByName(String name);

    @Query(nativeQuery = true, value = "SELECT created_by FROM products WHERE id = :id")
    Long getCreatedBy(Long id);
}
