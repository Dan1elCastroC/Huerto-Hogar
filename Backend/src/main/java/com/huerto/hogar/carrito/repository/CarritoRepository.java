package com.huerto.hogar.carrito.repository;

import com.huerto.hogar.carrito.entity.CarritoItemEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoRepository extends CrudRepository<CarritoItemEntity, Long> {
    List<CarritoItemEntity> findByUsuarioId(Long usuarioId);
    Optional<CarritoItemEntity> findByUsuarioIdAndProductoId(Long usuarioId, Long productoId);
    void deleteByUsuarioId(Long usuarioId);
    int countByUsuarioId(Long usuarioId);
}
