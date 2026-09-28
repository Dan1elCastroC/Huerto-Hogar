package com.huerto.hogar.pedido.repository;

import com.huerto.hogar.pedido.entity.DetallePedidoEntity;
import com.huerto.hogar.pedido.entity.EstadoPedido;
import com.huerto.hogar.reporte.dto.ProductoVendidoDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetallePedidoRepository extends CrudRepository<DetallePedidoEntity, Long> {

    // Productos más vendidos: suma de cantidades agrupada por producto.
    // Se excluyen los pedidos del estado indicado (CANCELADO). Usar Pageable para limitar (top N).
    @Query("SELECT new com.huerto.hogar.reporte.dto.ProductoVendidoDto(p.id, p.nombre, SUM(d.cantidad)) " +
           "FROM DetallePedidoEntity d JOIN d.producto p JOIN d.pedido ped " +
           "WHERE ped.estado <> :excluido " +
           "GROUP BY p.id, p.nombre " +
           "ORDER BY SUM(d.cantidad) DESC")
    List<ProductoVendidoDto> findMasVendidos(@Param("excluido") EstadoPedido excluido, Pageable pageable);
}
