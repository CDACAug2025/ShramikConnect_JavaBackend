package com.shramikconnect.modules.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category; // e.g., "Safety Gear", "Tools"
    private Double price;
    private Integer stock;
    
    @Column(length = 1000)
    private String description;
    
    private String image; // Stores URL string (frontend sends placeholder for now)

    // Logic to determine if product is available
    public boolean isAvailable() {
        return stock != null && stock > 0;
    }
}