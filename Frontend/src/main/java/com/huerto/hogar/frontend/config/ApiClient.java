package com.huerto.hogar.frontend.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ApiClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final FrontendSession session;
    private final String backendUrl;

    public ApiClient(FrontendSession session, @Value("${backend.url}") String backendUrl) {
        this.session = session;
        this.backendUrl = backendUrl;
    }

    private HttpHeaders headers() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        if (session.getToken() != null && !session.getToken().isBlank()) {
            h.setBearerAuth(session.getToken());
        }
        return h;
    }

    public <T> T get(String path, Class<T> type) {
        return restTemplate.exchange(backendUrl + path, HttpMethod.GET,
                new HttpEntity<>(headers()), type).getBody();
    }

    public <T> T getList(String path, ParameterizedTypeReference<T> type) {
        return restTemplate.exchange(backendUrl + path, HttpMethod.GET,
                new HttpEntity<>(headers()), type).getBody();
    }

    public <T> T post(String path, Object body, Class<T> type) {
        return restTemplate.exchange(backendUrl + path, HttpMethod.POST,
                new HttpEntity<>(body, headers()), type).getBody();
    }

    public <T> T put(String path, Object body, Class<T> type) {
        return restTemplate.exchange(backendUrl + path, HttpMethod.PUT,
                new HttpEntity<>(body, headers()), type).getBody();
    }

    public <T> T patch(String path, Class<T> type) {
        return restTemplate.exchange(backendUrl + path, HttpMethod.PATCH,
                new HttpEntity<>(headers()), type).getBody();
    }

    public <T> T deleteObject(String path) {\n        return restTemplate.exchange(backendUrl + path, HttpMethod.DELETE,\n                new HttpEntity<>(headers()), (Class<T>) Object.class).getBody();\n    }\n\n    public void delete(String path) {
        restTemplate.exchange(backendUrl + path, HttpMethod.DELETE,
                new HttpEntity<>(headers()), Void.class);
    }

    public String errorMessage(Exception e) {
        if (e.getMessage() == null) return "Error al comunicarse con el backend";
        return e.getMessage().replace("400 Bad Request:", "").trim();
    }
}
