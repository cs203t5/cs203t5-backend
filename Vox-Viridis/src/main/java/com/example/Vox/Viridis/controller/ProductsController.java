package com.example.Vox.Viridis.controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Vox.Viridis.exception.InvalidFileTypeException;
import com.example.Vox.Viridis.exception.ResourceNotFoundException;
import com.example.Vox.Viridis.model.Products;
import com.example.Vox.Viridis.model.dto.ProductsDTO;
import com.example.Vox.Viridis.model.dto.UsersDTO;
import com.example.Vox.Viridis.service.ProductsService;
import com.example.Vox.Viridis.service.StorageService;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductsService productsService;
    private final StorageService storageService;

    @GetMapping("{id}")
    public Products getProducts(@PathVariable Long id) {
        Products product = productsService.getProducts(id)
                .orElseThrow(() -> new ResourceNotFoundException("Products id " + id));
        String image = product.getImage();
        if (image != null)
            product.setImage(storageService.getUrl(image));
        return product;
    }

    @GetMapping()
    public @NotNull List<Products> getAllProducts() {
        return productsService.getAllProducts();
    }

    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public ProductsDTO addProducts(@Valid @ModelAttribute Products products,
            @RequestParam(value = "imageFile", required = false) MultipartFile image) {
            Products result = productsService.addProducts(products);
        if (image != null && !image.isEmpty()) {
            if (image.getContentType() == null || !image.getContentType().startsWith("image/"))
                throw new InvalidFileTypeException("Image file like jpeg");
        }
        if (image != null && !image.isEmpty()) {
            String filename = StorageService.PRODUCTS_DIR + result.getId() + image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
            result = productsService.updateProductsImage(products, filename);

            storageService.putObject(filename, image);
        }
        result.setImage(storageService.getUrl(result.getImage()));
        return result.convertToDTO();
    }

    @Transactional
    @PutMapping("{id}")
    public Products updateProducts(@RequestBody Products products,@PathVariable Long id, @RequestParam(value="imageFile", required=false) MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            if (image.getContentType() == null || !image.getContentType().startsWith("image/"))
                throw new InvalidFileTypeException("Image file like jpeg");
        }
        Products result = productsService.updateProducts(products, id);
        if (image != null && !image.isEmpty()) {
            if (result.getImage() != null)
                storageService.deleteObject(result.getImage());

            String filename = StorageService.PRODUCTS_DIR + result.getId() + image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
            result = productsService.updateProductsImage(products, filename);

            storageService.putObject(filename, image);
        }
        //entityManager.detach(result);
        result.setImage(storageService.getUrl(result.getImage()));
        return result;
    }

    @DeleteMapping("{id}")
    public void deleteProducts(@PathVariable Long id) {
        Products product = productsService.getProducts(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product id: " + id));
        if (product.getImage() != null && !product.getImage().isBlank())
            storageService.deleteObject(product.getImage());
        productsService.deleteProducts(id);
    }

    @PostMapping("buy/{id}")
    public UsersDTO buyProducts(@PathVariable Long id) {
        return productsService.buyProducts(id).convertToDTO();
    }

}
