# OAuth2 Setup Instructions

This guide will help you set up OAuth2 authentication with Google, GitHub, and LinkedIn.

## Prerequisites

1. You need to create OAuth2 applications on each provider's developer console
2. Get Client ID and Client Secret from each provider
3. Configure redirect URIs

## Step 1: Google OAuth2 Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable Google+ API
4. Go to "Credentials" → "Create Credentials" → "OAuth 2.0 Client ID"
5. Application type: Web application
6. Authorized redirect URIs: `http://localhost:8080/login/oauth2/code/google`
7. Copy the Client ID and Client Secret

## Step 2: GitHub OAuth2 Setup

1. Go to GitHub Settings → Developer settings → OAuth Apps
2. Click "New OAuth App"
3. Application name: ZENBUY
4. Homepage URL: `http://localhost:3000`
5. Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`
6. Copy the Client ID and Client Secret

## Step 3: LinkedIn OAuth2 Setup

1. Go to [LinkedIn Developers](https://www.linkedin.com/developers/)
2. Create a new app
3. Go to "Auth" tab
4. Add redirect URL: `http://localhost:8080/login/oauth2/code/linkedin`
5. Request access to: `openid`, `profile`, `email`
6. Copy the Client ID and Client Secret

## Step 4: Update application.properties

Update the following in `backend/src/main/resources/application.properties`:

```properties
# Google OAuth2
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET

# GitHub OAuth2
spring.security.oauth2.client.registration.github.client-id=YOUR_GITHUB_CLIENT_ID
spring.security.oauth2.client.registration.github.client-secret=YOUR_GITHUB_CLIENT_SECRET

# LinkedIn OAuth2
spring.security.oauth2.client.registration.linkedin.client-id=YOUR_LINKEDIN_CLIENT_ID
spring.security.oauth2.client.registration.linkedin.client-secret=YOUR_LINKEDIN_CLIENT_SECRET
```

Replace `YOUR_*_CLIENT_ID` and `YOUR_*_CLIENT_SECRET` with the actual values from each provider.

## Step 5: Restart Backend

After updating the properties file, restart your Spring Boot backend application.

## Testing

1. Go to the login or signup page
2. Click on any OAuth button (Google, GitHub, or LinkedIn)
3. You'll be redirected to the provider's login page
4. After authentication, you'll be redirected back to the products page

## Notes

- Make sure your backend is running on port 8080
- Make sure your frontend is running on port 3000
- The redirect URIs must match exactly what you configured in each provider's console
- For production, update the redirect URIs to your production domain

