package com.huerto.hogar.usuario.dto;
import lombok.Data;
import javax.validation.constraints.*;
@Data
public class RegistroRequest {
    @NotBlank(message = "El RUN es obligatorio") @Size(min=7,max=9,message="RUN entre 7 y 9 caracteres")
    private String run;
    @NotBlank(message = "Nombre obligatorio") @Size(max=50)
    private String nombre;
    @NotBlank(message = "Apellidos obligatorios") @Size(max=100)
    private String apellidos;
    @NotBlank(message = "Correo obligatorio") @Email(message = "Formato de correo inválido") @Size(max=100)
    private String correo;
    @NotBlank(message = "Contraseña obligatoria") @Size(min=4,max=10,message="Contraseña entre 4 y 10 caracteres")
    private String contrasena;
    @NotBlank(message = "Confirmar contraseña obligatorio")
    private String confirmarContrasena;
    private String telefono;
    private String fechaNacimiento;
    @NotBlank(message = "Dirección obligatoria") @Size(max=300)
    private String direccion;
    @NotBlank(message = "Región obligatoria")
    private String region;
    @NotBlank(message = "Comuna obligatoria")
    private String comuna;
    private String rol;
}
