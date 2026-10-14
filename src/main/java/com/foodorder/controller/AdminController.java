package com.foodorder.controller;

import com.foodorder.dto.Dtos.*;
import com.foodorder.model.Category;
import com.foodorder.model.FoodItem;
import com.foodorder.model.User;
import com.foodorder.service.FoodService;
import com.foodorder.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** All endpoints under /api/admin require the ADMIN role (see SecurityConfig). */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final FoodService foodService;
    private final OrderService orderService;

    // ----- food -----
    @PostMapping("/foods")
    public FoodItem createFood(@Valid @RequestBody FoodRequest r) {
        return foodService.create(r);
    }

    @PutMapping("/foods/{id}")
    public FoodItem updateFood(@PathVariable Long id, @Valid @RequestBody FoodRequest r) {
        return foodService.update(id, r);
    }

    @DeleteMapping("/foods/{id}")
    public void deleteFood(@PathVariable Long id) {
        foodService.delete(id);
    }

    // ----- categories -----
    @PostMapping("/categories")
    public Category createCategory(@Valid @RequestBody CategoryRequest r) {
        return foodService.createCategory(r);
    }

    @PutMapping("/categories/{id}")
    public Category updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest r) {
        return foodService.updateCategory(id, r);
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable Long id) {
        foodService.deleteCategory(id);
    }

    // ----- orders, customers, statistics -----
    @GetMapping("/orders")
    public List<OrderResponse> orders() {
        return orderService.allOrders();
    }

    @PutMapping("/orders/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest r) {
        return orderService.updateStatus(id, r.status());
    }

    @GetMapping("/customers")
    public List<User> customers() {
        return orderService.customers();
    }

    @GetMapping("/stats")
    public StatsResponse stats() {
        return orderService.stats();
    }
}
