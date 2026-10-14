package com.foodorder.service;

import com.foodorder.dto.Dtos.*;
import com.foodorder.exception.ApiException;
import com.foodorder.model.CartItem;
import com.foodorder.model.FoodItem;
import com.foodorder.model.User;
import com.foodorder.repository.CartItemRepository;
import com.foodorder.repository.FoodItemRepository;
import com.foodorder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartRepo;
    private final FoodItemRepository foodRepo;
    private final UserRepository userRepo;

    @Transactional(readOnly = true)
    public List<CartItemResponse> get(String email) {
        return cartRepo.findByUserId(user(email).getId()).stream().map(this::toDto).toList();
    }

    @Transactional
    public List<CartItemResponse> add(String email, CartRequest r) {
        User u = user(email);
        FoodItem f = foodRepo.findById(r.foodId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Food item not found"));
        if (!f.isAvailable()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, f.getName() + " is currently unavailable");
        }
        CartItem item = cartRepo.findByUserIdAndFoodId(u.getId(), f.getId()).orElseGet(() -> {
            CartItem c = new CartItem();
            c.setUser(u);
            c.setFood(f);
            c.setQuantity(0);
            return c;
        });
        item.setQuantity(item.getQuantity() + r.quantity());
        cartRepo.save(item);
        return get(email);
    }

    @Transactional
    public List<CartItemResponse> updateQuantity(String email, Long itemId, int quantity) {
        CartItem item = owned(email, itemId);
        if (quantity <= 0) {
            cartRepo.delete(item);
        } else {
            item.setQuantity(quantity);
            cartRepo.save(item);
        }
        return get(email);
    }

    @Transactional
    public List<CartItemResponse> remove(String email, Long itemId) {
        cartRepo.delete(owned(email, itemId));
        return get(email);
    }

    @Transactional
    public void clear(String email) {
        cartRepo.deleteByUserId(user(email).getId());
    }

    private CartItem owned(String email, Long itemId) {
        User u = user(email);
        CartItem item = cartRepo.findById(itemId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cart item not found"));
        if (!item.getUser().getId().equals(u.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your cart item");
        }
        return item;
    }

    private User user(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private CartItemResponse toDto(CartItem c) {
        FoodItem f = c.getFood();
        return new CartItemResponse(c.getId(), f.getId(), f.getName(), f.getPrice(),
                f.getImageUrl(), c.getQuantity());
    }
}
