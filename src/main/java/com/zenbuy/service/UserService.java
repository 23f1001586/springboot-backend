package com.zenbuy.service;

import com.zenbuy.model.User;
import com.zenbuy.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(String name, String email, String password, String profilePic, String flatNo, String locality, String city, String pincode, Integer age) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setProfilePic(profilePic);
        user.setFlatNo(flatNo);
        user.setLocality(locality);
        user.setCity(city);
        user.setPincode(pincode);
        
        // Combine address fields into a formatted address string
        StringBuilder addressBuilder = new StringBuilder();
        if (flatNo != null && !flatNo.trim().isEmpty()) {
            addressBuilder.append(flatNo.trim());
        }
        if (locality != null && !locality.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(locality.trim());
        }
        if (city != null && !city.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(city.trim());
        }
        if (pincode != null && !pincode.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(" - ");
            addressBuilder.append(pincode.trim());
        }
        user.setAddress(addressBuilder.length() > 0 ? addressBuilder.toString() : null);
        
        user.setAge(age);
        user.setProvider("local");
        user.setRole("USER"); // Default role for new users

        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Check if user is a local user (has password)
        if (user.getPassword() == null || !"local".equals(user.getProvider())) {
            throw new RuntimeException("This account uses social login. Please sign in with your OAuth provider.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return user;
    }

    public User updateProfile(Long userId, String name, String email, String profilePic, String flatNo, String locality, String city, String pincode, Integer age) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
        }
        
        // Email cannot be changed, so we don't update it
        
        if (profilePic != null) {
            user.setProfilePic(profilePic);
        }
        
        user.setFlatNo(flatNo);
        user.setLocality(locality);
        user.setCity(city);
        user.setPincode(pincode);
        user.setAge(age);
        
        // Combine address fields into a formatted address string
        StringBuilder addressBuilder = new StringBuilder();
        if (flatNo != null && !flatNo.trim().isEmpty()) {
            addressBuilder.append(flatNo.trim());
        }
        if (locality != null && !locality.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(locality.trim());
        }
        if (city != null && !city.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(", ");
            addressBuilder.append(city.trim());
        }
        if (pincode != null && !pincode.trim().isEmpty()) {
            if (addressBuilder.length() > 0) addressBuilder.append(" - ");
            addressBuilder.append(pincode.trim());
        }
        user.setAddress(addressBuilder.length() > 0 ? addressBuilder.toString() : null);

        return userRepository.save(user);
    }

    public User adminLogin(String email, String password) {
        User user = login(email, password); // Reuse login logic
        
        if (!"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("Access denied. Admin credentials required.");
        }
        
        return user;
    }
}

