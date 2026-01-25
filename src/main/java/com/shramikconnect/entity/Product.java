package com.shramikconnect.entity;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Product {
    @Id @GeneratedValue
    private Integer productId;
    private String name;
    private Double price;
    private Integer stock;
}

