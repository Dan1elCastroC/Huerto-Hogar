package com.huerto.hogar.categoria.interfaces;
import com.huerto.hogar.categoria.entity.CategoriaEntity;
import java.util.List;
public interface ICategoriaService {
    List<CategoriaEntity> findAll();
    CategoriaEntity findById(Long id);
    CategoriaEntity save(String nombre, String descripcion);
    CategoriaEntity update(Long id, String nombre, String descripcion);
    void deleteById(Long id);
}
