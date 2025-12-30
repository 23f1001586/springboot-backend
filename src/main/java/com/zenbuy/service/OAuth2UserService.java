package com.zenbuy.service;

import com.zenbuy.model.User;
import com.zenbuy.repository.UserRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public OAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        processOAuth2User(userRequest, oAuth2User);
        return oAuth2User;
    }

    private void processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        String email = null;
        String name = null;
        String providerId = null;

        switch (provider.toLowerCase()) {
            case "google":
                email = (String) attributes.get("email");
                name = (String) attributes.get("name");
                providerId = (String) attributes.get("sub");
                break;
            case "github":
                // GitHub doesn't return email in user attributes by default
                // Need to make a separate API call to get email
                email = (String) attributes.get("email");
                if (email == null) {
                    email = fetchGitHubEmail(userRequest);
                }
                name = (String) attributes.get("name");
                if (name == null) {
                    name = (String) attributes.get("login");
                }
                providerId = String.valueOf(attributes.get("id"));
                break;
            case "linkedin":
                @SuppressWarnings("unchecked")
                Map<String, Object> nameObj = (Map<String, Object>) attributes.get("name");
                if (nameObj != null) {
                    name = (String) nameObj.get("givenName");
                    String lastName = (String) nameObj.get("familyName");
                    if (lastName != null) {
                        name = name + " " + lastName;
                    }
                }
                email = (String) attributes.get("email");
                providerId = (String) attributes.get("id");
                break;
        }

        if (email == null || providerId == null) {
            throw new OAuth2AuthenticationException("Unable to extract user information from " + provider);
        }

        // Check if user exists by provider
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElse(null);

        if (user == null) {
            // Check if user exists by email (for linking accounts)
            user = userRepository.findByEmail(email).orElse(null);
            
            if (user == null) {
                // Create new user
                user = new User();
                user.setEmail(email);
                user.setName(name != null ? name : email);
                user.setProvider(provider);
                user.setProviderId(providerId);
                user.setPassword(null); // OAuth users don't have passwords
            } else {
                // Link OAuth account to existing user
                user.setProvider(provider);
                user.setProviderId(providerId);
            }
        } else {
            // Update existing OAuth user
            if (name != null && !name.equals(user.getName())) {
                user.setName(name);
            }
        }

        userRepository.save(user);
    }

    @SuppressWarnings("unchecked")
    private String fetchGitHubEmail(OAuth2UserRequest userRequest) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(userRequest.getAccessToken().getTokenValue());
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List> response = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                entity,
                List.class
            );
            
            List<?> body = response.getBody();
            if (body != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> emails = (List<Map<String, Object>>) body;
                // Find primary email or first verified email
                if (emails != null) {
                    for (Map<String, Object> emailObj : emails) {
                        Boolean primary = (Boolean) emailObj.get("primary");
                        Boolean verified = (Boolean) emailObj.get("verified");
                        if (primary != null && primary && verified != null && verified) {
                            return (String) emailObj.get("email");
                        }
                    }
                    // If no primary email found, get first verified email
                    for (Map<String, Object> emailObj : emails) {
                        Boolean verified = (Boolean) emailObj.get("verified");
                        if (verified != null && verified) {
                            return (String) emailObj.get("email");
                        }
                    }
                    // Last resort: get first email
                    if (!emails.isEmpty()) {
                        return (String) emails.get(0).get("email");
                    }
                }
            }
        } catch (Exception e) {
            // Log error but don't fail authentication
            System.err.println("Failed to fetch GitHub email: " + e.getMessage());
        }
        return null;
    }
}

