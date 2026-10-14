package com.foodorder.repository;

import com.foodorder.model.OrderStatus;
import com.foodorder.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Orders> findAllByOrderByCreatedAtDesc();
    long countByStatus(OrderStatus status);

    @Query("select coalesce(sum(o.total), 0) from Orders o "
         + "where o.status <> com.foodorder.model.OrderStatus.CANCELLED")
    BigDecimal totalRevenue();
}
