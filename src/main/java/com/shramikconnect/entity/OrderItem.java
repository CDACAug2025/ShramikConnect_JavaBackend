package com.shramikconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor 
public class OrderItem {
    @Id @GeneratedValue
    private Integer orderItemId;

    @ManyToOne
    private Order order;

    @ManyToOne
    private Product product;

    private Integer quantity;
}
