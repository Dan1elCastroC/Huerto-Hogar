package com.huerto.hogar.contacto.dto;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class ContactoDto {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 100, message = "El correo no puede superar 100 caracteres")
    private String correo;

    @NotBlank(message = "El comentario es obligatorio")
    @Size(max = 500, message = "El comentario no puede superar 500 caracteres")
    private String comentario;
}
