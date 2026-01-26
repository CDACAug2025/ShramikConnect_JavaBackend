package com.shramikconnect.modules.subscription.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "plans")
@Data
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;       
    private Double price;
    
    @Column(name = "`interval`") // Your database fix for the reserved keyword
    private String interval;   
    
    private Boolean isActive;  

    // FIX: Load features immediately so JSON conversion doesn't fail
    @ElementCollection(fetch = FetchType.EAGER) 
    @CollectionTable(name = "plan_features", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "feature")
    private List<String> features; 
}