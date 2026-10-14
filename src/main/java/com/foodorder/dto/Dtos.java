package com.foodorder.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Dtos {

    public record RegisterRequest(
            @NotBlank(message = "Name is required") String name,
            @NotBlank(message = "Email is required") @Email(message = "Invalid email") String email,
            @NotBlank @Size(min = 6, message = "Password must be at least 6 characters") String password,
            String phone,
            String address) {}

    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}

    public record AuthResponse(String token, Long id, String name, String email,
                               String phone, String address, String role) {}

    public record FoodRequest(
            @NotBlank(message = "Food name is required") String name,
            String description,
            @NotNull(message = "Price is required") @DecimalMin(value = "0.0", message = "Price must be >= 0") BigDecimal price,
            String imageUrl,
            boolean available,
            @NotNull(message = "Category is required") Long categoryId) {}

    public record CategoryRequest(@NotBlank(message = "Category name is required") String name,
                                  String description) {}

    public record CartRequest(@NotNull Long foodId, @Min(1) int quantity) {}

    public record QuantityRequest(@Min(0) int quantity) {}

    public record CartItemResponse(Long id, Long foodId, String name, BigDecimal price,
                                   String imageUrl, int quantity) {}

    public record PlaceOrderRequest(@NotBlank(message = "Delivery address is required") String deliveryAddress) {}

    public record OrderItemResponse(String name, BigDecimal price, int quantity) {}

    public record OrderResponse(Long id, BigDecimal total, String status, LocalDateTime createdAt,
                                String deliveryAddress, String customerName, String customerEmail,
                                List<OrderItemResponse> items) {}

    public record StatusRequest(@NotBlank String status) {}

    public record StatsResponse(long totalOrders, BigDecimal totalRevenue, long totalCustomers,
                                long totalFoodItems, Map<String, Long> ordersByStatus) {}
}
