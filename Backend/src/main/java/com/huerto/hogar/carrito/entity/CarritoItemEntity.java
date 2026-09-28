package com.huerto.hogar.carrito.entity;

import com.huerto.hogar.producto.entity.ProductoEntity;
import com.huerto.hogar.usuario.entity.UsuarioEntity;
import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "carrito_items",
    uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id","producto_id"}))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CarritoItemEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;

    @Column(nullable = false)
    private Integer cantidad;

    // Precio capturado al momento de agregar (no cambia si el producto cambia de precio)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Builder.Default
    private LocalDateTime agregadoEn = LocalDateTime.now();

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
