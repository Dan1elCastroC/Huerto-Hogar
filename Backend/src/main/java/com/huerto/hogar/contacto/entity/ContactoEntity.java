package com.huerto.hogar.contacto.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "contactos")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ContactoEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    // Dominios permitidos: @duoc.cl, @profesor.duoc.cl, @gmail.com (se valida en el service)
    @Column(nullable = false, length = 100)
    private String correo;

    @Column(nullable = false, length = 500)
    private String comentario;

    @Column(nullable = false)
    @Builder.Default
    private boolean leido = false;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();
}
