package com.shramikconnect.modules.ecommerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shramikconnect.modules.ecommerce.entity.Product;
import com.shramikconnect.modules.ecommerce.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product addProduct(Product product) {
        // Set default image if none provided
        if (product.getImage() == null || product.getImage().isEmpty()) {
            product.setImage("https://via.placeholder.com/40"); // <--- CHANGE THIS to .setImage
        }
        return productRepository.save(product);
    }

    public Product updateStock(Long id, Integer newStock) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        product.setStock(newStock);
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}	