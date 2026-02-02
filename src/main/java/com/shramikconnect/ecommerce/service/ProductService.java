package com.shramikconnect.ecommerce.service;

import com.shramikconnect.entity.Product;
import com.shramikconnect.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    /**
     * Retrieves all products for the administrative registry.
     */
    public List<Product> getAllProducts() {
        return repository.findAll();
    }
    /**
     * Finds a specific product by ID; throws an exception if not found.
     */
    public Product getProductById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found with ID: " + id));
    }

    /**
     * Saves or updates a product in the database.
     */
    public Product saveProduct(Product product) {
        return repository.save(product);
    }

    /**
     * Deletes a product from the inventory registry.
     */
    public void deleteProduct(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Cannot delete: Asset ID " + id + " does not exist.");
        }
        repository.deleteById(id);
    }
}