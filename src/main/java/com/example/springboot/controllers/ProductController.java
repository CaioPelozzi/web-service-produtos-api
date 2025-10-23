package com.example.springboot.controllers;

import com.example.springboot.dtos.ProductRecordDTO;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ProductController {

    private ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostMapping("/products")
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDTO productRecordDTO) {
        var productModel = new ProductModel();
        BeanUtils.copyProperties(productRecordDTO, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
    }

    @GetMapping("products")
    public ResponseEntity<List<ProductModel>> getAllProduct() {
        List<ProductModel> productModelList  = productRepository.findAll();
        if (!productModelList.isEmpty()) {
            for (ProductModel product : productModelList){
                UUID id = product.getIdProduct();
                product.add(linkTo(methodOn(ProductController.class).getProductModelById(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(productModelList);
    }

    @GetMapping("products/{id}")
    public ResponseEntity<Object> getProductModelById(@PathVariable(value="id") UUID id) {
        Optional<ProductModel> productModelOptional = productRepository.findById(id);
        if(productModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found!");
        }
        productModelOptional.get().add(linkTo(methodOn(ProductController.class).getAllProduct()).withRel("Products List"));
        return ResponseEntity.status(HttpStatus.OK).body(productModelOptional.get());

    }

    @PutMapping("products/{id}")
    public ResponseEntity<Object> updateProductModelById(@PathVariable(value="id") UUID id,
                                                        @RequestBody @Valid ProductRecordDTO productRecordDTO) {
        Optional<ProductModel> productModelOptional = productRepository.findById(id);
        if(productModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found!");
        }
        var productModel = productModelOptional.get();
        BeanUtils.copyProperties(productRecordDTO, productModel);
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
    }

    @DeleteMapping("products/{id}")
    public ResponseEntity<Object> deleteProductModelById(@PathVariable(value="id") UUID id) {
        Optional<ProductModel> productModelOptional = productRepository.findById(id);
        if(productModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found!");
        }
        var productModel = productModelOptional.get();
        productRepository.delete(productModel);
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully.");
    }


}
