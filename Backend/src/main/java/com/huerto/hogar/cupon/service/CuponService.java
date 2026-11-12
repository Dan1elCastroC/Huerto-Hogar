package com.huerto.hogar.cupon.service;

import com.huerto.hogar.cupon.dto.CuponDto;
import com.huerto.hogar.cupon.dto.CuponResponse;
import com.huerto.hogar.cupon.entity.CuponEntity;
import com.huerto.hogar.cupon.interfaces.ICuponService;
import com.huerto.hogar.cupon.mapper.CuponMapper;
import com.huerto.hogar.cupon.repository.CuponRepository;
import com.huerto.hogar.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class CuponService implements ICuponService {

    private final CuponRepository repo;
    private final CuponMapper mapper;

    @Override
    public List<CuponResponse> findAll() {
        return mapper.toDtoList(StreamSupport.stream(repo.findAll().spliterator(), false).toList());
    }

    @Override
    public CuponResponse findById(Long id) {
        return mapper.toDto(getOrThrow(id));
    }

    @Override
    public CuponResponse findByCodigo(String codigo) {
        CuponEntity c = repo.findByCodigoAndActivoTrue(codigo.trim().toUpperCase(Locale.ROOT))
            .orElseThrow(() -> new ReglaDeNegocioException("Cupón inválido o expirado: " + codigo));
        // Verificar vencimiento
        if (c.getFechaExpiracion() != null && c.getFechaExpiracion().isBefore(LocalDate.now()))
            throw new ReglaDeNegocioException("El cupón ha expirado");
        return mapper.toDto(c);
    }

    @Override
    public CuponResponse save(CuponDto dto) {
        CuponEntity c = mapper.toEntity(dto);
        String codigo = c.getCodigo().trim().toUpperCase(Locale.ROOT);
        if (repo.existsByCodigo(codigo))
            throw new ReglaDeNegocioException("Ya existe un cupón con ese código");
        c.setCodigo(codigo);
        return mapper.toDto(repo.save(c));
    }

    @Override
    public CuponResponse update(Long id, CuponDto datos) {
        CuponEntity c = getOrThrow(id);
        CuponEntity nuevos = mapper.toEntity(datos);
        c.setPorcentaje(nuevos.getPorcentaje());
        c.setFechaExpiracion(nuevos.getFechaExpiracion());
        return mapper.toDto(repo.save(c));
    }

    @Override
    public void deleteById(Long id) {
        CuponEntity c = getOrThrow(id);
        c.setActivo(false);
        repo.save(c);
    }

    private CuponEntity getOrThrow(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Cupón no encontrado: " + id));
    }
}
