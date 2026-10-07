package com.huerto.hogar.frontend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ApiClient {

    private final RestTemplate restTemplate;
    private final FrontendSession session;
    private final String backendUrl;

    public ApiClient(FrontendSession session, @Value("${backend.url}") String backendUrl) {
        this.restTemplate = new RestTemplate();
        this.session = session;
        this.backendUrl = backendUrl.endsWith("/")
                ? backendUrl.substring(0, backendUrl.length() - 1)
                : backendUrl;
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (session.getToken() != null && !session.getToken().isBlank()) {
            headers.setBearerAuth(session.getToken());
        }

        return headers;
    }

    private String url(String path) {
        if (path == null || path.isBlank()) {
            return backendUrl;
        }
        return path.startsWith("/") ? backendUrl + path : backendUrl + "/" + path;
    }

    public <T> T get(String path, Class<T> type) {
        return restTemplate.exchange(
                url(path),
                HttpMethod.GET,
                new HttpEntity<>(headers()),
                type
        ).getBody();
    }

    public <T> T getList(String path, ParameterizedTypeReference<T> type) {
        return restTemplate.exchange(
                url(path),
                HttpMethod.GET,
                new HttpEntity<>(headers()),
                type
        ).getBody();
    }

    public <T> T post(String path, Object body, Class<T> type) {
        return restTemplate.exchange(
                url(path),
                HttpMethod.POST,
                new HttpEntity<>(body, headers()),
                type
        ).getBody();
    }

    public <T> T put(String path, Object body, Class<T> type) {
        return restTemplate.exchange(
                url(path),
                HttpMethod.PUT,
                new HttpEntity<>(body, headers()),
                type
        ).getBody();
    }

    public <T> T patch(String path, Class<T> type) {
        return restTemplate.exchange(
                url(path),
                HttpMethod.PATCH,
                new HttpEntity<>(headers()),
                type
        ).getBody();
    }

    public Object deleteObject(String path) {
        return restTemplate.exchange(
                url(path),
                HttpMethod.DELETE,
                new HttpEntity<>(headers()),
                Object.class
        ).getBody();
    }

    public void delete(String path) {
        restTemplate.exchange(
                url(path),
                HttpMethod.DELETE,
                new HttpEntity<>(headers()),
                Void.class
        );
    }

    public String errorMessage(Exception e) {
        if (e == null || e.getMessage() == null || e.getMessage().isBlank()) {
            return "Error al comunicarse con el backend";
        }

        return e.getMessage()
                .replace("400 Bad Request:", "")
                .trim();
    }
}
