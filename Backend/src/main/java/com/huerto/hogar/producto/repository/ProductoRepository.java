package com.huerto.hogar.producto.repository;
import com.huerto.hogar.producto.entity.ProductoEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface ProductoRepository extends CrudRepository<ProductoEntity, Long> {
    List<ProductoEntity> findByActivoTrue();
    Optional<ProductoEntity> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    List<ProductoEntity> findByCategoriaIdAndActivoTrue(Long categoriaId);
    @Query("SELECT p FROM ProductoEntity p WHERE p.activo=true AND (LOWER(p.nombre) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%',:q,'%')))")
    List<ProductoEntity> buscar(@Param("q") String q);
    @Query("SELECT p FROM ProductoEntity p WHERE p.activo=true AND p.stockCritico IS NOT NULL AND p.stock <= p.stockCritico")
    List<ProductoEntity> findStockCritico();
    // Reportes: trae la categoría en la misma consulta (open-in-view=false)
    @Query("SELECT p FROM ProductoEntity p JOIN FETCH p.categoria WHERE p.activo=true AND p.stockCritico IS NOT NULL AND p.stock <= p.stockCritico")
    List<ProductoEntity> findStockCriticoConCategoria();
    long countByActivoTrue();
}
