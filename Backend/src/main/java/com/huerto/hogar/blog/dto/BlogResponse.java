package com.huerto.hogar.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "Artículo del blog")
@Data
public class BlogResponse {
    @Schema(description = "Identificador del artículo", example = "1")
    private Long id;
    @Schema(description = "Título del artículo", example = "Beneficios de los productos orgánicos")
    private String titulo;
    @Schema(description = "Contenido del artículo", example = "Los productos orgánicos se cultivan sin pesticidas.")
    private String descripcion;
    @Schema(description = "URL de la imagen de portada", example = "https://huertohogar.cl/img/blog1.jpg")
    private String imagenUrl;
    @Schema(description = "Indica si el artículo está publicado", example = "true")
    private boolean activo;
    @Schema(description = "Fecha y hora de creación", example = "2026-11-09T10:15:30")
    private LocalDateTime creadoEn;
}
