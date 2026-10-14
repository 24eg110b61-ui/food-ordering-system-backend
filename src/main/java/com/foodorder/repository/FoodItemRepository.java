package com.foodorder.repository;

import com.foodorder.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    @Query("""
           select f from FoodItem f
           where lower(f.name) like lower(concat('%', :q, '%'))
             and (:categoryId is null or f.category.id = :categoryId)
           order by f.name
           """)
    List<FoodItem> search(@Param("q") String q, @Param("categoryId") Long categoryId);

    boolean existsByCategoryId(Long categoryId);
}
