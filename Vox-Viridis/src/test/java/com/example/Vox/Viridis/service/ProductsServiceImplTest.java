package com.example.Vox.Viridis.service;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.Vox.Viridis.exception.NotEnoughPointException;
import com.example.Vox.Viridis.exception.ResourceNotFoundException;
import com.example.Vox.Viridis.model.Products;
import com.example.Vox.Viridis.model.Role;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.repository.*;

@ExtendWith(MockitoExtension.class)
public class ProductsServiceImplTest {
    @Mock
    private ProductsRepository products;

    @Mock
    private UsersRepository users;

    @Mock
    private UsersService usersService;

    @InjectMocks
    private ProductsServiceImpl productsService;

    @Test
    void addProduct_NewTitle_ReturnSavedProducts() {
        Products product = new Products();
        product.setName("This is a new products");

        // mock the "save" operation
        when(products.save(any(Products.class))).thenReturn(product);

        Products savedProducts = productsService.addProducts(product);
        assertNotNull(savedProducts);
        verify(products).save(product);
    }

    @Test
    void getProduct_validId_ReturnProduct() {
        Products product = new Products();
        product.setId((long) 23);
        when(products.findById(((long) 23))).thenReturn(Optional.of(product));

        Products returnProduct = productsService.getProducts((long) 23);
        assertNotNull(returnProduct);
        verify(products).findById((long) 23);
    }

    @Test
    void getProduct_InvalidId_ReturnNull() {
        Products product = new Products();
        product.setId((long) 23);
        Throwable resourceNotFoundException = new ResourceNotFoundException();
        Throwable exception = null;
        when(products.findById(((long) 22))).thenReturn(Optional.ofNullable(null));
        try {
        Products returnProduct = productsService.getProducts((long) 22);
        }catch(ResourceNotFoundException e) {
            exception = resourceNotFoundException;
        }
        assertEquals(resourceNotFoundException,exception);
        verify(products).findById((long) 22);
    }

    @Test
    void updateProducts_NewTitle_ReturnSavedProducts() {
        Products product = new Products();
        product.setId((long) 23);
        product.setName("This is a new product");

        Products updatedProducts = new Products();
        product.setName("This is a new new product");
        updatedProducts.setId((long) 23);

        // mock the findbyId operation
        when(products.findById((long) 23)).thenReturn(Optional.of(product));
        // mock the save operation
        when(products.save(any(Products.class))).thenReturn(updatedProducts);

        Products savedProducts = productsService.updateProducts(updatedProducts, (long) 23);

        assertEquals(savedProducts, updatedProducts);
        verify(products).findById((long) 23);
        verify(products).save(updatedProducts);

    }

    // @Test
    // void updateProducts_SameTitle_ReturnSavedProducts() {
    //     Products product = new Products();
    //     product.setId((long) 23);
    //     product.setName("This is a new product");

    //     Products updatedProducts = new Products();
    //     product.setName("This is a new product");
    //     updatedProducts.setId((long) 23);

    //     // mock the findbyId operation
    //     when(products.findById((long) 23)).thenReturn(Optional.of(product));
    //     // mock the save operation
    //     when(products.save(any(Products.class))).thenReturn(updatedProducts);

    //     Products savedProducts = productsService.updateProducts(updatedProducts, (long) 23);

    //     assertEquals(savedProducts, updatedProducts);
    //     verify(products).findById((long) 23);
    //     verify(products).save(updatedProducts);

    // }

    @Test
    void buyProducts_SufficientPoint_ReturnUsers() {
        Users user = new Users();
        user.setAccountId((long) 23);
        user.setPoints(40);

        Products product = new Products();
        product.setId((long) (24));
        product.setPoint(10);

        Role role = new Role();
        role.setName("CONSUMER");
        role.setRoleId(1l);

        Users updatedUser = user;
        updatedUser.setPoints(20);
        updatedUser.setRoles(role);

        // mock the "Find" operation
        when(products.findById((long) (24))).thenReturn(Optional.of(product));
        // mock the get current user
        when(usersService.getCurrentUser()).thenReturn(user);
        // mock the updateUser operation
        when(usersService.updateUser(updatedUser)).thenReturn(updatedUser.convertToDTO());

        Users savedUser = productsService.buyProducts((long) 24);

        assertEquals(savedUser, updatedUser);
        verify(products).findById((long) 24);
        verify(usersService).getCurrentUser();
        verify(usersService).updateUser(updatedUser);
    }


    @Test
    void buyProducts_InsufficientPoint_ReturnErrorMessage() {
        Users user = new Users();
        user.setAccountId((long) 23);
        user.setPoints(5);

        Products product = new Products();
        product.setId((long) (24));
        product.setPoint(10);

        NotEnoughPointException error = new NotEnoughPointException();
        // mock the "Find" operation
        when(products.findById((long) (24))).thenReturn(Optional.of(product));
        // mock the get current user
        when(usersService.getCurrentUser()).thenReturn(user);

        String errorMsg = "";
        try{
        Users savedUser = productsService.buyProducts((long) 24);
        }catch (NotEnoughPointException e) {
            errorMsg += "Insufficient point to purchase product.";
        }
        
        assertEquals(error.getMessage(),errorMsg);
        verify(products).findById((long) 24);
        verify(usersService).getCurrentUser();
    }
}
