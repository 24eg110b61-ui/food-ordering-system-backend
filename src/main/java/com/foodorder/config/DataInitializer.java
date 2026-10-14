package com.foodorder.config;

import com.foodorder.model.*;
import com.foodorder.repository.CategoryRepository;
import com.foodorder.repository.FoodItemRepository;
import com.foodorder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/** Seeds a default admin account and sample menu on first start. */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;
    private final FoodItemRepository foodRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (!userRepo.existsByEmail("admin@food.com")) {
            User admin = new User();
            admin.setName("Administrator");
            admin.setEmail("admin@food.com");
            admin.setPassword(encoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepo.save(admin);
        }
        if (categoryRepo.count() > 0) return;

        Category pizza = cat("Pizza", "Wood-fired and cheesy");
        Category burger = cat("Burgers", "Juicy stacked burgers");
        Category indian = cat("Indian Mains", "Curries and biryani");
        Category drinks = cat("Drinks", "Cool and refreshing");
        Category dessert = cat("Desserts", "Something sweet");

        food("Margherita Pizza", "Classic tomato, mozzarella and basil", "249", pizza);
        food("Pepperoni Pizza", "Loaded with spicy pepperoni", "329", pizza);
        food("Veg Burger", "Crispy veg patty with fresh veggies", "149", burger);
        food("Chicken Burger", "Grilled chicken patty with mayo", "189", burger);
        food("Paneer Butter Masala", "Creamy tomato gravy with paneer", "279", indian);
        food("Chicken Biryani", "Hyderabadi dum biryani with raita", "299", indian);
        food("Cold Coffee", "Chilled coffee with ice cream", "119", drinks);
        food("Fresh Lime Soda", "Sweet or salted", "79", drinks);
        food("Gulab Jamun", "Two warm gulab jamuns", "99", dessert);
        food("Chocolate Brownie", "Fudgy brownie with chocolate sauce", "129", dessert);
    }

    private Category cat(String name, String desc) {
        Category c = new Category();
        c.setName(name);
        c.setDescription(desc);
        return categoryRepo.save(c);
    }

    private void food(String name, String desc, String price, Category category) {
        FoodItem f = new FoodItem();
        f.setName(name);
        f.setDescription(desc);
        f.setPrice(new BigDecimal(price));
        f.setCategory(category);
        f.setAvailable(true);
        foodRepo.save(f);
    }
}
