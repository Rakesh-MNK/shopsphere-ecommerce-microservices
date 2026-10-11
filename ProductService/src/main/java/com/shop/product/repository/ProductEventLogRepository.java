package com.shop.product.repository;

import com.shop.product.entity.ProductEventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductEventLogRepository extends JpaRepository<ProductEventLog, Long> {
}
