package com.huerto.hogar.categoria.mapper;

import com.huerto.hogar.categoria.dto.CategoriaDto;
import com.huerto.hogar.categoria.dto.CategoriaResponse;
import com.huerto.hogar.categoria.entity.CategoriaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/** Conversión automática DTO <-> entity de categoría (MapStruct genera la implementación). */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoriaMapper {
    CategoriaEntity toEntity(CategoriaDto dto);

    CategoriaResponse toDto(CategoriaEntity entity);

    List<CategoriaResponse> toDtoList(List<CategoriaEntity> entities);
}
