package com.shramikconnect.modules.ecommerce.repository;

import com.shramikconnect.modules.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // We can add "findByCategory" or "findByStockLessThan" later if needed
}