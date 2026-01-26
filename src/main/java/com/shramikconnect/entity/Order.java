package com.shramikconnect.entity;
import com.shramikconnect.common.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor 
public class Order {
    @Id @GeneratedValue
    private Integer orderId;

    @ManyToOne
    private User buyer;

    private Double totalAmount;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

}

