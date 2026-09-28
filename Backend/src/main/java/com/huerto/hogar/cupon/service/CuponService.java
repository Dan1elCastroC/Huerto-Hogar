package com.huerto.hogar.cupon.service;

import com.huerto.hogar.cupon.entity.CuponEntity;
import com.huerto.hogar.cupon.interfaces.ICuponService;
import com.huerto.hogar.cupon.repository.CuponRepository;
import com.huerto.hogar.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class CuponService implements ICuponService {

    private final CuponRepository repo;

    @Override
    public List<CuponEntity> findAll() {
        return StreamSupport.stream(repo.findAll().spliterator(), false).toList();
    }

    @Override
    public CuponEntity findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Cupón no encontrado: " + id));
    }

    @Override
    public CuponEntity findByCodigo(String codigo) {
        CuponEntity c = repo.findByCodigoAndActivoTrue(codigo.toUpperCase())
            .orElseThrow(() -> new ReglaDeNegocioException("Cupón inválido o expirado: " + codigo));
        // Verificar vencimiento
        if (c.getFechaExpiracion() != null && c.getFechaExpiracion().isBefore(LocalDate.now()))
            throw new ReglaDeNegocioException("El cupón ha expirado");
        return c;
    }

    @Override
    public CuponEntity save(CuponEntity c) {
        if (repo.existsByCodigo(c.getCodigo().toUpperCase()))
            throw new ReglaDeNegocioException("Ya existe un cupón con ese código");
        c.setCodigo(c.getCodigo().toUpperCase());
        return repo.save(c);
    }

    @Override
    public CuponEntity update(Long id, CuponEntity datos) {
        CuponEntity c = findById(id);
        c.setPorcentaje(datos.getPorcentaje());
        c.setFechaExpiracion(datos.getFechaExpiracion());
        c.setActivo(datos.isActivo());
        return repo.save(c);
    }

    @Override
    public void deleteById(Long id) {
        CuponEntity c = findById(id);
        c.setActivo(false);
        repo.save(c);
    }
}
