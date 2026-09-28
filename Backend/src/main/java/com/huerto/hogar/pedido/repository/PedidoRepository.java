package com.huerto.hogar.pedido.repository;

import com.huerto.hogar.pedido.entity.EstadoPedido;
import com.huerto.hogar.pedido.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {
    // Historial del cliente (más reciente primero)
    List<PedidoEntity> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);
    // Admin: filtrar por estado
    List<PedidoEntity> findByEstadoOrderByCreadoEnDesc(EstadoPedido estado);
    // Admin: todos ordenados
    List<PedidoEntity> findAllByOrderByCreadoEnDesc();
    // Reportes: conteo por estado
    long countByEstado(EstadoPedido estado);
}
