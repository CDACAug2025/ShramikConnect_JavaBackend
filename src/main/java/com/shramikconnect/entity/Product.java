package com.shramikconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Forces Hibernate to let MySQL handle ID generation
    private Integer productId;

    private String name;
    private String category;
    private Double price;
    private Integer stock;
    private String image;
}