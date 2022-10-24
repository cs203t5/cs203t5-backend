package com.example.Vox.Viridis.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.example.Vox.Viridis.exception.NotEnoughPointException;
import com.example.Vox.Viridis.exception.ResourceNotFoundException;
import com.example.Vox.Viridis.model.Products;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.repository.ProductsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ProductsServiceImpl implements ProductsService{
    private final ProductsRepository productsRepository;
    private final UsersService usersService;
    
    @Override
    public List<Products> getAllProducts() {
        return productsRepository.findAll();
    }

    @Override
    public Optional<Products> getProducts(Long id) {
        return productsRepository.findById(id);
    }

    @Override
    public Products addProducts(Products products) {
        products.setCreatedBy(usersService.getCurrentUser());
        log.info("Product created: " + products.getName());
        return productsRepository.save(products);
    }

    @Override
    public Products updateProducts(Products updatedProducts, Long id) {
        Products existingProducts = getProducts(id).orElseThrow(() -> new ResourceNotFoundException("Products not found"));
        updatedProducts.setId(id);
        updatedProducts.setImage(existingProducts.getImage());
        updatedProducts.setCreatedBy(existingProducts.getCreatedBy());
        log.info("updated Product with id: " + id);
        return productsRepository.save(updatedProducts);
    }

    @Override
    public void deleteProducts(Long id) {
        log.info("Delete Product with id: " + id);
        productsRepository.deleteById(id);
    }

    @Override
    public Products updateProductsImage(Products product, String imageFilename) {
        product.setImage(imageFilename);
        log.info("updated product image to '" + imageFilename + "'' for id: " + product.getId());
        return productsRepository.save(product);
    }

    @Override
    public int buyProducts(Long id){
        Products products = getProducts(id).orElseThrow(() -> new ResourceNotFoundException("Products not found"));
        int cost = products.getPoint();
        Users buyer = usersService.getCurrentUser();
        int buyerPoint = buyer.getPoints();
        int leftOverPoint = buyerPoint - cost;
        if (cost > buyerPoint) {
            throw new NotEnoughPointException();
        }
        else {
            log.info("Product with id " + id + " purchased");
            buyer.setPoints(leftOverPoint);
            usersService.updateUser(buyer);
            return leftOverPoint;
        }
    }
}
