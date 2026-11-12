package com.huerto.hogar.cupon.mapper;

import com.huerto.hogar.cupon.dto.CuponDto;
import com.huerto.hogar.cupon.dto.CuponResponse;
import com.huerto.hogar.cupon.entity.CuponEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import java.time.LocalDate;

import java.util.List;

/** Conversión automática DTO <-> entity de cupón (MapStruct genera la implementación). */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CuponMapper {
    CuponEntity toEntity(CuponDto dto);

    CuponResponse toDto(CuponEntity entity);

    List<CuponResponse> toDtoList(List<CuponEntity> entities);

    /** La fecha llega como texto yyyy-MM-dd; vacío o nulo significa «sin vencimiento». */
    default LocalDate aFecha(String fecha) {
        return fecha == null || fecha.isBlank() ? null : LocalDate.parse(fecha.trim());
    }
}
