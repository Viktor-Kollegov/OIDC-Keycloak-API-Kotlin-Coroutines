package com.example.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class AuthClient {

    private final RestTemplate restTemplate;
    private final String authServerUrl;
    private final String resourceServerUrl;
    private final String clientId;
    private final String redirectUri;

    public AuthClient(RestTemplate restTemplate,
                      @Value("${auth.server.url:http://localhost:9000}") String authServerUrl,
                      @Value("${resource.server.url:http://localhost:8081}") String resourceServerUrl,
                      @Value("${client.id:client-app1}") String clientId,
                      @Value("${client.redirect-uri:http://localhost:8082/callback}") String redirectUri) {
        this.restTemplate = restTemplate;
        this.authServerUrl = authServerUrl;
        this.resourceServerUrl = resourceServerUrl;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
    }

    public String generateAuthorizationUrl(String state) {
        String codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);

        // TODO session/cache
        CodeVerifierHolder.setCodeVerifier(state, codeVerifier);

        String authorizationUri = UriComponentsBuilder.fromUriString(authServerUrl + "/oauth2/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "read write")
                .queryParam("state", state)
                .queryParam("code_challenge", codeChallenge)
                .queryParam("code_challenge_method", "S256")
                .build()
                .encode()
                .toUriString();

        log.info("Generated authorization URL: {}", authorizationUri);
        return authorizationUri;
    }

    public String exchangeCodeForToken(String code, String state) {
        String codeVerifier = CodeVerifierHolder.getCodeVerifier(state);
        if (codeVerifier == null) {
            throw new RuntimeException("Code verifier not found for state: " + state);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        body.add("code_verifier", codeVerifier);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        log.info("Sending token request to {} with client_id: {}, body: {}",
                authServerUrl + "/oauth2/token", clientId, body);

        try {
            ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                    authServerUrl + "/oauth2/token", request, TokenResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Received token response: {}", response.getBody().getAccessToken());
                return response.getBody().getAccessToken();
            } else {
                log.error("Failed to obtain token: HTTP {}", response.getStatusCode());
                throw new RuntimeException("Failed to obtain token: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            log.error("Token request failed with HTTP {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Token request failed: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Token request failed: {}", e.getMessage());
            throw new RuntimeException("Token request failed: " + e.getMessage(), e);
        } finally {
            CodeVerifierHolder.removeCodeVerifier(state);
        }
    }

    public String accessProtectedResource(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        log.info("Accessing protected resource at {}", resourceServerUrl + "/api/protected");

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    resourceServerUrl + "/api/protected", HttpMethod.GET, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Received resource response: {}", response.getBody());
                return response.getBody();
            } else {
                log.error("Failed to access resource: HTTP {}", response.getStatusCode());
                throw new RuntimeException("Failed to access resource: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Resource access failed: {}", e.getMessage());
            throw new RuntimeException("Resource access failed: " + e.getMessage(), e);
        }
    }

    private String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] codeVerifier = new byte[32];
        secureRandom.nextBytes(codeVerifier);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
    }

    private String generateCodeChallenge(String codeVerifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(codeVerifier.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashed);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate code challenge", e);
        }
    }

    public static class TokenResponse {
        private String access_token;
        private String scope;
        private String token_type;
        private int expires_in;

        public String getAccessToken() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public String getToken_type() {
            return token_type;
        }

        public void setToken_type(String token_type) {
            this.token_type = token_type;
        }

        public int getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(int expires_in) {
            this.expires_in = expires_in;
        }
    }

    private static class CodeVerifierHolder {
        private static final Map<String, String> verifiers = new ConcurrentHashMap<>();

        public static void setCodeVerifier(String state, String codeVerifier) {
            verifiers.put(state, codeVerifier);
        }

        public static String getCodeVerifier(String state) {
            return verifiers.get(state);
        }

        public static void removeCodeVerifier(String state) {
            verifiers.remove(state);
        }
    }

}
