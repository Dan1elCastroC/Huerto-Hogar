package com.huerto.hogar.blog.mapper;

import com.huerto.hogar.blog.dto.BlogDto;
import com.huerto.hogar.blog.dto.BlogResponse;
import com.huerto.hogar.blog.entity.BlogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/** Conversión automática DTO <-> entity de blog (MapStruct genera la implementación). */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BlogMapper {
    /** Un activo nulo conserva el valor por defecto de la entidad (true). */
    BlogEntity toEntity(BlogDto dto);

    BlogResponse toDto(BlogEntity entity);

    List<BlogResponse> toDtoList(List<BlogEntity> entities);
}
