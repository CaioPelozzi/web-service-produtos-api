package com.example.springboot.controllers;

import com.example.springboot.repositories.ProductRepository;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    private ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

}
