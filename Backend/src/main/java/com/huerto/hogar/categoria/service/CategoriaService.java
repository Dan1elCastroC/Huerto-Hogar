package com.huerto.hogar.categoria.service;
import com.huerto.hogar.categoria.entity.CategoriaEntity;
import com.huerto.hogar.categoria.interfaces.ICategoriaService;
import com.huerto.hogar.categoria.repository.CategoriaRepository;
import com.huerto.hogar.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CategoriaService implements ICategoriaService {
    private final CategoriaRepository repo;
    @Override public List<CategoriaEntity> findAll() { return repo.findByActivoTrue(); }
    @Override public CategoriaEntity findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada: " + id));
    }
    @Override public CategoriaEntity save(String nombre, String descripcion) {
        if (repo.existsByNombre(nombre)) throw new ReglaDeNegocioException("Ya existe una categoría con ese nombre");
        return repo.save(CategoriaEntity.builder().nombre(nombre).descripcion(descripcion).build());
    }
    @Override public CategoriaEntity update(Long id, String nombre, String descripcion) {
        CategoriaEntity c = findById(id);
        c.setNombre(nombre); c.setDescripcion(descripcion);
        return repo.save(c);
    }
    @Override public void deleteById(Long id) {
        CategoriaEntity c = findById(id);
        c.setActivo(false); repo.save(c);
    }
}
