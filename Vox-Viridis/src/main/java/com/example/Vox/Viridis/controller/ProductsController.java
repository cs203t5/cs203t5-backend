package com.example.Vox.Viridis.controller;

import javax.persistence.EntityManager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import com.example.Vox.Viridis.exception.NotEnoughPointException;
import com.example.Vox.Viridis.exception.ResourceNotFoundException;
import com.example.Vox.Viridis.model.Products;
import com.example.Vox.Viridis.service.ProductsService;
import com.example.Vox.Viridis.service.StorageService;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductsService productsService;
    private final StorageService storageService;
    private final EntityManager entityManager;

    @GetMapping("{id}")
    public Products getProducts(@PathVariable Long id){
        Products result =  productsService.getProducts(id)
            .orElseThrow(() -> new ResourceNotFoundException("Products id " + id));
        entityManager.detach(result);
        String image = result.getImage();
        if (image != null)
            result.setImage(storageService.getUrl(image));
        return result;
    }

    
}
