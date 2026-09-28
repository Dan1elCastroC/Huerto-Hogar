package com.huerto.hogar.categoria.repository;
import com.huerto.hogar.categoria.entity.CategoriaEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface CategoriaRepository extends CrudRepository<CategoriaEntity, Long> {
    List<CategoriaEntity> findByActivoTrue();
    boolean existsByNombre(String nombre);
}
