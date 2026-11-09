package com.huerto.hogar.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.*;

@Schema(description = "Datos de un artículo del blog")
@Data
public class BlogDto {
    @Schema(description = "Título del artículo (máx. 150 caracteres)", example = "Beneficios de los productos orgánicos")
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede superar 150 caracteres")
    private String titulo;

    @Schema(description = "Contenido del artículo (máx. 20000 caracteres)", example = "Los productos orgánicos se cultivan sin pesticidas sintéticos...")
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 20000, message = "La descripción no puede superar 20000 caracteres")
    private String descripcion;

    @Schema(description = "URL de la imagen de portada (opcional)", example = "https://huertohogar.cl/img/blog1.jpg")
    @Size(max = 255, message = "La URL de imagen no puede superar 255 caracteres")
    private String imagenUrl;

    // Opcional: solo se usa en PUT para reactivar/desactivar
    @Schema(description = "Solo en PUT: indica si el blog está activo (por defecto true)", example = "true")
    private Boolean activo;
}
