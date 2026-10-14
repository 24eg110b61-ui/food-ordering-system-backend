package com.foodorder.service;

import com.foodorder.dto.Dtos.*;
import com.foodorder.exception.ApiException;
import com.foodorder.model.Category;
import com.foodorder.model.FoodItem;
import com.foodorder.repository.CartItemRepository;
import com.foodorder.repository.CategoryRepository;
import com.foodorder.repository.FoodItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodItemRepository foodRepo;
    private final CategoryRepository categoryRepo;
    private final CartItemRepository cartRepo;

    // ---------- Food ----------
    public List<FoodItem> search(String q, Long categoryId) {
        return foodRepo.search(q == null ? "" : q.trim(), categoryId);
    }

    public FoodItem get(Long id) {
        return foodRepo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Food item not found"));
    }

    @Transactional
    public FoodItem create(FoodRequest r) {
        return foodRepo.save(apply(new FoodItem(), r));
    }

    @Transactional
    public FoodItem update(Long id, FoodRequest r) {
        return foodRepo.save(apply(get(id), r));
    }

    @Transactional
    public void delete(Long id) {
        get(id);
        cartRepo.deleteByFoodId(id);
        foodRepo.deleteById(id);
    }

    private FoodItem apply(FoodItem f, FoodRequest r) {
        f.setName(r.name().trim());
        f.setDescription(r.description());
        f.setPrice(r.price());
        f.setImageUrl(r.imageUrl() == null || r.imageUrl().isBlank() ? null : r.imageUrl().trim());
        f.setAvailable(r.available());
        f.setCategory(getCategory(r.categoryId()));
        return f;
    }

    // ---------- Categories ----------
    public List<Category> categories() {
        return categoryRepo.findAll();
    }

    public Category getCategory(Long id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    @Transactional
    public Category createCategory(CategoryRequest r) {
        if (categoryRepo.existsByNameIgnoreCase(r.name().trim())) {
            throw new ApiException(HttpStatus.CONFLICT, "Category already exists");
        }
        Category c = new Category();
        c.setName(r.name().trim());
        c.setDescription(r.description());
        return categoryRepo.save(c);
    }

    @Transactional
    public Category updateCategory(Long id, CategoryRequest r) {
        Category c = getCategory(id);
        c.setName(r.name().trim());
        c.setDescription(r.description());
        return categoryRepo.save(c);
    }

    @Transactional
    public void deleteCategory(Long id) {
        getCategory(id);
        if (foodRepo.existsByCategoryId(id)) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Cannot delete a category that still has food items");
        }
        categoryRepo.deleteById(id);
    }
}
