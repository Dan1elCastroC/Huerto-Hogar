package com.huerto.hogar.blog.dto;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class BlogDto {
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede superar 150 caracteres")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 20000, message = "La descripción no puede superar 20000 caracteres")
    private String descripcion;

    @Size(max = 255, message = "La URL de imagen no puede superar 255 caracteres")
    private String imagenUrl;

    // Opcional: solo se usa en PUT para reactivar/desactivar
    private Boolean activo;
}
