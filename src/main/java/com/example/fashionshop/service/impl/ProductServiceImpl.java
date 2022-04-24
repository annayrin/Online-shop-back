package com.example.fashionshop.service.impl;

import com.example.fashionshop.model.Product;
import com.example.fashionshop.repository.ProductRepository;
import com.example.fashionshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    /***
     *
     * @param product the product that would be added in DB
     * @return new product which has added
     */
    @Override
    public Product create(Product product) {
        return productRepository.save(product);
    }

    /***
     *
     * @return all data from DB, if there is not any data will return empty List.
     */
    @Override
    public List<Product> getAll() {
        List<Product> all = productRepository.findAll();
        Collections.reverse(all);
        return all;
    }

    /***
     *
     * @param id with the help of it will find the object from DB.
     * @return returns founded object or throws @ResponseStatusException(BAD_REQUEST).
     */
    @Override
    public Product getById(long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "product with id:" + id + "  not found in database")
                );
    }

    /***
     *
     * @param id is related to product which need to update
     * @param product is changed data
     * @returns just updated product
     */
    @Transactional
    @Override
    public Product update(long id, Product product) {
        Product fromDb = productRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "product with id:" + id + "  not found in database")
                );
        fromDb.setName(product.getName());
        fromDb.setPrice(product.getPrice());
        fromDb.setCurrency(product.getCurrency());
        fromDb.setDescription(product.getDescription());
        fromDb.setStock(product.getStock());
        fromDb.setSection(product.getSection());

        return fromDb;
    }

    /**
     * @param id need to delete
     *           after deleting product from table,
     *           it automatically deletes all related information from other tables
     */

    @Override
    public void delete(long id) {
        productRepository.deleteById(id);
    }
}


