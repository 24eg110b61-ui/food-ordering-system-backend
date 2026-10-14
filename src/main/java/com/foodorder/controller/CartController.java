package com.foodorder.controller;

import com.foodorder.dto.Dtos.*;
import com.foodorder.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public List<CartItemResponse> get(Authentication auth) {
        return cartService.get(auth.getName());
    }

    @PostMapping
    public List<CartItemResponse> add(Authentication auth, @Valid @RequestBody CartRequest r) {
        return cartService.add(auth.getName(), r);
    }

    @PutMapping("/{itemId}")
    public List<CartItemResponse> update(Authentication auth, @PathVariable Long itemId,
                                         @Valid @RequestBody QuantityRequest r) {
        return cartService.updateQuantity(auth.getName(), itemId, r.quantity());
    }

    @DeleteMapping("/{itemId}")
    public List<CartItemResponse> remove(Authentication auth, @PathVariable Long itemId) {
        return cartService.remove(auth.getName(), itemId);
    }

    @DeleteMapping
    public void clear(Authentication auth) {
        cartService.clear(auth.getName());
    }
}
