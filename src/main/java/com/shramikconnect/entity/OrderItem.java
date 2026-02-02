package com.shramikconnect.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//src/main/java/com/shramikconnect/entity/OrderItem.java
@Entity
@Table(name = "order_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor 
public class OrderItem {
 
 @Id 
 // ✅ Ensure strategy is IDENTITY for MySQL AUTO_INCREMENT
 @GeneratedValue(strategy = GenerationType.IDENTITY) 
 private Integer orderItemId;

 @ManyToOne
 @JoinColumn(name = "order_id")
 @JsonBackReference
 private Order order;

 @ManyToOne(fetch = FetchType.EAGER)
 @JoinColumn(name = "product_id")
 private Product product;

 private Integer quantity;
}