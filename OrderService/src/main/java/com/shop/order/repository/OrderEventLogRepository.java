package com.shop.order.repository;

import com.shop.order.entity.OrderEventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderEventLogRepository extends JpaRepository<OrderEventLog, Long> {
}
