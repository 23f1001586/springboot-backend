package com.zenbuy.controller;

import com.zenbuy.model.User;
import com.zenbuy.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth2")
@CrossOrigin(origins = "http://localhost:3000")
public class OAuth2Controller {

    private final UserRepository userRepository;

    public OAuth2Controller(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getOAuth2User(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated", "details", "Authentication is null"));
        }
        
        if (!(authentication.getPrincipal() instanceof OAuth2User)) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated", "details", "Principal is not OAuth2User: " + authentication.getPrincipal().getClass().getName()));
        }

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        // Get provider from OAuth2AuthenticationToken
        String provider = null;
        String providerId = null;
        
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            provider = oauth2Token.getAuthorizedClientRegistrationId();
        }
        
        // Extract providerId based on provider
        if (provider != null) {
            switch (provider.toLowerCase()) {
                case "google":
                    providerId = (String) attributes.get("sub");
                    break;
                case "github":
                    providerId = String.valueOf(attributes.get("id"));
                    break;
                case "linkedin":
                    providerId = (String) attributes.get("id");
                    break;
            }
        }
        
        // Try to find user by provider and providerId first (more reliable)
        User user = null;
        if (provider != null && providerId != null) {
            user = userRepository.findByProviderAndProviderId(provider, providerId)
                    .orElse(null);
        }
        
        // Fallback to email lookup if provider lookup fails
        if (user == null) {
            String email = (String) attributes.get("email");
            if (email != null) {
                user = userRepository.findByEmail(email).orElse(null);
            }
        }
        
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of(
                "error", "User not found",
                "details", "Provider: " + provider + ", ProviderId: " + providerId + ", Email: " + attributes.get("email")
            ));
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        if (user.getProfilePic() != null) {
            userData.put("profilePic", user.getProfilePic());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "OAuth2 login successful");
        response.put("user", userData);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/success")
    public ResponseEntity<?> oauth2Success(Authentication authentication) {
        return getOAuth2User(authentication);
    }
}

