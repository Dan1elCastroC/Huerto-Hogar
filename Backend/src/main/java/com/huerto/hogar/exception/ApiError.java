package com.huerto.hogar.exception;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
@Data @Builder
public class ApiError {
    private int status;
    private String error;
    private String mensaje;
    private List<String> detalles;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
