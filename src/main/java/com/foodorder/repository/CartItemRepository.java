package com.foodorder.repository;

import com.foodorder.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    Optional<CartItem> findByUserIdAndFoodId(Long userId, Long foodId);
    void deleteByUserId(Long userId);
    void deleteByFoodId(Long foodId);
}
