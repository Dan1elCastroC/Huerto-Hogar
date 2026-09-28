package com.huerto.hogar.cupon.repository;

import com.huerto.hogar.cupon.entity.CuponEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CuponRepository extends CrudRepository<CuponEntity, Long> {
    Optional<CuponEntity> findByCodigoAndActivoTrue(String codigo);
    boolean existsByCodigo(String codigo);
}
