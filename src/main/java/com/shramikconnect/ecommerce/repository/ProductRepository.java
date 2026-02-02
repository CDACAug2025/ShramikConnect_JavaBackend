package com.shramikconnect.ecommerce.repository;

import com.shramikconnect.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    // ✅ Useful for filtering inventory in the Admin Dashboard
    List<Product> findByCategory(String category);
}