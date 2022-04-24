package com.example.fashionshop.validation;

import com.example.fashionshop.model.Product;
import com.example.fashionshop.validation.commons.DescriptionValidator;
import com.example.fashionshop.validation.commons.ImageValidator;
import com.example.fashionshop.validation.commons.StockValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class ProductValidator {
    public static void validateUpdateProduct(Product product, HttpStatus status, String message) {
//        ImageValidator.checkDefaultImage(product);
        if (product.getName().length() == 0 ||
                product.getPrice() < 0 ||
                !StockValidator.validateStock(product.getStock()) ||
                !DescriptionValidator.validateDescription(product.getDescription())) {
            throw new ResponseStatusException(status, message);
        }
    }

    public static void validateCreateProduct(Product product, HttpStatus status, String message) {
        validateUpdateProduct(product, status, message);
    }

    public static void validateDeleteProduct(String userId, HttpStatus status, String message){
        UserValidator.checkUserAuthorized(userId, status, message);
    }
}
