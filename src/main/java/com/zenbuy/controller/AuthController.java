package com.zenbuy.controller;

import com.zenbuy.model.User;
import com.zenbuy.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            User user = userService.signup(
                request.getName(), 
                request.getEmail(), 
                request.getPassword(),
                request.getProfilePic(),
                request.getFlatNo(),
                request.getLocality(),
                request.getCity(),
                request.getPincode(),
                request.getAge()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User created successfully");
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());
            if (user.getProfilePic() != null) userData.put("profilePic", user.getProfilePic());
            if (user.getAddress() != null) userData.put("address", user.getAddress());
            if (user.getFlatNo() != null) userData.put("flatNo", user.getFlatNo());
            if (user.getLocality() != null) userData.put("locality", user.getLocality());
            if (user.getCity() != null) userData.put("city", user.getCity());
            if (user.getPincode() != null) userData.put("pincode", user.getPincode());
            if (user.getAge() != null) userData.put("age", user.getAge());
            response.put("user", userData);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request.getEmail(), request.getPassword());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());
            userData.put("role", user.getRole());
            if (user.getProfilePic() != null) userData.put("profilePic", user.getProfilePic());
            if (user.getFlatNo() != null) userData.put("flatNo", user.getFlatNo());
            if (user.getLocality() != null) userData.put("locality", user.getLocality());
            if (user.getCity() != null) userData.put("city", user.getCity());
            if (user.getPincode() != null) userData.put("pincode", user.getPincode());
            if (user.getAge() != null) userData.put("age", user.getAge());
            response.put("user", userData);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest request) {
        try {
            User user = userService.adminLogin(request.getEmail(), request.getPassword());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Admin login successful");
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());
            userData.put("role", user.getRole());
            if (user.getProfilePic() != null) userData.put("profilePic", user.getProfilePic());
            response.put("user", userData);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId, @RequestBody UpdateProfileRequest request) {
        try {
            User user = userService.updateProfile(
                userId,
                request.getName(),
                request.getEmail(),
                request.getProfilePic(),
                request.getFlatNo(),
                request.getLocality(),
                request.getCity(),
                request.getPincode(),
                request.getAge()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profile updated successfully");
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());
            if (user.getProfilePic() != null) userData.put("profilePic", user.getProfilePic());
            if (user.getFlatNo() != null) userData.put("flatNo", user.getFlatNo());
            if (user.getLocality() != null) userData.put("locality", user.getLocality());
            if (user.getCity() != null) userData.put("city", user.getCity());
            if (user.getPincode() != null) userData.put("pincode", user.getPincode());
            if (user.getAge() != null) userData.put("age", user.getAge());
            if (user.getAddress() != null) userData.put("address", user.getAddress());
            response.put("user", userData);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // Inner classes for request DTOs
    static class SignupRequest {
        private String name;
        private String email;
        private String password;
        private String profilePic;
        private String flatNo;
        private String locality;
        private String city;
        private String pincode;
        private Integer age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public String getFlatNo() {
            return flatNo;
        }

        public void setFlatNo(String flatNo) {
            this.flatNo = flatNo;
        }

        public String getLocality() {
            return locality;
        }

        public void setLocality(String locality) {
            this.locality = locality;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }

    static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    static class UpdateProfileRequest {
        private String name;
        private String email;
        private String profilePic;
        private String flatNo;
        private String locality;
        private String city;
        private String pincode;
        private Integer age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public String getFlatNo() {
            return flatNo;
        }

        public void setFlatNo(String flatNo) {
            this.flatNo = flatNo;
        }

        public String getLocality() {
            return locality;
        }

        public void setLocality(String locality) {
            this.locality = locality;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}

