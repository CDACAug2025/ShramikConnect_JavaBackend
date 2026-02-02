package com.shramikconnect.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shramikconnect.ecommerce.service.ProductService;
import com.shramikconnect.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    @Autowired
    private ProductService service;

    private final String UPLOAD_DIR = "src/main/resources/static/images/";

    @GetMapping
    public List<Product> getAll() {
        return service.getAllProducts();
    }

    // ✅ Consumes MULTIPART_FORM_DATA_VALUE is vital here
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Product addProduct(
            @RequestPart("product") Product product, // Spring now auto-deserializes the JSON Blob
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        
        handleImageUpload(product, imageFile);
        return service.saveProduct(product);
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Product> updateProduct(
            @PathVariable Integer id,
            @RequestPart("product") Product details,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        
        Product existing = service.getProductById(id);
        existing.setName(details.getName());
        existing.setCategory(details.getCategory());
        existing.setPrice(details.getPrice());
        existing.setStock(details.getStock());

        if (imageFile != null && !imageFile.isEmpty()) {
            handleImageUpload(existing, imageFile);
        }

        return ResponseEntity.ok(service.saveProduct(existing));
    }

    private void handleImageUpload(Product product, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.createDirectories(path.getParent());
            Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            product.setImage("http://localhost:8080/images/" + fileName);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        service.deleteProduct(id);
        return ResponseEntity.ok("Deleted successfully.");
    }
}