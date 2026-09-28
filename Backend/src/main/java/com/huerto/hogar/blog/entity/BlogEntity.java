package com.huerto.hogar.blog.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "blogs")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BlogEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    // Texto largo (TEXT en MySQL)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 255)
    private String imagenUrl;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();
}
