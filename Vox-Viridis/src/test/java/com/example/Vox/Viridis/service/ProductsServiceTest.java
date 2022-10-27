package com.example.Vox.Viridis.service;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.Vox.Viridis.model.Products;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.model.dto.UsersDTO;
import com.example.Vox.Viridis.repository.*;


@ExtendWith (MockitoExtension.class)
public class ProductsServiceTest {
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

        //mock the "save" operation
        when(products.save(any(Products.class))).
        thenReturn(product);

        Products savedProducts = productsService.addProducts(product);
        assertNotNull(savedProducts); 
    }  


    // @Test
    // void buyProducts_SufficientPoint_ReturnUsers() {
    //     Users user = new Users();
    //     user.setUsername("new account");
    //     user.setPoints(40);

    //     Products product = new Products();
    //     product.setId((long) (23));
    //     product.setPoint(20);

    //     Users updatedUser = new Users();
    //     user.setPoints(20);

    //     //mock the "Find" operation
    //     when(products.findById((long)(23))).thenReturn(Optional.of(product));
    //     //mock the get current user
    //     when(usersService.getCurrentUser()).thenReturn(user);
    //     //mock the "update" operation
    //     when(users.save(any(Users.class))).thenReturn(updatedUser);

    //     UsersDTO updatedUsers = usersService.updateUser(user);


    // }

    /*
     * public Users buyProducts(Long id){
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
            return buyer;
        }
    }
     */
}
