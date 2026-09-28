package com.huerto.hogar.categoria.entity;
import lombok.*;
import javax.persistence.*;

@Entity @Table(name = "categorias")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoriaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 80)
    private String nombre;
    @Column(length = 300)
    private String descripcion;
    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;
}
