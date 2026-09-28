package com.huerto.hogar.producto.entity;
import com.huerto.hogar.categoria.entity.CategoriaEntity;
import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name = "productos")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductoEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 50)
    private String codigo;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(length = 500)
    private String descripcion;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    @Column(nullable = false)
    private Integer stock;
    private Integer stockCritico;
    @Column(length = 255)
    private String imagenUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaEntity categoria;
    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();
    private LocalDateTime actualizadoEn;
    @PreUpdate
    public void preUpdate() { this.actualizadoEn = LocalDateTime.now(); }
    public boolean tieneStockCritico() { return stockCritico != null && stock <= stockCritico; }
}
