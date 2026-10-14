package com.foodorder.controller;

import com.foodorder.dto.Dtos.*;
import com.foodorder.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public OrderResponse place(Authentication auth, @Valid @RequestBody PlaceOrderRequest r) {
        return orderService.place(auth.getName(), r);
    }

    @GetMapping
    public List<OrderResponse> myOrders(Authentication auth) {
        return orderService.myOrders(auth.getName());
    }

    @GetMapping("/{id}")
    public OrderResponse myOrder(Authentication auth, @PathVariable Long id) {
        return orderService.myOrder(auth.getName(), id);
    }

    @PutMapping("/{id}/cancel")
    public OrderResponse cancel(Authentication auth, @PathVariable Long id) {
        return orderService.cancel(auth.getName(), id);
    }
}
