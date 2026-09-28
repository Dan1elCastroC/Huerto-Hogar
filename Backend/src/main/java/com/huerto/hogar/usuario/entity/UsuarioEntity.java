package com.huerto.hogar.usuario.entity;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity @Table(name = "usuarios")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UsuarioEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 9)
    private String run;
    @Column(nullable = false, length = 50)
    private String nombre;
    @Column(nullable = false, length = 100)
    private String apellidos;
    @Column(nullable = false, unique = true, length = 100)
    private String correo;
    @Column(nullable = false)
    private String contrasena;
    @Column(length = 15)
    private String telefono;
    private LocalDate fechaNacimiento;
    @Column(nullable = false, length = 300)
    private String direccion;
    @Column(nullable = false, length = 100)
    private String region;
    @Column(nullable = false, length = 100)
    private String comuna;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Rol rol = Rol.CLIENTE;
    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();
    private LocalDateTime actualizadoEn;
    @PreUpdate
    public void preUpdate() { this.actualizadoEn = LocalDateTime.now(); }
}
