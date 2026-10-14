package com.foodorder.service;

import com.foodorder.dto.Dtos.*;
import com.foodorder.exception.ApiException;
import com.foodorder.model.*;
import com.foodorder.repository.CartItemRepository;
import com.foodorder.repository.FoodItemRepository;
import com.foodorder.repository.OrderRepository;
import com.foodorder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepo;
    private final CartItemRepository cartRepo;
    private final UserRepository userRepo;
    private final FoodItemRepository foodRepo;

    // ---------- Customer ----------
    @Transactional
    public OrderResponse place(String email, PlaceOrderRequest req) {
        User u = user(email);
        List<CartItem> cart = cartRepo.findByUserId(u.getId());
        if (cart.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Your cart is empty");
        }
        Orders order = new Orders();
        order.setUser(u);
        order.setDeliveryAddress(req.deliveryAddress().trim());
        order.setStatus(OrderStatus.PLACED);
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem c : cart) {
            FoodItem f = c.getFood();
            if (!f.isAvailable()) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        f.getName() + " is no longer available. Please remove it from the cart.");
            }
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setFoodId(f.getId());
            oi.setFoodName(f.getName());
            oi.setPrice(f.getPrice());
            oi.setQuantity(c.getQuantity());
            order.getItems().add(oi);
            total = total.add(f.getPrice().multiply(BigDecimal.valueOf(c.getQuantity())));
        }
        order.setTotal(total);
        orderRepo.save(order);
        cartRepo.deleteByUserId(u.getId());
        return toDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> myOrders(String email) {
        return orderRepo.findByUserIdOrderByCreatedAtDesc(user(email).getId())
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse myOrder(String email, Long id) {
        Orders o = find(id);
        if (!o.getUser().getId().equals(user(email).getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your order");
        }
        return toDto(o);
    }

    @Transactional
    public OrderResponse cancel(String email, Long id) {
        Orders o = find(id);
        if (!o.getUser().getId().equals(user(email).getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your order");
        }
        if (o.getStatus() != OrderStatus.PLACED) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Only orders that are still in PLACED state can be cancelled");
        }
        o.setStatus(OrderStatus.CANCELLED);
        return toDto(orderRepo.save(o));
    }

    // ---------- Admin ----------
    @Transactional(readOnly = true)
    public List<OrderResponse> allOrders() {
        return orderRepo.findAllByOrderByCreatedAtDesc().stream().map(this::toDto).toList();
    }

    @Transactional
    public OrderResponse updateStatus(Long id, String status) {
        OrderStatus s;
        try {
            s = OrderStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
        }
        Orders o = find(id);
        o.setStatus(s);
        return toDto(orderRepo.save(o));
    }

    @Transactional(readOnly = true)
    public StatsResponse stats() {
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (OrderStatus s : OrderStatus.values()) {
            byStatus.put(s.name(), orderRepo.countByStatus(s));
        }
        return new StatsResponse(orderRepo.count(), orderRepo.totalRevenue(),
                userRepo.countByRole(Role.CUSTOMER), foodRepo.count(), byStatus);
    }

    @Transactional(readOnly = true)
    public List<User> customers() {
        return userRepo.findByRole(Role.CUSTOMER);
    }

    // ---------- helpers ----------
    private Orders find(Long id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    private User user(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private OrderResponse toDto(Orders o) {
        List<OrderItemResponse> items = o.getItems().stream()
                .map(i -> new OrderItemResponse(i.getFoodName(), i.getPrice(), i.getQuantity()))
                .toList();
        return new OrderResponse(o.getId(), o.getTotal(), o.getStatus().name(), o.getCreatedAt(),
                o.getDeliveryAddress(), o.getUser().getName(), o.getUser().getEmail(), items);
    }
}
