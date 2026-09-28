package com.huerto.hogar.cupon.entity;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name = "cupones")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CuponEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    // Porcentaje de descuento 1-100
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentaje;

    private LocalDate fechaExpiracion; // null = sin vencimiento

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;
}
