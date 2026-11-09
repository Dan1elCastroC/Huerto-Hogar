package com.huerto.hogar.contacto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.*;

@Schema(description = "Mensaje del formulario de contacto")
@Data
public class ContactoDto {
    @Schema(description = "Nombre de quien escribe", example = "María Pérez")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @Schema(description = "Correo de contacto", example = "maria@correo.cl")
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 100, message = "El correo no puede superar 100 caracteres")
    private String correo;

    @Schema(description = "Mensaje o consulta (máx. 500 caracteres)", example = "Quisiera información sobre despachos a regiones.")
    @NotBlank(message = "El comentario es obligatorio")
    @Size(max = 500, message = "El comentario no puede superar 500 caracteres")
    private String comentario;
}
