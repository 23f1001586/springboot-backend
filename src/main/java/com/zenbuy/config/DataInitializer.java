package com.zenbuy.config;

import com.zenbuy.model.Product;
import com.zenbuy.model.User;
import com.zenbuy.repository.ProductRepository;
import com.zenbuy.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(ProductRepository productRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Check if products already exist
        long existingCount = productRepository.count();
        System.out.println("Existing products in database: " + existingCount);
        
        // Always initialize products (will skip if they already exist)
        List<Product> products = Arrays.asList(
                // Electronics
                createProduct("Laptop", "High-performance laptop with latest processor and fast SSD storage", 59999.00, "/image.png", "Electronics", 12),
                createProduct("Wireless Bluetooth Headphones", "Premium noise-cancelling headphones with 30-hour battery life", 2999.00, "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500", "Electronics", 25),
                createProduct("Smart Watch Pro", "Fitness tracking smartwatch with heart rate monitor and GPS", 8999.00, "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500", "Electronics", 15),
                createProduct("Wireless Mouse", "Ergonomic wireless mouse with 2-year battery life", 599.00, "https://images.unsplash.com/photo-1527814050087-3793815479db?w=500", "Electronics", 50),
                createProduct("USB-C Charging Cable", "Fast charging cable compatible with all USB-C devices", 299.00, "https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04?w=500", "Electronics", 100),
                createProduct("Laptop Stand", "Adjustable aluminum laptop stand for better ergonomics", 1299.00, "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=500", "Electronics", 30),
                createProduct("Mechanical Keyboard", "RGB backlit mechanical keyboard with blue switches", 4499.00, "https://images.unsplash.com/photo-1541140532154-b024d705b90a?w=500", "Electronics", 20),
                
                // Fashion
                createProduct("Cotton T-Shirt", "100% organic cotton t-shirt in multiple colors", 499.00, "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=500", "Fashion", 75),
                createProduct("Denim Jeans", "Classic fit denim jeans with stretch comfort", 1999.00, "https://images.unsplash.com/photo-1542272604-787c3835535d?w=500", "Fashion", 40),
                createProduct("Running Shoes", "Lightweight running shoes with cushioned sole", 3499.00, "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500", "Fashion", 35),
                createProduct("Leather Wallet", "Genuine leather wallet with RFID blocking", 899.00, "https://images.unsplash.com/photo-1627123424574-724758594e93?w=500", "Fashion", 60),
                createProduct("Sunglasses", "UV protection sunglasses with polarized lenses", 1299.00, "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=500", "Fashion", 45),
                createProduct("Backpack", "Waterproof backpack with laptop compartment", 2499.00, "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500", "Fashion", 25),
                
                // Home & Kitchen
                createProduct("Coffee Maker", "Programmable coffee maker with thermal carafe", 3999.00, "https://images.unsplash.com/photo-1517668808823-f8f30c0c58f4?w=500", "Home & Kitchen", 18),
                createProduct("Air Fryer", "5.5L capacity air fryer with digital display", 4999.00, "https://images.unsplash.com/photo-1556912172-45b7abe8b7e1?w=500", "Home & Kitchen", 12),
                createProduct("Blender", "High-speed blender for smoothies and soups", 2999.00, "https://images.unsplash.com/photo-1574269909862-7e1d70bb8078?w=500", "Home & Kitchen", 22),
                createProduct("Dinner Set", "Ceramic dinner set for 6 people", 2499.00, "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=500", "Home & Kitchen", 30),
                createProduct("Bed Sheets", "Premium cotton bed sheets set (King size)", 1799.00, "https://images.unsplash.com/photo-1586075010923-2dd45780fb98?w=500", "Home & Kitchen", 50),
                createProduct("Wall Clock", "Modern minimalist wall clock with silent movement", 899.00, "https://images.unsplash.com/photo-1493612276216-ee3925520721?w=500", "Home & Kitchen", 40),
                
                // Books
                createProduct("The Great Novel", "Bestselling fiction novel by acclaimed author", 599.00, "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=500", "Books", 100),
                createProduct("Programming Guide", "Complete guide to modern programming languages", 1299.00, "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=500", "Books", 45),
                createProduct("Cookbook Collection", "500 recipes from around the world", 899.00, "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=500", "Books", 60),
                createProduct("Self-Help Book", "Transform your life with practical advice", 499.00, "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500", "Books", 80),
                
                // Sports & Outdoors
                createProduct("Yoga Mat", "Non-slip yoga mat with carrying strap", 799.00, "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=500", "Sports & Outdoors", 55),
                createProduct("Dumbbells Set", "Adjustable dumbbells set (5-25kg)", 4999.00, "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=500", "Sports & Outdoors", 20),
                createProduct("Bicycle", "Mountain bike with 21-speed gear system", 12999.00, "https://images.unsplash.com/photo-1558618048-5c3e8b0c5c8b?w=500", "Sports & Outdoors", 8),
                createProduct("Tennis Racket", "Professional tennis racket with carbon fiber frame", 3999.00, "https://images.unsplash.com/photo-1622163642993-408996b0e0e2?w=500", "Sports & Outdoors", 25),
                
                // Beauty & Personal Care
                createProduct("Face Moisturizer", "Hydrating face cream with SPF 30", 899.00, "https://images.unsplash.com/photo-1556229010-6c3f2c9ca5f8?w=500", "Beauty & Personal Care", 70),
                createProduct("Shampoo & Conditioner Set", "Natural hair care set for all hair types", 599.00, "https://images.unsplash.com/photo-1556228578-0d85b1a4d571?w=500", "Beauty & Personal Care", 90),
                createProduct("Electric Toothbrush", "Sonic electric toothbrush with 3 modes", 2499.00, "https://images.unsplash.com/photo-1607613009820-a29f7bb81c04?w=500", "Beauty & Personal Care", 40),
                createProduct("Perfume", "Long-lasting fragrance for men", 1999.00, "https://images.unsplash.com/photo-1541643600914-78b084683601?w=500", "Beauty & Personal Care", 35),
                
                // Toys & Games
                createProduct("Board Game", "Strategy board game for 2-4 players", 1299.00, "https://images.unsplash.com/photo-1606092195730-5d7b9af1efc5?w=500", "Toys & Games", 28),
                createProduct("Puzzle Set", "1000-piece jigsaw puzzle", 499.00, "https://images.unsplash.com/photo-1606092195730-5d7b9af1efc5?w=500", "Toys & Games", 45),
                createProduct("Remote Control Car", "RC car with 2.4GHz remote control", 1999.00, "https://images.unsplash.com/photo-1606092195730-5d7b9af1efc5?w=500", "Toys & Games", 30),
                
                // Health & Wellness
                createProduct("Protein Powder", "Whey protein powder (2kg)", 2499.00, "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=500", "Health & Wellness", 42),
                createProduct("Vitamin Supplements", "Multivitamin tablets (60 tablets)", 699.00, "https://images.unsplash.com/photo-1559757148-5c0d30698e7b?w=500", "Health & Wellness", 65),
                createProduct("Yoga Block", "Eco-friendly cork yoga block set (2 pieces)", 599.00, "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=500", "Health & Wellness", 50),
                
                // Accessories
                createProduct("Phone Case", "Protective phone case with shock absorption", 399.00, "https://images.unsplash.com/photo-1601972602237-8c79241e468b?w=500", "Accessories", 120),
                createProduct("Power Bank", "10000mAh portable power bank", 1299.00, "https://images.unsplash.com/photo-1609091839311-d5365f9ff1a8?w=500", "Accessories", 38),
                createProduct("Car Phone Mount", "Magnetic car mount for smartphones", 499.00, "https://images.unsplash.com/photo-1601972602237-8c79241e468b?w=500", "Accessories", 55),
                
                // Food & Beverages
                createProduct("Organic Green Tea", "Premium organic green tea (100 tea bags)", 299.00, "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=500", "Food & Beverages", 200),
                createProduct("Dark Chocolate Box", "Assorted dark chocolates (500g)", 599.00, "https://images.unsplash.com/photo-1606312619070-d48b4e001e46?w=500", "Food & Beverages", 85),
                createProduct("Coffee Beans", "Arabica coffee beans (500g)", 899.00, "https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=500", "Food & Beverages", 60)
        );

        // Get existing product names for efficient duplicate checking
        Set<String> existingNames = new HashSet<>();
        productRepository.findAll().forEach(p -> {
            if (p.getName() != null) {
                existingNames.add(p.getName());
            }
        });
        
        // Save products (skip if they already exist)
        int savedCount = 0;
        for (Product product : products) {
            if (product.getName() != null && !existingNames.contains(product.getName())) {
                productRepository.save(product);
                existingNames.add(product.getName()); // Add to set to avoid duplicates in same run
                savedCount++;
            }
        }
        
        System.out.println("Products initialization complete. Added " + savedCount + " new products. Total products: " + productRepository.count());
        
        // Initialize admin user if it doesn't exist
        initializeAdminUser();
    }
    
    private void initializeAdminUser() {
        String adminEmail = "admin@zenbuy.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("admin123")); // Default password
            admin.setName("Administrator");
            admin.setRole("ADMIN");
            admin.setProvider("local");
            userRepository.save(admin);
            System.out.println("Admin user created: " + adminEmail + " / admin123");
        } else {
            System.out.println("Admin user already exists");
        }
    }

    private Product createProduct(String name, String description, double price, String imageUrl, String category, int stockQuantity) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setImageUrl(imageUrl);
        product.setCategory(category);
        product.setStockQuantity(stockQuantity);
        return product;
    }
}

