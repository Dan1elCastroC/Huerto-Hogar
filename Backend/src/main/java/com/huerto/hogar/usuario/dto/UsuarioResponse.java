package com.huerto.hogar.usuario.dto;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data @Builder
public class UsuarioResponse {
    private Long id;
    private String run;
    private String nombre;
    private String apellidos;
    private String correo;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String region;
    private String comuna;
    private String rol;
    private boolean activo;
    private LocalDateTime creadoEn;
}
