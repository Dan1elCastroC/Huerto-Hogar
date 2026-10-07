package com.huerto.hogar.frontend.service;

import com.huerto.hogar.frontend.config.ApiClient;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ReporteService {
    private final ApiClient api;
    public ReporteService(ApiClient api) { this.api = api; }
    public Map resumen() { return api.get("/api/v1/entities/reportes/resumen", Map.class); }
}
