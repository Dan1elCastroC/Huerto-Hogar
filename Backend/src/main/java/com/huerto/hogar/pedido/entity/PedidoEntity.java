package com.huerto.hogar.pedido.entity;

import com.huerto.hogar.usuario.entity.UsuarioEntity;
import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "pedidos")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PedidoEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetallePedidoEntity> detalles = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(length = 30)
    private String cuponAplicado;

    // Dirección de entrega (puede diferir de la del usuario)
    @Column(nullable = false, length = 300)
    private String direccionEntrega;

    private LocalDate fechaEntregaDeseada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();

    private LocalDateTime actualizadoEn;

    @PreUpdate
    public void preUpdate() { this.actualizadoEn = LocalDateTime.now(); }
}
