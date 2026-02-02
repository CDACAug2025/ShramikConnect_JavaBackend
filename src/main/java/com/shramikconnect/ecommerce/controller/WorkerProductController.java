package com.shramikconnect.ecommerce.controller;

import com.shramikconnect.ecommerce.service.ProductService;
import com.shramikconnect.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/worker/products")
@CrossOrigin(origins = "http://localhost:5173")
public class WorkerProductController {

    @Autowired
    private ProductService service;

    @GetMapping
    public List<Product> getLiveProducts() {
        // ✅ This returns the list currently in the MySQL database
        return service.getAllProducts();
    }
}