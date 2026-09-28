package com.huerto.hogar.pedido.entity;

import com.huerto.hogar.producto.entity.ProductoEntity;
import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name = "detalle_pedidos")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DetallePedidoEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoEntity pedido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;

    @Column(nullable = false)
    private Integer cantidad;

    // Precio al momento de la compra (histórico)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
