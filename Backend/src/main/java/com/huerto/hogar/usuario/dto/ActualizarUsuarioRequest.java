package com.huerto.hogar.usuario.dto;
import lombok.Data;
import javax.validation.constraints.*;
@Data
public class ActualizarUsuarioRequest {
    @NotBlank @Size(max=50) private String nombre;
    @NotBlank @Size(max=100) private String apellidos;
    private String telefono;
    private String fechaNacimiento;
    @NotBlank @Size(max=300) private String direccion;
    @NotBlank private String region;
    @NotBlank private String comuna;
}
