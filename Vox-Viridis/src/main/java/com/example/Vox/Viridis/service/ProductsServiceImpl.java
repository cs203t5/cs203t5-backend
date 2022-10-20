package com.example.Vox.Viridis.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.Vox.Viridis.model.Products;
import com.example.Vox.Viridis.repository.ProductsRepository;

@Service
@Transactional
public class ProductsServiceImpl implements ProductsService{
    private final ProductsRepository productsRepository;
    
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Products updateProducts(Products products, Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteProducts(Long id) {
        // TODO Auto-generated method stub
        
    }
}
