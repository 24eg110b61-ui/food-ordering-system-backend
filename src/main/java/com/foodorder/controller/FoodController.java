package com.foodorder.controller;

import com.foodorder.model.Category;
import com.foodorder.model.FoodItem;
import com.foodorder.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** Public (read-only) menu endpoints. */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    @GetMapping("/foods")
    public List<FoodItem> foods(@RequestParam(required = false) String q,
                                @RequestParam(required = false) Long categoryId) {
        return foodService.search(q, categoryId);
    }

    @GetMapping("/foods/{id}")
    public FoodItem food(@PathVariable Long id) {
        return foodService.get(id);
    }

    @GetMapping("/categories")
    public List<Category> categories() {
        return foodService.categories();
    }
}
