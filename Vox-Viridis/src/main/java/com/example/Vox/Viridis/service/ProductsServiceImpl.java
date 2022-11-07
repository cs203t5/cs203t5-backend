package com.example.Vox.Viridis.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.example.Vox.Viridis.exception.NotEnoughPointException;
import com.example.Vox.Viridis.exception.NotOwnerException;
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
    public Products getProducts(Long id) {
        Optional<Products> result = productsRepository.findById(id);
        if (result.isEmpty()) {
           throw new ResourceNotFoundException();
        }
        return result.get();
    }

    @Override
    public Products addProducts(Products products) {
        products.setCreatedBy(usersService.getCurrentUser());
        log.info("Product created: " + products.getName());
        return productsRepository.save(products);
    }

    @Override
    public Products updateProducts(Products updatedProducts, Long id) {
        Products existingProducts = getProducts(id);
        updatedProducts.setId(id);
        updatedProducts.setImage(existingProducts.getImage());
        updatedProducts.setCreatedBy(existingProducts.getCreatedBy());
        log.info("updated Product with id: " + id);
        return productsRepository.save(updatedProducts);
    }

    /**
     * @throws NotOwnerException if current user isn't the owner of this campaign
     */
    @Override
    public void deleteProducts(Long id) {
        Users username = usersService.getCurrentUser();
        if (username != null
                && !productsRepository.getCreatedBy(id).equals(username.getAccountId()))
            throw new NotOwnerException();
        log.info("Delete Product with id: " + id);
        productsRepository.deleteById(id);
    }

    @Override
    public Products updateProductsImage(Products product, String imageFilename) {
        product.setImage(imageFilename);
        log.info("updated product image to '" + imageFilename + "'' for id: " + product.getId());
        return productsRepository.save(product);
    }


    private int getLeftOverPoint(Users buyer,Products product) {
        return buyer.getPoints() - product.getPoint();
    }
    
    /**
     * @throws NotEnoughPointException if current user doesnt have enough point
     */
    @Override
    public Users buyProducts(Long id){
        Products products = getProducts(id);
        Users buyer = usersService.getCurrentUser();
        int leftOverPoint = getLeftOverPoint(buyer, products);
        if (leftOverPoint < 0) {
            throw new NotEnoughPointException();
        }
        else {
            log.info("Product with id " + id + " purchased");
            buyer.setPoints(leftOverPoint);
            usersService.updateUser(buyer);
            return buyer;
        }
    }
}
