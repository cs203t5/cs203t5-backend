package com.example.Vox.Viridis.controller;

import javax.persistence.EntityManager;

import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import com.example.Vox.Viridis.service.ProductsService;
import com.example.Vox.Viridis.service.StorageService;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductsService productsService;
    private final StorageService storageService;

    
}
